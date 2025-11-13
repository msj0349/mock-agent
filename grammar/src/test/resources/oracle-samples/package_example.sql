-- Oracle Package Example
CREATE OR REPLACE PACKAGE employee_mgmt AS
    -- Package specification
    FUNCTION get_employee_count(p_dept_id NUMBER) RETURN NUMBER;
    PROCEDURE update_salary(p_emp_id NUMBER, p_amount NUMBER);
END employee_mgmt;
/

CREATE OR REPLACE PACKAGE BODY employee_mgmt AS
    -- Get employee count for a department
    FUNCTION get_employee_count(p_dept_id NUMBER) RETURN NUMBER IS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_count
        FROM employees
        WHERE department_id = p_dept_id
        AND hire_date <= SYSDATE;
        
        RETURN v_count;
    END get_employee_count;
    
    -- Update employee salary
    PROCEDURE update_salary(p_emp_id NUMBER, p_amount NUMBER) IS
        v_current_salary NUMBER;
    BEGIN
        SELECT salary
        INTO v_current_salary
        FROM employees
        WHERE employee_id = p_emp_id;
        
        UPDATE employees
        SET salary = v_current_salary + p_amount,
            last_modified = SYSDATE
        WHERE employee_id = p_emp_id;
        
        COMMIT;
    END update_salary;
    
END employee_mgmt;
/
