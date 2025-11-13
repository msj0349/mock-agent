-- Simple Oracle Procedure
CREATE OR REPLACE PROCEDURE update_employee_salary (
    p_employee_id IN NUMBER,
    p_new_salary IN NUMBER
) AS
BEGIN
    UPDATE employees
    SET salary = p_new_salary,
        last_modified = SYSDATE
    WHERE employee_id = p_employee_id;
    
    COMMIT;
END update_employee_salary;
