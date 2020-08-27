package com.cagrigurbuz.kayseriulasim.dutyassignment.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.EmployeeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/employee")
@CrossOrigin
@Validated
@Api(tags = "Emloyee")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		super();
		this.employeeService = employeeService;
	}
	
	@ApiOperation("Get a list of all employees")
	@GetMapping("/")
	public ResponseEntity<List<Employee>> getEmployeeList() {
		return new ResponseEntity<>(employeeService.getEmployeeList(), HttpStatus.OK);
	}
	
	@ApiOperation("Add a new employee")
	@PostMapping("/add")
	public ResponseEntity<Employee> addEmployee(@RequestBody @Valid Employee employee) {
		return new ResponseEntity<>(employeeService.addEmployee(employee), HttpStatus.OK);
	}

	@ApiOperation("Import employees from an Excel file")
	@PostMapping("/import")
	public ResponseEntity<List<Employee>> addEmployeesFromExcelFile(@RequestParam("file") MultipartFile excelDataFile) throws IOException {
		return new ResponseEntity<>(employeeService.importEmployeesFromExcel(excelDataFile.getInputStream()),
				HttpStatus.OK);
	}

}
