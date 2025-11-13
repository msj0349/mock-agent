# Frontend Usage Guide

## Overview

This guide explains how to use and integrate with the frontend interface of the application. The frontend provides a modern, responsive web interface for interacting with the grammar parsing and mapping functionality.

## Getting Started

### Installation

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Or using yarn
yarn install
```

### Development Server

```bash
# Start development server
npm run dev

# With specific port
npm run dev -- --port 3000

# With host binding
npm run dev -- --host 0.0.0.0
```

The application will be available at `http://localhost:3000`

### Production Build

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

## Application Structure

```
frontend/
├── src/
│   ├── components/      # Reusable UI components
│   │   ├── common/      # Common components (Button, Input, etc.)
│   │   ├── grammar/     # Grammar-related components
│   │   ├── mapping/     # Mapping-related components
│   │   └── layout/      # Layout components
│   ├── pages/           # Page components
│   ├── services/        # API service layer
│   ├── stores/          # State management
│   ├── utils/           # Utility functions
│   ├── styles/          # Global styles
│   ├── types/           # TypeScript type definitions
│   ├── App.tsx          # Root component
│   └── main.tsx         # Application entry point
├── public/              # Static assets
├── tests/               # Test files
└── package.json         # Dependencies and scripts
```

## Core Features

### 1. Grammar Editor

The grammar editor allows users to create, edit, and test grammar definitions.

#### Using the Grammar Editor

```typescript
// Example: Programmatically interacting with Grammar Editor
import { GrammarEditor } from '@/components/grammar/GrammarEditor';

function MyComponent() {
  const [grammar, setGrammar] = useState('');
  const [parseResult, setParseResult] = useState(null);

  const handleParse = async (grammarText: string) => {
    try {
      const result = await grammarService.parse(grammarText);
      setParseResult(result);
    } catch (error) {
      console.error('Parse error:', error);
    }
  };

  return (
    <GrammarEditor
      value={grammar}
      onChange={setGrammar}
      onParse={handleParse}
      parseResult={parseResult}
    />
  );
}
```

#### Grammar Editor Features

- Syntax highlighting
- Auto-completion
- Error detection
- Real-time validation
- AST visualization
- Export/import grammar definitions

### 2. Mapping Designer

Visual interface for creating and testing mapping rules.

#### Using the Mapping Designer

```typescript
import { MappingDesigner } from '@/components/mapping/MappingDesigner';

function MappingPage() {
  const [mappingRules, setMappingRules] = useState<MappingRule[]>([]);
  
  const handleExecute = async (input: any) => {
    const result = await mappingService.execute(mappingRules, input);
    return result;
  };

  return (
    <MappingDesigner
      rules={mappingRules}
      onRulesChange={setMappingRules}
      onExecute={handleExecute}
    />
  );
}
```

#### Mapping Designer Features

- Drag-and-drop field mapping
- Visual transformation builder
- Real-time preview
- Test data input
- Rule validation
- Import/export mappings

### 3. API Tester

Interactive API testing interface.

```typescript
import { ApiTester } from '@/components/api/ApiTester';

function ApiTestPage() {
  return (
    <ApiTester
      endpoints={apiEndpoints}
      defaultAuth={authToken}
    />
  );
}
```

## API Integration

### Service Layer

The frontend uses a service layer to interact with the backend API.

#### Grammar Service

```typescript
// src/services/grammarService.ts
import { apiClient } from './apiClient';
import type { Grammar, ParseResult, ValidationResult } from '@/types';

export const grammarService = {
  /**
   * Parse a grammar definition
   */
  async parse(grammar: string, options?: ParseOptions): Promise<ParseResult> {
    const response = await apiClient.post('/api/v1/grammar/parse', {
      grammar,
      options,
    });
    return response.data;
  },

  /**
   * Validate a grammar definition
   */
  async validate(grammar: string): Promise<ValidationResult> {
    const response = await apiClient.post('/api/v1/grammar/validate', {
      grammar,
    });
    return response.data;
  },

  /**
   * Get all saved grammars
   */
  async list(page = 0, size = 20): Promise<PaginatedResult<Grammar>> {
    const response = await apiClient.get('/api/v1/grammar', {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Get a specific grammar by ID
   */
  async get(id: string): Promise<Grammar> {
    const response = await apiClient.get(`/api/v1/grammar/${id}`);
    return response.data;
  },

  /**
   * Create a new grammar
   */
  async create(grammar: Omit<Grammar, 'id'>): Promise<Grammar> {
    const response = await apiClient.post('/api/v1/grammar', grammar);
    return response.data;
  },

  /**
   * Update an existing grammar
   */
  async update(id: string, grammar: Partial<Grammar>): Promise<Grammar> {
    const response = await apiClient.put(`/api/v1/grammar/${id}`, grammar);
    return response.data;
  },

  /**
   * Delete a grammar
   */
  async delete(id: string): Promise<void> {
    await apiClient.delete(`/api/v1/grammar/${id}`);
  },
};
```

