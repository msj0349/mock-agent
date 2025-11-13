-- Oracle Function with DECODE
CREATE OR REPLACE FUNCTION get_employee_status (
    p_employee_id IN NUMBER
) RETURN VARCHAR2 AS
    v_status VARCHAR2(20);
BEGIN
    SELECT DECODE(active, 1, 'ACTIVE', 'INACTIVE')
    INTO v_status
    FROM employees
    WHERE employee_id = p_employee_id;
    
    RETURN v_status;
END get_employee_status;
