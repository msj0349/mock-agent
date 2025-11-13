-- Expected MySQL Procedure
DELIMITER $$
CREATE PROCEDURE update_employee_salary (
    IN p_employee_id DECIMAL,
    IN p_new_salary DECIMAL
)
BEGIN
    UPDATE employees
    SET salary = p_new_salary,
        last_modified = NOW()
    WHERE employee_id = p_employee_id;
    
    COMMIT;
END$$
DELIMITER ;