#### Mapping Service

```typescript
// src/services/mappingService.ts
import { apiClient } from './apiClient';
import type { MappingRule, MappingResult } from '@/types';

export const mappingService = {
  /**
   * Execute a mapping rule
   */
  async execute(
    ruleId: string,
    inputData: any,
    context?: any
  ): Promise<MappingResult> {
    const response = await apiClient.post('/api/v1/mapping/execute', {
      ruleId,
      inputData,
      context,
    });
    return response.data;
  },

  /**
   * Create a new mapping rule
   */
  async createRule(rule: Omit<MappingRule, 'id'>): Promise<MappingRule> {
    const response = await apiClient.post('/api/v1/mapping/rules', rule);
    return response.data;
  },

  /**
   * Get all mapping rules
   */
  async listRules(page = 0, size = 20): Promise<PaginatedResult<MappingRule>> {
    const response = await apiClient.get('/api/v1/mapping/rules', {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Get a specific mapping rule
   */
  async getRule(id: string): Promise<MappingRule> {
    const response = await apiClient.get(`/api/v1/mapping/rules/${id}`);
    return response.data;
  },
};
```

#### API Client

```typescript
// src/services/apiClient.ts
import axios from 'axios';
import { authStore } from '@/stores/authStore';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for adding auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = authStore.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized
      authStore.logout();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

## State Management

The application uses a state management solution (e.g., Zustand, Redux, or Pinia) for managing global state.

### Auth Store

```typescript
// src/stores/authStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  getToken: () => string | null;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      user: null,
      isAuthenticated: false,

      login: async (username: string, password: string) => {
        const response = await apiClient.post('/api/v1/auth/login', {
          username,
          password,
        });
        
        const { accessToken, user } = response.data;
        
        set({
          token: accessToken,
          user,
          isAuthenticated: true,
        });
      },

      logout: () => {
        set({
          token: null,
          user: null,
          isAuthenticated: false,
        });
      },

      getToken: () => get().token,
    }),
    {
      name: 'auth-storage',
    }
  )
);
```

### Grammar Store

```typescript
// src/stores/grammarStore.ts
import { create } from 'zustand';
import { grammarService } from '@/services/grammarService';

interface GrammarState {
  grammars: Grammar[];
  currentGrammar: Grammar | null;
  loading: boolean;
  error: string | null;
  
  fetchGrammars: () => Promise<void>;
  selectGrammar: (id: string) => Promise<void>;
  createGrammar: (grammar: Omit<Grammar, 'id'>) => Promise<void>;
  updateGrammar: (id: string, grammar: Partial<Grammar>) => Promise<void>;
  deleteGrammar: (id: string) => Promise<void>;
}

