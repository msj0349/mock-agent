# Deployment Guide

## Overview

This guide covers deployment strategies and procedures for the application across different environments.

## Prerequisites

Before deployment, ensure you have:

- Application JAR file (from build process)
- Java Runtime Environment (JRE) 11 or higher
- Database access (if applicable)
- Required environment variables configured
- SSL certificates (for HTTPS)
- Sufficient system resources

## Deployment Methods

### 1. Standalone JAR Deployment

#### Build the JAR

```bash
# Maven
mvn clean package

# Gradle
./gradlew clean build
```

#### Run the JAR

```bash
# Basic execution
java -jar target/project-name-1.0.0.jar

# With specific profile
java -jar target/project-name-1.0.0.jar --spring.profiles.active=prod

# With custom configuration
java -jar target/project-name-1.0.0.jar --spring.config.location=file:/path/to/config/

# With JVM options
java -Xmx2g -Xms512m -jar target/project-name-1.0.0.jar
```

#### Systemd Service (Linux)

Create `/etc/systemd/system/project-name.service`:

```ini
[Unit]
Description=Project Name Application
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/project-name
ExecStart=/usr/bin/java -jar /opt/project-name/project-name.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Enable and start:

```bash
sudo systemctl daemon-reload
sudo systemctl enable project-name
sudo systemctl start project-name
sudo systemctl status project-name
```

### 2. Docker Deployment

#### Dockerfile

```dockerfile
FROM eclipse-temurin:11-jre-alpine

LABEL maintainer="team@example.com"
LABEL version="1.0.0"

# Create app user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy JAR
COPY target/project-name-*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Build Docker Image

```bash
docker build -t project-name:1.0.0 .
docker tag project-name:1.0.0 project-name:latest
```

#### Run Docker Container

```bash
# Basic run
docker run -d \
  --name project-name \
  -p 8080:8080 \
  project-name:1.0.0

# With environment variables
docker run -d \
  --name project-name \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://db:5432/appdb \
  project-name:1.0.0

# With volume mount
docker run -d \
  --name project-name \
  -p 8080:8080 \
  -v /path/to/config:/app/config \
  -v /path/to/logs:/app/logs \
  project-name:1.0.0
```

### 3. Docker Compose Deployment

#### docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    image: project-name:1.0.0
    container_name: project-name
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:postgresql://db:5432/appdb
      - DATABASE_USERNAME=appuser
      - DATABASE_PASSWORD=${DB_PASSWORD}
    depends_on:
      - db
    volumes:
      - ./config:/app/config
      - ./logs:/app/logs
    networks:
      - app-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 40s

  db:
    image: postgres:14-alpine
    container_name: project-db
    environment:
      - POSTGRES_DB=appdb
      - POSTGRES_USER=appuser
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - db-data:/var/lib/postgresql/data
    networks:
      - app-network
    restart: unless-stopped

  frontend:
    image: project-name-frontend:1.0.0
    container_name: project-name-frontend
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - app
    networks:
      - app-network
    restart: unless-stopped

networks:
  app-network:
    driver: bridge

volumes:
  db-data:
```

#### Deploy with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Update and restart
docker-compose pull
docker-compose up -d
```

### 4. Kubernetes Deployment

#### deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: project-name
  labels:
    app: project-name
spec:
  replicas: 3
  selector:
    matchLabels:
      app: project-name
  template:
    metadata:
      labels:
        app: project-name
    spec:
      containers:
      - name: project-name
        image: project-name:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DATABASE_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: database.url
        - name: DATABASE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: database.password
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: project-name-service
spec:
  selector:
    app: project-name
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

#### Deploy to Kubernetes

```bash
# Apply configurations
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secrets.yaml

# Check status
kubectl get deployments
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/project-name

# Scale deployment
kubectl scale deployment project-name --replicas=5
```

## Environment Configuration

### Development Environment

```yaml
# application-dev.yml
server:
  port: 8080

spring:
  devtools:
    restart:
      enabled: true

logging:
  level:
    root: INFO
    com.project: DEBUG

grammar:
  cache-enabled: false

mapping:
  validation-strict: false
```

