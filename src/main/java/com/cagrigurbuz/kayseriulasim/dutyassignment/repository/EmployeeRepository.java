package com.cagrigurbuz.kayseriulasim.dutyassignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
	
    @Query("select e from Employee e " +
            "order by e.id")
    List<Employee> findAll();
	    
    @Query("select e from Employee e " +
            "where e.id = :employeeId")
    Employee findEmployeeById(@Param("employeeId") Long id);
    
    @Query("select e from Employee e " +
            "where e.code = :employeeCode")
    Employee findEmployeeByCode(@Param("employeeCode") String c);
}