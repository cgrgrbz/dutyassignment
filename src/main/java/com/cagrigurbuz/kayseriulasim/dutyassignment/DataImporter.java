package com.cagrigurbuz.kayseriulasim.dutyassignment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cagrigurbuz.kayseriulasim.dutyassignment.service.DutyService;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.EmployeeService;

//This Class is just for test purposes, it's load the duty and employee data from the given XLSX file

@Component
public class DataImporter {
	
	@Autowired
	private final DutyService dutyService;
	
	@Autowired
	private final EmployeeService employeeService;
	
	public DataImporter(DutyService dutyService, EmployeeService employeeService) {
		super();
		this.dutyService = dutyService;
		this.employeeService = employeeService;
	}
	
	@PostConstruct
	public void importSampleDataAtStartup() {
		
		try {
			InputStream dutyList = new FileInputStream("src\\main\\resources\\DutyList.xlsx");	
			dutyService.importDutyFromExcel(dutyList);
			
			InputStream employeeList = new FileInputStream("src\\main\\resources\\EmployeeList.xlsx");	
			employeeService.importEmployeesFromExcel(employeeList);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
