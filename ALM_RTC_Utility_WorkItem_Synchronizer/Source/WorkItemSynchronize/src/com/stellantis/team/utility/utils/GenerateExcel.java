package com.stellantis.team.utility.utils;

import java.io.File;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ibm.team.workitem.common.model.IWorkItem;

public enum GenerateExcel {
	INSTANCE;
	
	public void writeToFile(String filePath, List<IWorkItem> lstWorkItem){
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			sheet.createRow(0);
			createHeader(sheet);
			
			int rowCount = 1;
			createContent(sheet, lstWorkItem, rowCount);
			
			File file = new File(filePath);
			workbook.write(file);
			workbook.close();
			
		} catch (Exception e) {
			CustomLogger.logException(e);
		}
	}
	
	private void createHeader(HSSFSheet sheet) {
		sheet.getRow(0).createCell(0).setCellValue("Id");
		sheet.getRow(0).createCell(1).setCellValue("WorkItem Id");
		sheet.getRow(0).createCell(2).setCellValue("WorkItem Summary");
	}
	
	private void createContent(HSSFSheet sheet, List<IWorkItem> lstWorkItem, int rowCount){
		for (IWorkItem iWorkItem : lstWorkItem) {
			sheet.createRow(rowCount);
			sheet.getRow(rowCount).createCell(0).setCellValue(rowCount);
			sheet.getRow(rowCount).createCell(1).setCellValue(iWorkItem.getId());
			sheet.getRow(rowCount).createCell(2).setCellValue(iWorkItem.getHTMLSummary().getPlainText());
			rowCount++;
		}
	}
}
