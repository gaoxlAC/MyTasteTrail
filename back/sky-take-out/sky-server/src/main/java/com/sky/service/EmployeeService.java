package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

import java.util.List;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 员工新增
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 员工禁用和启用
     * @param id
     * @param status
     */
    void startOrStop(Long id, Integer status);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Employee getById(Long id);

    void update(EmployeeDTO employeeDTO);
}
