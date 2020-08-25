package com.cagrigurbuz.kayseriulasim.dutyassignment.utils;

import java.io.IOException;
import java.io.InputStream;
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
				
				//Name	Region	startDayIndex	startTime	endDayIndex	endTime
				
				
				duty.setName(row.getCell(0).getStringCellValue());
				duty.setRegion(row.getCell(1).getStringCellValue());
				
				duty.setStartDayIndex((int) row.getCell(2).getNumericCellValue());		
				
				duty.setStartTime(LocalTime.parse(row.getCell(3).getStringCellValue()));
				
				duty.setEndDayIndex((int) row.getCell(4).getNumericCellValue());
				
				duty.setEndTime(LocalTime.parse(row.getCell(5).getStringCellValue()));
				
				toSave.add(duty);
			}
			return toSave;
		}
	}

}
