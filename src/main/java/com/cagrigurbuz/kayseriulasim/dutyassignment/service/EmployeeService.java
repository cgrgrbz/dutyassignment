package com.cagrigurbuz.kayseriulasim.dutyassignment.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cagrigurbuz.kayseriulasim.dutyassignment.repository.EmployeeRepository;
import com.cagrigurbuz.kayseriulasim.dutyassignment.utils.EmployeeListXLSXFileIO;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;
	
	private final EmployeeListXLSXFileIO employeeListXLSXFileIO;
	
	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository, EmployeeListXLSXFileIO employeeListXLSXFileIO ) {
		super();
		this.employeeRepository = employeeRepository;
		this.employeeListXLSXFileIO = employeeListXLSXFileIO;
	}
	
    @Transactional
    public List<Employee> getEmployeeList() {
        return employeeRepository.findAll();
    }
    
    @Transactional
    public Employee getEmployeeByCode(String employeeCode) {      
        return employeeRepository.findEmployeeByCode(employeeCode);
    }

    @Transactional
    public Employee addEmployee(Employee employee) {
    	return employeeRepository.save(employee);
    }
    
    @Transactional
    public Employee updateEmployee(Employee employee) {
        
    	Employee newEmployee = employee;

        Employee oldEmployee = employeeRepository
                .findById(newEmployee.getId())
                .orElseThrow(() -> new EntityNotFoundException("Employee entity with ID (" + newEmployee.getId() + ") not found."));

        oldEmployee.setName(newEmployee.getName());
        oldEmployee.setRegion(newEmployee.getRegion());
        oldEmployee.setMaxMonthlyWorkingHour(newEmployee.getMaxMonthlyWorkingHour());
        
        return employeeRepository.save(oldEmployee);
    }
    
    @Transactional
    public List<Employee> importEmployeesFromExcel(InputStream excelInputStream) throws IOException {
    	
        List<Employee> excelEmployeeList = employeeListXLSXFileIO.getEmployeeListFromExcelFile(excelInputStream);

        final Set<String> addedEmployeeSet = new HashSet<>();
        
        excelEmployeeList.stream().flatMap(employee -> {
            if (addedEmployeeSet.contains(employee.getCode().toLowerCase())) {
                // Duplicate Employee; already in the stream
                return Stream.empty();
            }
            // Add employee to the stream
            addedEmployeeSet.add(employee.getName().toLowerCase());
            return Stream.of(employee);
        }).forEach(employee -> {
            Employee oldEmployee = employeeRepository.findEmployeeById(employee.getId());
            if (oldEmployee != null) {
                employee.setId(oldEmployee.getId());
                updateEmployee(employee);
            } else {
                addEmployee(employee);
            }
        });
        
        return getEmployeeList();
    }
    
 }