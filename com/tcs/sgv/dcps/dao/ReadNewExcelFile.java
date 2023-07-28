package com.tcs.sgv.dcps.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadNewExcelFile {
	public static List parseNewExcel(String fileName2)
	    {
	 String filename = "C:\\Users\\s.gorakhanath\\Downloads\\book1.xlsx";
     List<List<XSSFCell>> sheetData = new ArrayList<>();

     try (FileInputStream fis = new FileInputStream(filename)) {
     	
         XSSFWorkbook workbook = new XSSFWorkbook(fis);
         XSSFSheet sheet = workbook.getSheetAt(0);
         Iterator<Row> rows = sheet.rowIterator();
         while (rows.hasNext()) {
             XSSFRow row = (XSSFRow) rows.next();
             Iterator<Cell> cells = row.cellIterator();

             List<XSSFCell> data = new ArrayList<>();
             while (cells.hasNext()) {
                 XSSFCell cell = (XSSFCell) cells.next();
                 data.add(cell);
             }
             sheetData.add(data);
         }
     } catch (IOException e) {
         e.printStackTrace();
     }

  return sheetData;
}
}


