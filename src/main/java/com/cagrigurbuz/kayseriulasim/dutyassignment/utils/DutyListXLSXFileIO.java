package com.cagrigurbuz.kayseriulasim.dutyassignment.utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;

@Component
public class DutyListXLSXFileIO {
	
	//TODO 
	//Assuming that all the cell types as we wanted in the XLSX file, so it's may (will) have problems in large datasets..
	//What to do? Read all cells as string then convert what ever you want..

	public List<Duty> getDutyListFromExcelFile(InputStream excelFileStream) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(excelFileStream)) {

			Sheet worksheet = workbook.getSheetAt(0);

			List<Duty> toSave = new ArrayList<>(worksheet.getPhysicalNumberOfRows() - 1);

			for (int i = 1; i <= worksheet.getLastRowNum(); i++) {

				Row row = worksheet.getRow(i);

				if (row == null || row.getCell(0) == null) {
					continue;
				}

				Duty duty = new Duty();

				LocalDate startDate, endDate;
				LocalTime startTime, endTime;

				duty.setName(row.getCell(0).getStringCellValue());
				duty.setRegion(row.getCell(1).getStringCellValue());

				startDate = LocalDate.parse(row.getCell(2).getStringCellValue());
				startTime = LocalTime.parse(row.getCell(3).getStringCellValue());

				endDate = LocalDate.parse(row.getCell(4).getStringCellValue());
				endTime = LocalTime.parse(row.getCell(5).getStringCellValue());

				duty.setStartDateTime(LocalDateTime.of(startDate, startTime));
				duty.setEndDateTime(LocalDateTime.of(endDate, endTime));
				
				duty.setLoad(row.getCell(6).getNumericCellValue());

				duty.setType(row.getCell(7).getStringCellValue());

				toSave.add(duty);
			}
			
			return toSave;
			
		}
	}

}
