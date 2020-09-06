package com.cagrigurbuz.kayseriulasim.dutyassignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
	
    @Query("select e from Employee e " +
            "order by e.id")
    List<Employee> findAll();
	    
    @Query("select e from Employee e " +
            "where e.id = :employeeId")
    Employee findEmployeeById(@Param("employeeId") Long id);
    
    @Query("select e from Employee e where e.code = :code")
    Employee findEmployeeByCode(@Param("code") String code);
}