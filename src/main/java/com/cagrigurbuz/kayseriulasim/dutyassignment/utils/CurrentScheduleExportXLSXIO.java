package com.cagrigurbuz.kayseriulasim.dutyassignment.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cagrigurbuz.kayseriulasim.dutyassignment.domain.Duty;

public class CurrentScheduleExportXLSXIO {

	private CurrentScheduleExportXLSXIO() {

	}

	public static byte[] getExcelCurrentDutyFile(List<Duty> currentScheduleDutyList) throws IOException {

		try (Workbook workbook = new XSSFWorkbook()) {
			
				Sheet sheet = workbook.createSheet("Current Schedule");
				Row headerRow = sheet.createRow(0);
				Cell headerCell = headerRow.createCell(0);
				headerCell.setCellValue("dutyDate");

				headerCell = headerRow.createCell(1);
				headerCell.setCellValue("dutyName");

				headerCell = headerRow.createCell(2);
				headerCell.setCellValue("employeeCode");

				headerCell = headerRow.createCell(3);
				headerCell.setCellValue("employeeName");
				
			for (Duty duty : currentScheduleDutyList) {

				int rowNumber = 1;

				Row dutyRow = sheet.createRow(rowNumber);

				Cell dateCell = dutyRow.createCell(0);
				dateCell.setCellValue(duty.getStartDate());

				dateCell = dutyRow.createCell(1);
				dateCell.setCellValue(duty.getName());

				dateCell = dutyRow.createCell(2);
				dateCell.setCellValue(duty.getEmployee().getCode());

				
				dateCell = dutyRow.createCell(3);
				dateCell.setCellValue(duty.getEmployee().getName());
				
				rowNumber++;
			}

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				workbook.write(outputStream);
				return outputStream.toByteArray();
			}
			
		}
	}

}