export const useGrammarStore = create<GrammarState>((set, get) => ({
  grammars: [],
  currentGrammar: null,
  loading: false,
  error: null,

  fetchGrammars: async () => {
    set({ loading: true, error: null });
    try {
      const result = await grammarService.list();
      set({ grammars: result.content, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  selectGrammar: async (id: string) => {
    set({ loading: true, error: null });
    try {
      const grammar = await grammarService.get(id);
      set({ currentGrammar: grammar, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  createGrammar: async (grammar: Omit<Grammar, 'id'>) => {
    set({ loading: true, error: null });
    try {
      const newGrammar = await grammarService.create(grammar);
      set((state) => ({
        grammars: [...state.grammars, newGrammar],
        currentGrammar: newGrammar,
        loading: false,
      }));
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  updateGrammar: async (id: string, grammar: Partial<Grammar>) => {
    set({ loading: true, error: null });
    try {
      const updated = await grammarService.update(id, grammar);
      set((state) => ({
        grammars: state.grammars.map((g) => (g.id === id ? updated : g)),
        currentGrammar: updated,
        loading: false,
      }));
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  deleteGrammar: async (id: string) => {
    set({ loading: true, error: null });
    try {
      await grammarService.delete(id);
      set((state) => ({
        grammars: state.grammars.filter((g) => g.id !== id),
        currentGrammar: null,
        loading: false,
      }));
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
}));
```

## Component Library

### Common Components

#### Button

```typescript
// src/components/common/Button.tsx
import React from 'react';
import classNames from 'classnames';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  className,
  disabled,
  ...props
}) => {
  return (
    <button
      className={classNames(
        'btn',
        `btn-${variant}`,
        `btn-${size}`,
        { 'btn-loading': loading },
        className
      )}
      disabled={disabled || loading}
      {...props}
    >
      {loading && <span className="spinner" />}
      {children}
    </button>
  );
};
```

#### Input

```typescript
// src/components/common/Input.tsx
import React, { forwardRef } from 'react';
import classNames from 'classnames';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  hint?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, hint, className, ...props }, ref) => {
    return (
      <div className="input-group">
        {label && <label className="input-label">{label}</label>}
        <input
          ref={ref}
          className={classNames('input', { 'input-error': error }, className)}
          {...props}
        />
        {error && <span className="input-error-message">{error}</span>}
        {hint && !error && <span className="input-hint">{hint}</span>}
      </div>
    );
  }
);
```

### Custom Hooks

#### useApi

```typescript
// src/hooks/useApi.ts
import { useState, useCallback } from 'react';

interface UseApiState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}

export function useApi<T>(
  apiFunction: (...args: any[]) => Promise<T>
) {
  const [state, setState] = useState<UseApiState<T>>({
    data: null,
    loading: false,
    error: null,
  });

  const execute = useCallback(
    async (...args: any[]) => {
      setState({ data: null, loading: true, error: null });
      try {
        const result = await apiFunction(...args);
        setState({ data: result, loading: false, error: null });
        return result;
      } catch (error) {
        setState({ data: null, loading: false, error: error.message });
        throw error;
      }
    },
    [apiFunction]
  );

  return { ...state, execute };
}
```

#### useDebounce

```typescript
// src/hooks/useDebounce.ts
import { useState, useEffect } from 'react';

export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}
```

## Styling

### CSS Modules

```typescript
// Component.module.css
.container {
  padding: 20px;
  background-color: #fff;
}

.title {
  font-size: 24px;
  font-weight: bold;
}

// Component.tsx
import styles from './Component.module.css';

export const Component = () => {
  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Title</h1>
    </div>
  );
};
```

### Tailwind CSS (if used)

```typescript
export const Component = () => {
  return (
    <div className="p-5 bg-white">
      <h1 className="text-2xl font-bold">Title</h1>
    </div>
  );
};
```

## Testing

See [TESTING.md](./TESTING.md) for comprehensive testing guide.

### Component Testing Example

```typescript
// Button.test.tsx
import { render, fireEvent, screen } from '@testing-library/react';
import { Button } from './Button';

describe('Button', () => {
  it('renders correctly', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });

  it('calls onClick when clicked', () => {
    const onClick = jest.fn();
    render(<Button onClick={onClick}>Click me</Button>);
    
    fireEvent.click(screen.getByText('Click me'));
    expect(onClick).toHaveBeenCalledTimes(1);
  });

  it('is disabled when loading', () => {
    render(<Button loading>Click me</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });
});
```

## Configuration

### Environment Variables

```env
# .env.development
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=Grammar Parser Dev
VITE_ENABLE_DEBUG=true

# .env.production
VITE_API_BASE_URL=https://api.example.com
VITE_APP_TITLE=Grammar Parser
VITE_ENABLE_DEBUG=false
```

### TypeScript Configuration

```json
// tsconfig.json
{
  "compilerOptions": {
    "target": "ES2020",
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "jsx": "react-jsx",
    "strict": true,
    "moduleResolution": "node",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "allowSyntheticDefaultImports": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src"],
  "exclude": ["node_modules"]
}
```

## Best Practices

1. **Component Organization**: Keep components small and focused
2. **Type Safety**: Use TypeScript for type checking
3. **Error Handling**: Handle errors gracefully with user-friendly messages
4. **Loading States**: Show loading indicators for async operations
5. **Accessibility**: Follow WCAG guidelines
6. **Performance**: Optimize re-renders with React.memo, useMemo, useCallback
7. **Code Splitting**: Use lazy loading for routes and large components
8. **Testing**: Write comprehensive tests for components and utilities
9. **Documentation**: Document complex components and utilities
10. **Consistency**: Follow established patterns and conventions
