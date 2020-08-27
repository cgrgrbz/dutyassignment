package com.cagrigurbuz.kayseriulasim.dutyassignment.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Employee;

@Component
public class EmployeeListXLSXFileIO {

	public List<Employee> getEmployeeListFromExcelFile(InputStream excelFileStream) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(excelFileStream)) {

			Sheet worksheet = workbook.getSheetAt(0);

			List<Employee> toSave = new ArrayList<>(worksheet.getPhysicalNumberOfRows() - 1);

			for (int i = 1; i <= worksheet.getLastRowNum(); i++) {
				
				Row row = worksheet.getRow(i);

				if (row == null || row.getCell(0) == null) {
					continue;
				}

				Employee employee = new Employee();
				employee.setCode(row.getCell(0).getStringCellValue());
				employee.setName(row.getCell(1).getStringCellValue());
				employee.setRegion(row.getCell(2).getStringCellValue());
				
				toSave.add(employee);
			}
			return toSave;
		}
	}
}
