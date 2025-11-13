-- Complex Oracle Procedure with Multiple Features
CREATE OR REPLACE PROCEDURE calculate_bonus (
    p_department_id IN NUMBER,
    p_bonus_percentage IN NUMBER,
    p_effective_date IN DATE DEFAULT SYSDATE
) IS
    v_employee_count NUMBER;
    v_total_bonus NUMBER := 0;
BEGIN
    -- Count employees in department
    SELECT COUNT(*)
    INTO v_employee_count
    FROM employees
    WHERE department_id = p_department_id;
    
    -- Calculate and apply bonuses
    FOR emp IN (
        SELECT employee_id, salary
        FROM employees
        WHERE department_id = p_department_id
        AND hire_date <= p_effective_date
    ) LOOP
        UPDATE employees
        SET bonus = salary * (p_bonus_percentage / 100),
            bonus_date = SYSDATE
        WHERE employee_id = emp.employee_id;
        
        v_total_bonus := v_total_bonus + (emp.salary * p_bonus_percentage / 100);
    END LOOP;
    
    COMMIT;
    
    DBMS_OUTPUT.PUT_LINE('Processed ' || v_employee_count || ' employees');
    DBMS_OUTPUT.PUT_LINE('Total bonus: ' || v_total_bonus);
END calculate_bonus;