### Production Environment

```yaml
# application-prod.yml
server:
  port: 8080
  compression:
    enabled: true

spring:
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

logging:
  level:
    root: WARN
    com.project: INFO
  file:
    name: /var/log/project-name/application.log

grammar:
  cache-enabled: true
  cache-size: 10000

mapping:
  validation-strict: true
  
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
```

## Database Migration

### Flyway Migrations

Place migration scripts in `src/main/resources/db/migration/`:

```
db/migration/
  ├── V1__initial_schema.sql
  ├── V2__add_grammar_table.sql
  └── V3__add_mapping_rules.sql
```

Migrations run automatically on startup.

### Manual Migration

```bash
# Run migrations
mvn flyway:migrate

# Show migration status
mvn flyway:info

# Rollback (if supported)
mvn flyway:undo
```

## SSL/TLS Configuration

### Generate Self-Signed Certificate (Development)

```bash
keytool -genkeypair -alias project-name -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 365
```

### Application Configuration

```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: project-name
```

### Reverse Proxy (Nginx)

```nginx
server {
    listen 80;
    server_name example.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name example.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Monitoring and Health Checks

### Spring Boot Actuator Endpoints

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

Access endpoints:
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Info: `http://localhost:8080/actuator/info`

### Prometheus Integration

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
```

Prometheus scrape config:

```yaml
scrape_configs:
  - job_name: 'project-name'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

## Logging

### Log Configuration

```yaml
logging:
  level:
    root: INFO
    com.project: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/project-name/application.log
    max-size: 10MB
    max-history: 30
```

### Centralized Logging (ELK Stack)

```yaml
# Logstash configuration
logging:
  pattern:
    console: '{"timestamp":"%d{ISO8601}","level":"%p","thread":"%t","class":"%c","message":"%m"}%n'
```

## Backup and Recovery

### Database Backup

```bash
# PostgreSQL backup
pg_dump -h localhost -U appuser -d appdb > backup.sql

# Restore
psql -h localhost -U appuser -d appdb < backup.sql
```

### Configuration Backup

```bash
# Backup configuration
tar -czf config-backup-$(date +%Y%m%d).tar.gz /etc/project-name/

# Restore
tar -xzf config-backup-20240101.tar.gz -C /
```

## Rollback Procedure

### Docker Deployment

```bash
# Rollback to previous version
docker stop project-name
docker rm project-name
docker run -d --name project-name project-name:1.0.0-previous
```

### Kubernetes Deployment

```bash
# Rollback deployment
kubectl rollout undo deployment/project-name

# Rollback to specific revision
kubectl rollout undo deployment/project-name --to-revision=2

# Check rollout status
kubectl rollout status deployment/project-name
```

## Performance Tuning

### JVM Options

```bash
java -Xmx2g \
     -Xms512m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+PrintGCDetails \
     -XX:+PrintGCDateStamps \
     -Xloggc:/var/log/project-name/gc.log \
     -jar project-name.jar
```

### Connection Pool

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## Security Checklist

- [ ] Change default passwords
- [ ] Enable HTTPS
- [ ] Configure firewall rules
- [ ] Set up authentication
- [ ] Enable audit logging
- [ ] Configure CORS properly
- [ ] Use secrets management
- [ ] Keep dependencies updated
- [ ] Enable security headers
- [ ] Configure rate limiting

## Troubleshooting

### Common Issues

1. **Application won't start**
   - Check Java version
   - Verify configuration files
   - Check port availability
   - Review logs

2. **Database connection errors**
   - Verify database is running
   - Check connection string
   - Verify credentials
   - Check network connectivity

3. **Out of memory errors**
   - Increase heap size
   - Check for memory leaks
   - Review GC logs
   - Optimize queries

4. **Performance issues**
   - Check database queries
   - Review cache configuration
   - Monitor thread pools
   - Analyze profiling data

## Support

For deployment issues:
- Check logs: `/var/log/project-name/`
- Review documentation: `docs/`
- Contact DevOps team
- Create support ticket
