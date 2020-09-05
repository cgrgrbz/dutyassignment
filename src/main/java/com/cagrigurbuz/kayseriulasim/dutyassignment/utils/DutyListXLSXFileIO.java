package com.cagrigurbuz.kayseriulasim.dutyassignment.utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;
import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;
import com.cagrigurbuz.kayseriulasim.dutyassignment.service.EmployeeService;

@Component
public class DutyListXLSXFileIO {

	private final EmployeeService employeeService;

	@Autowired
	public DutyListXLSXFileIO(EmployeeService employeeService) {
		super();
		this.employeeService = employeeService;
	}

	public List<Duty> getDutyListFromExcelFile(InputStream excelFileStream) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(excelFileStream)) {

			Sheet worksheet = workbook.getSheetAt(0);

			List<Duty> toSave = new ArrayList<>(worksheet.getPhysicalNumberOfRows() - 1);

			DataFormatter formatter = new DataFormatter();
			
			for (int i = 1; i <= worksheet.getLastRowNum(); i++) {

				Row row = worksheet.getRow(i);

				Duty duty = new Duty();

				String dutyName, employeeCode, employeeName, dutyRegion, dutyType, dutyLoad, dutyStartDate, dutyStartTime,
						dutyEndDate, dutyEndTime;
				
				int dutyPriority, maxMonthlyWorkingHour;
				
				LocalDate startDate, endDate;
				LocalTime startTime, endTime;

				dutyName = formatter.formatCellValue(row.getCell(1));
				dutyRegion = formatter.formatCellValue(row.getCell(5));
				dutyType = formatter.formatCellValue(row.getCell(6));
				dutyLoad = formatter.formatCellValue(row.getCell(7));
				dutyStartDate = formatter.formatCellValue(row.getCell(8));
				dutyStartTime = formatter.formatCellValue(row.getCell(9));
				dutyEndDate = formatter.formatCellValue(row.getCell(10));
				dutyEndTime = formatter.formatCellValue(row.getCell(11));
				dutyPriority = (int) row.getCell(12).getNumericCellValue();
				
				startDate = LocalDate.parse(dutyStartDate);
				startTime = LocalTime.parse(dutyStartTime);
				endDate = LocalDate.parse(dutyEndDate);
				endTime = LocalTime.parse(dutyEndTime);

				duty.setName(dutyName);
				duty.setRegion(dutyRegion);
				duty.setType(dutyType);
				duty.setLoad(Double.parseDouble(dutyLoad));
				duty.setPriority(dutyPriority);
				duty.setStartDateTime(LocalDateTime.of(startDate, startTime));
				duty.setEndDateTime(LocalDateTime.of(endDate, endTime));

				// if it's an old duty (not in current schedule) it's might have an employee
				// so we can will use it for fair duty assignments
				if (row.getCell(2) != null) {
					employeeCode = row.getCell(2).getStringCellValue();
					employeeName = row.getCell(3).getStringCellValue();
					maxMonthlyWorkingHour = (int) row.getCell(13).getNumericCellValue();
										
					Employee employee = employeeService.getEmployeeByCode(employeeCode);
					
					//if (employee == null) {
					//	employee = new Employee(null, employeeCode, employeeName, dutyRegion, maxMonthlyWorkingHour);
					//	employeeService.addEmployee(employee);
					//}
					
					duty.setEmployee(employee);
				}
				
//				boolean isCurrentDuty;
//				isCurrentDuty = (duty.getEmployee() == null) ? true : false;
//				duty.setItCurrentDutyToBeAssigned(isCurrentDuty);
				
				duty.setItCurrentDutyToBeAssigned(false);
							
				toSave.add(duty);
			}
			return toSave;
		}
	}
}
