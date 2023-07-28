package com.tcs.sgv.common.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.CellView;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.sgv.common.constant.DBConstants;

public class ExcelExportHelper 
{
	private static final Log logger = LogFactory.getLog(ExcelExportHelper.class);
	  static NumberFormat nf =  NumberFormat.getInstance();
	  static SimpleDateFormat oDateFormat ; 
	  static SimpleDateFormat oSimpleFormat = new SimpleDateFormat("dd/MM/yyyy");
	  static
	  {
	    nf.setMinimumFractionDigits(2);
	    nf.setMaximumFractionDigits(2);
	    nf.setGroupingUsed(false);
	    oDateFormat =  new SimpleDateFormat("dd/MM/yyyy");
	  }
    ResourceBundle localStringsBundle = null;
    Map objFormatter = new HashMap();
    
    public static void exportToExcelFile(HttpServletRequest request,HttpServletResponse response,byte[] data, String lStrFileName) throws IOException
    {
    	try
		{
    		request.setAttribute("ContentType","application/ms-excel;charset=UTF-8");
			request.setAttribute( "ExportedReportBytesArray",data);
			response.addHeader("Content-disposition", "attachment; filename=" + lStrFileName + ".xls");
		    response.setCharacterEncoding("UTF-8");
		}catch(Exception ex)
		{
			logger.error("Exception occurred in: \n" + ex,ex);
		}
    }
    public static void exportToExcelFile(HttpServletRequest request,HttpServletResponse response,String output) throws IOException
    {
     	request.setAttribute( "ContentType", "application/ms-excel" );
    	
  //  String fileName = "ExcelReport." + ReportDAOImpl.ymd.format(new Date()) + ".xls";
		try
		{
		FileInputStream objFileInpStr = new FileInputStream(output);
		BufferedInputStream objBuffInpStr =  new BufferedInputStream(objFileInpStr);
		byte[] data = new byte[(int)new File(output).length()];
		objBuffInpStr.read(data); 
		response.setContentType("application/ms-excel;charset=UTF-8");
		request.setAttribute( "ExportedReportBytesArray",data);
	    response.addHeader("Content-disposition", "attachment; filename=test.xls");
	    response.setCharacterEncoding("UTF-8");
		}catch(Exception ex)
		{
			logger.error("Exception occurred in: \n" + ex,ex);
		}
    }

  /*  public File getExcelReport(ArrayList arrOuter, ColumnVo[] columnHeading , String Header, String strParameterString ,
    		String Footer ,String datetime,String path) throws Exception
    {

    	  int nrowCount = 0;
    	     int ncolCount = 1;
    	File reportFile = new File(path + "/ExcelReport." + ReportDAOImpl.ymd.format(new Date())+ ".xls");	
    	WorkbookSettings lbookSettings = new WorkbookSettings();
    	WritableWorkbook lworkbook = Workbook.createWorkbook(reportFile,lbookSettings );
    	WritableSheet lwriSheet = lworkbook.createSheet("ExcelReport." + ReportDAOImpl.ymd.format(new Date()) , 0 );
    	WritableCell wc = null;
    	WritableCell hc = null;
    	Border showBorder = Border.ALL;
    	Label lTitle;
    	Label lfooter;
    	byte[] data = null;
    	try
    	{
    		WritableFont lReportTitleFont = new WritableFont( WritableFont.TAHOMA,
    				WritableFont.DEFAULT_POINT_SIZE + 2 , WritableFont.BOLD, false,
    				UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

    		WritableFont lReportSubTitleFont = new WritableFont( WritableFont.TAHOMA,
    				WritableFont.DEFAULT_POINT_SIZE + 1, WritableFont.BOLD, false,
    				UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

    		WritableFont lLblFont = new WritableFont( WritableFont.TAHOMA,
    				WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false,
    				UnderlineStyle.NO_UNDERLINE, Colour.BLACK );

    		WritableCellFormat lTitleFmt = new WritableCellFormat( lReportTitleFont );
    		lTitleFmt.setWrap( true );
    		lTitleFmt.setBorder( showBorder, BorderLineStyle.THIN );
    		lTitleFmt.setAlignment( Alignment.CENTRE );
    		lTitleFmt.setVerticalAlignment( VerticalAlignment.CENTRE );
    		lTitleFmt.setBackground( Colour.WHITE);

    		WritableCellFormat lSubTitleFmt = new WritableCellFormat( lReportSubTitleFont );
    		lSubTitleFmt.setWrap( true );
    		lSubTitleFmt.setBorder( showBorder, BorderLineStyle.THIN );
    		lSubTitleFmt.setAlignment( Alignment.CENTRE );
    		lSubTitleFmt.setVerticalAlignment( VerticalAlignment.CENTRE );
    		lSubTitleFmt.setBackground( Colour.ROSE);


    		WritableCellFormat lHdrFmt = new WritableCellFormat( lLblFont );
    		lHdrFmt.setWrap( true );
    		lHdrFmt.setBorder( showBorder, BorderLineStyle.THIN );
    		lHdrFmt.setAlignment( Alignment.CENTRE );
    		lHdrFmt.setVerticalAlignment( VerticalAlignment.CENTRE );

    		WritableCellFormat lHdr1Fmt = new WritableCellFormat( lLblFont );
    		lHdr1Fmt.setWrap( true );
    		lHdr1Fmt.setBorder( showBorder, BorderLineStyle.THIN );
    		lHdr1Fmt.setAlignment( Alignment.RIGHT );
    		lHdr1Fmt.setVerticalAlignment( VerticalAlignment.CENTRE );

    		WritableCellFormat lHdr2Fmt = new WritableCellFormat( lLblFont );
    		lHdr2Fmt.setWrap( true );
    		lHdr2Fmt.setBorder( showBorder, BorderLineStyle.THIN );
    		lHdr2Fmt.setAlignment( Alignment.LEFT );
    		lHdr2Fmt.setVerticalAlignment( VerticalAlignment.CENTRE );

    		int total= columnHeading.length ;

    		if( (Header != null ) && !Header.equals( "" ) )   //------<<<<<<<-------report title 
    		{
    			lTitle = new Label(ncolCount, nrowCount, Header, lTitleFmt );
    			lwriSheet.addCell(lTitle);
    			lwriSheet.mergeCells( ncolCount, nrowCount, total ,nrowCount+3);
    			Header = "";
    		}
    		if( ( strParameterString != null ) && !strParameterString.equalsIgnoreCase( "" ) )
    		{
    			lTitle = new Label( ncolCount, nrowCount+4, strParameterString, lSubTitleFmt );
    			lwriSheet.addCell( lTitle );
    			lwriSheet.mergeCells( ncolCount, nrowCount+4, total, nrowCount+7);
    			strParameterString = "";
    		}
    		for(int j=0; j < columnHeading.length ;j++)
    		{
    			lwriSheet.setColumnView(j + 1,columnHeading[j].width);
    			hc = new Label(ncolCount++,nrowCount+8,columnHeading[j].ColumnName.toUpperCase(),lSubTitleFmt);
    			lwriSheet.addCell(hc);
    		}
    		nrowCount=nrowCount+9;
    		ncolCount = 1;
    		String indianNumFmt =DBConstants.DATA_indianCurrencyStyle;
			DecimalFormat df = new DecimalFormat( indianNumFmt );
    		
    		for(Object Inner : arrOuter)
    		{
    			ArrayList arrInner1 = (ArrayList) Inner;
    			for(int i=0;i<arrInner1.size();i++)
    			{
    				if(columnHeading[i].alignMent == 1 && arrInner1.get(i)!= null)
    				{	 
    					wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString(),lHdr2Fmt);
    					lwriSheet.addCell(wc);
    				}     
    				if(columnHeading[i].alignMent == 2 && arrInner1.get(i)!= null)
    				{
    					if(columnHeading[i].currancyFormated == true)
    					{
    						boolean isErrorData = false;
    						try{

    							if (columnHeading[i].dataType == DBConstants.DATA_TYPE_BIGDECIMAL) 
    							{
    								double bval = Double.parseDouble(arrInner1.get(i).toString());
    								String value =  df.format( bval ) ;
    								wc = new Label( ncolCount++, nrowCount, value, lHdr1Fmt );
    								lwriSheet.addCell(wc);

    							}
    							else if (columnHeading[i].dataType == DBConstants.DATA_TYPE_LONG) 
    							{
    								Double dval = (Double) arrInner1.get(i);
    								wc = new jxl.write.Number( ncolCount++, nrowCount, dval.doubleValue(), lHdrFmt );
    								//  wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString().concat(".00"),lHdr1Fmt);
    								lwriSheet.addCell(wc);
    							}
    							else
    							{
    								// 	wc = new jxl.write.Number( ncolCount++, nrowCount, ((BigDecimal)arrInner1.get(i)).doubleValue(), lHdr1Fmt );
    								wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString(),lHdr1Fmt);
    								lwriSheet.addCell(wc);
    							}
    						}
    						catch(Exception e)
    						{
    							isErrorData = true;
    						}
    						if(isErrorData)
    						{
    							wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString(),lHdr1Fmt);
    							lwriSheet.addCell(wc);
    						}
    					}

    				}
    				if(columnHeading[i].alignMent == 3 && arrInner1.get(i)!= null)
    				{	 
    					wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString(),lHdrFmt);
    					lwriSheet.addCell(wc);
    				} 
    			}
    			ncolCount = 1;
    			nrowCount++;
    		}


    		if( (Footer != null ) && !Footer.equals( "" ) )   //------<<<<<<<-------report footer
    		{
    			lfooter = new Label(ncolCount, nrowCount, Footer, lHdr2Fmt );
    			lwriSheet.addCell( lfooter );
    			lwriSheet.mergeCells( ncolCount, nrowCount,total ,nrowCount++);
    			Footer = "";
    		}
    		lworkbook.write();
    		lworkbook.close();

    	}catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return reportFile;
    }
*/
    public byte[] getExcelReportPrintFormat(List mainData, List columndata, String[] param, List HeaderData,
    		List footerdata) throws IOException
    		{
    	int nrowCount = 0;
    	int ncolCount = 1;
    	String strFilepath = "ExcelReport." + System.currentTimeMillis() + ".xls";
    	File reportFile = new File(strFilepath);	
    	WorkbookSettings lbookSettings = new WorkbookSettings();
    	lbookSettings.setSuppressWarnings(true);
    	
    	WritableWorkbook lworkbook = Workbook.createWorkbook(reportFile,lbookSettings );
    	WritableSheet lwriSheet = lworkbook.createSheet("Sheet1" , 0 );
    	SheetSettings oSheetSetting = lwriSheet.getSettings();
    	oSheetSetting.setPassword("jignesh");
    	oSheetSetting.setProtected(true);
    	
    	
    	Border showBorder = Border.ALL;
    	Label lTitle;
    	Label lfooter;
    	WritableCell wc = null;
    	WritableCell hc = null;
    	int count = 0;
    	byte[] data = null;
    	
    	try
    	{
    		WritableFont lReportTitleFont = new WritableFont( WritableFont.TAHOMA,
    				WritableFont.DEFAULT_POINT_SIZE + 2 , WritableFont.BOLD, false,
    				UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

    		WritableFont lReportSubTitleFont = new WritableFont( WritableFont.TAHOMA,
    				WritableFont.DEFAULT_POINT_SIZE + 1 , WritableFont.BOLD, false,
    				UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

    		WritableFont lLblFont = new WritableFont( WritableFont.TAHOMA,
    				WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false,
    				UnderlineStyle.NO_UNDERLINE, Colour.BLACK );

    		WritableCellFormat lTitleFmt = new WritableCellFormat( lReportTitleFont );
    		lTitleFmt.setWrap( true );
    		lTitleFmt.setBorder( showBorder, BorderLineStyle.THIN);
    		lTitleFmt.setAlignment( Alignment.CENTRE );
    		lTitleFmt.setVerticalAlignment( VerticalAlignment.CENTRE );
    		lTitleFmt.setBackground( Colour.WHITE);

    		WritableCellFormat lSubTitleFmt = new WritableCellFormat( lReportSubTitleFont );
    		lSubTitleFmt.setWrap( true );
    		lSubTitleFmt.setBorder( showBorder, BorderLineStyle.THIN );
    		lSubTitleFmt.setAlignment( Alignment.CENTRE );
    		lSubTitleFmt.setVerticalAlignment( VerticalAlignment.CENTRE );
    		lSubTitleFmt.setBackground(Colour.ROSE);

    		WritableCellFormat lHdrFmt = new WritableCellFormat( lLblFont );
    		lHdrFmt.setWrap( true );
    		lHdrFmt.setBorder( showBorder, BorderLineStyle.THIN );
    		lHdrFmt.setAlignment( Alignment.CENTRE );
    		lHdrFmt.setVerticalAlignment( VerticalAlignment.CENTRE );
    		lHdrFmt.setBackground( Colour.WHITE);

    		WritableCellFormat lHdr1Fmt = new WritableCellFormat( lLblFont );
    		lHdr1Fmt.setBackground( Colour.WHITE);
    		lHdr1Fmt.setWrap(false );
    		lHdr1Fmt.setBorder( showBorder, BorderLineStyle.THIN );
    		lHdr1Fmt.setAlignment( Alignment.RIGHT);
    		lHdr1Fmt.setVerticalAlignment( VerticalAlignment.CENTRE );
    		lHdr1Fmt.setShrinkToFit(true);
    	
    		
    		
    		WritableCellFormat lHdr2Fmt = new WritableCellFormat( lLblFont );
    		lHdr2Fmt.setBackground( Colour.WHITE);
    		lHdr2Fmt.setWrap( true );
    		lHdr2Fmt.setBorder( showBorder, BorderLineStyle.THIN );
    		lHdr2Fmt.setAlignment( Alignment.LEFT );
    		lHdr2Fmt.setVerticalAlignment( VerticalAlignment.CENTRE );

    		String strParameterString = null;
			ColumnVo[] columnHeading = (ColumnVo[])columndata.get(count);
			String Footer = null;
			int total= columnHeading.length ;
			String Header = null;
			
    		for(Object Outer : mainData)
    		{
    			ArrayList arrOuter1 = (ArrayList) Outer;
    			
    			if(count < param.length)
    			    strParameterString = param[count];
    			
    			if(count < columndata.size())
    				columnHeading = (ColumnVo[])columndata.get(count);
    			
    			if(count < footerdata.size())
    				Footer = (String)footerdata.get(count);
    			
    			if(count < HeaderData.size())
    				Header = (String)HeaderData.get(count);
    				
    			total= columnHeading.length ;

    			if( (Header != null ) && !Header.equals( "" ) )   //------<<<<<<<-------report title 
    			{
    				lTitle = new Label(ncolCount, nrowCount, Header, lTitleFmt );
    				lwriSheet.addCell(lTitle);
    				lwriSheet.mergeCells( ncolCount, nrowCount, total ,nrowCount+3);
    				Header = "";
    			}

    			if( ( strParameterString != null ) && !strParameterString.equalsIgnoreCase(""))
    			{
    				lTitle = new Label( ncolCount, nrowCount+4, strParameterString, lSubTitleFmt );
    				lwriSheet.addCell( lTitle );
    				lwriSheet.mergeCells( ncolCount, nrowCount+4, total, nrowCount+7);
    				// strParameterString = "";
    			}
    			
    		/*	 if( ( datetime != null ) && !datetime.equalsIgnoreCase( "" ) )
    	         {
    	             lTitle = new Label( 1, 6, datetime, lSubTitleFmt );
    	             lwriSheet.addCell( lTitle );
    	             lwriSheet.mergeCells( 1, 6, total, 7 );
    	             datetime = "";
    	         }
             */

    			for(int j=0; j < columnHeading.length;j++)
    			{
    				lwriSheet.setColumnView(j + 1,columnHeading[j].width);
    				
    				hc = new Label(ncolCount++,nrowCount+8,columnHeading[j].ColumnName.toUpperCase(),lSubTitleFmt);
    				lwriSheet.addCell(hc);
    			}
    			nrowCount=nrowCount+8;
    		//	String indianNumFmt =DBConstants.DATA_indianCurrencyStyle;
			//	DecimalFormat df = new DecimalFormat( indianNumFmt );
//    			String value =  df.format( bval ) ;
    			for(Object Inner : arrOuter1)
    			{
    				ncolCount = 1;
    				nrowCount++ ;
    				ArrayList arrInner1 = (ArrayList) Inner;

    				for(int i=0;i<arrInner1.size();i++)
    				{
    					if(columnHeading[i].alignMent == 1 && arrInner1.get(i)!= null)
    					{	 
    						wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString(),lHdr2Fmt);
    						lwriSheet.addCell(wc);
    					}     
    					if(columnHeading[i].alignMent == 2 && arrInner1.get(i)!= null)
    					{
    							boolean isErrorData = false;
    							try{

    								if (columnHeading[i].currancyFormated == true && columnHeading[i].dataType == DBConstants.DATA_TYPE_BIGDECIMAL) 
    								{
    									double bval = Double.parseDouble(arrInner1.get(i).toString());
    									wc = new jxl.write.Number( ncolCount++, nrowCount, bval, lHdr1Fmt );
    									
    									lwriSheet.addCell(wc);
    								}
    								else if (columnHeading[i].currancyFormated == true && columnHeading[i].dataType == DBConstants.DATA_TYPE_LONG) 
    								{
    									Double dval = (Double) arrInner1.get(i);
    									wc = new jxl.write.Number( ncolCount++, nrowCount, dval.doubleValue(), lHdrFmt );
    									//  wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString().concat(".00"),lHdr1Fmt);
    									lwriSheet.addCell(wc);
    								}
    								else
    								{
    									// 	wc = new jxl.write.Number( ncolCount++, nrowCount, ((BigDecimal)arrInner1.get(i)).doubleValue(), lHdr1Fmt );
    									wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString(),lHdr1Fmt);
    									lwriSheet.addCell(wc);
    								}
    							}
    							catch(Exception e)
    							{
    								isErrorData = true;
    							}
    							if(isErrorData)
    							{
    								wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString(),lHdr1Fmt);
    								lwriSheet.addCell(wc);
    							}
    					}
    					if(columnHeading[i].alignMent == 3 && arrInner1.get(i)!= null)
    					{	 
    						wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString(),lHdrFmt);
    						lwriSheet.addCell(wc);
    					} 
    				} 
    			}
    			ncolCount = 1;
    			nrowCount++;
    			count++;

    			if( (Footer != null ) && !Footer.equals( "" ) )   //------<<<<<<<-------report footer
    			{
    				lfooter = new Label(ncolCount, nrowCount, Footer, lHdr2Fmt );
    				lwriSheet.addCell( lfooter );
    				lwriSheet.mergeCells( ncolCount, nrowCount,total ,nrowCount++);
    				//  Footer = "";
    			}
    		
    		}
    		lworkbook.write();
    		lworkbook.close();
    		
    		File objExcelFile =new File(strFilepath);
    		FileInputStream objFileInpStr = new FileInputStream(strFilepath);
    		BufferedInputStream objBuffInpStr =  new BufferedInputStream(objFileInpStr);
    		data = new byte[(int)objExcelFile.length()];
    		objBuffInpStr.read(data);
    		objBuffInpStr.close();
    		objExcelFile.delete();
    		
    	}	
    	catch(Exception ex)
    	{
    		logger.error("Exception occurred in: \n" + ex,ex);
    	}
    	return data;
    }
    public byte[] getExcelReportPrintFormatCustom(List mainData, List columndata, String[] param, List HeaderData, List footerdata, Integer ncolCountHeader,Integer[] lArrIntHiddenCol) throws IOException {
    	
		int nrowCount = 0;
		int ncolCount = 1;
		String strFilepath = "ExcelReport." + System.currentTimeMillis() + ".xls";
		File reportFile = new File(strFilepath);
		WorkbookSettings lbookSettings = new WorkbookSettings();
		lbookSettings.setSuppressWarnings(true);

		WritableWorkbook lworkbook = Workbook.createWorkbook(reportFile, lbookSettings);
		WritableSheet lwriSheet = lworkbook.createSheet("Sheet1", 0);
		lwriSheet.getSettings();

		Border showBorder = Border.ALL;
		Label lTitle;
		Label lfooter;
		WritableCell wc = null;
		WritableCell hc = null;
		int count = 0;
		byte[] data = null;
		CellView cellView = new CellView();
		try {
			WritableFont lReportTitleFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE + 2, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);

			WritableFont lReportSubTitleFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE + 1, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);

			WritableFont lLblFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

			WritableCellFormat lTitleFmt = new WritableCellFormat(lReportTitleFont);
			lTitleFmt.setBorder(showBorder, BorderLineStyle.THIN);
			lTitleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lTitleFmt.setBackground(Colour.WHITE);

			WritableCellFormat lSubTitleFmt = new WritableCellFormat(lReportSubTitleFont);
			lSubTitleFmt.setWrap(true);
			lSubTitleFmt.setBorder(showBorder, BorderLineStyle.THIN);
			lSubTitleFmt.setAlignment(Alignment.CENTRE);
			lSubTitleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lSubTitleFmt.setBackground(Colour.GRAY_25);

			WritableCellFormat lHdrFmt = new WritableCellFormat(lLblFont);
			lHdrFmt.setWrap(true);
			lHdrFmt.setBorder(showBorder, BorderLineStyle.THIN);
			lHdrFmt.setAlignment(Alignment.CENTRE);
			lHdrFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lHdrFmt.setBackground(Colour.WHITE);

			WritableCellFormat lHdr1Fmt = new WritableCellFormat(lLblFont);
			lHdr1Fmt.setBackground(Colour.WHITE);
			lHdr1Fmt.setWrap(false);
			lHdr1Fmt.setBorder(showBorder, BorderLineStyle.THIN);
			lHdr1Fmt.setAlignment(Alignment.RIGHT);
			lHdr1Fmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lHdr1Fmt.setShrinkToFit(true);

			WritableCellFormat lHdr2Fmt = new WritableCellFormat(lLblFont);
			lHdr2Fmt.setBackground(Colour.WHITE);
			lHdr2Fmt.setWrap(true);
			lHdr2Fmt.setBorder(showBorder, BorderLineStyle.THIN);
			lHdr2Fmt.setAlignment(Alignment.LEFT);
			lHdr2Fmt.setVerticalAlignment(VerticalAlignment.CENTRE);

			String strParameterString = null;
			ColumnVo[] columnHeading = (ColumnVo[]) columndata.get(count);
			String Footer = null;
			int total = columnHeading.length;
			String Header = null;

			for (Object Outer : mainData) {
				ArrayList arrOuter1 = (ArrayList) Outer;

				if (count < param.length) {
					strParameterString = param[count];
				}

				if (count < columndata.size()) {
					columnHeading = (ColumnVo[]) columndata.get(count);
				}

				if (count < footerdata.size()) {
					Footer = (String) footerdata.get(count);
				}

				if (count < HeaderData.size()) {
					Header = (String) HeaderData.get(count);
				}

				total = columnHeading.length;

				if ((Header != null) && !Header.equals("")) //------<<<<<<<-------report title 
				{
					lTitle = new Label(ncolCountHeader, nrowCount, Header, lTitleFmt);
					lwriSheet.addCell(lTitle);
					Header = "";
				}

				if ((strParameterString != null) && !strParameterString.equalsIgnoreCase("")) {
					lTitle = new Label(ncolCount, nrowCount + 4, strParameterString, lSubTitleFmt);
					lwriSheet.addCell(lTitle);
					lwriSheet.mergeCells(ncolCount, nrowCount + 4, total, nrowCount + 7);
					// strParameterString = "";
				}

				/*	 if( ( datetime != null ) && !datetime.equalsIgnoreCase( "" ) )
				     {
				         lTitle = new Label( 1, 6, datetime, lSubTitleFmt );
				         lwriSheet.addCell( lTitle );
				         lwriSheet.mergeCells( 1, 6, total, 7 );
				         datetime = "";
				     }
				 */

				
			    for (int j = 0; j < columnHeading.length; j++) {
					lwriSheet.setColumnView(j + 1, columnHeading[j].width);

					hc = new Label(ncolCount++, nrowCount + 2, columnHeading[j].ColumnName.toUpperCase(), lSubTitleFmt);
					lwriSheet.addCell(hc);
				}
				nrowCount = nrowCount + 2;
				//	String indianNumFmt =DBConstants.DATA_indianCurrencyStyle;
				//	DecimalFormat df = new DecimalFormat( indianNumFmt );
//    			String value =  df.format( bval ) ;
				
				/*Hide Column in Excel
				 * Provide -1 value to default
				 * Column Number to hide particular column
				*/
				for(int i = 0;i< lArrIntHiddenCol.length;i++){
					if(lArrIntHiddenCol[i] != -1){
						cellView=lwriSheet.getColumnView(lArrIntHiddenCol[i]);
						cellView.setHidden(true); //set hidden
						lwriSheet.setColumnView(lArrIntHiddenCol[i], cellView);
					}
				}
				
				for (Object Inner : arrOuter1) {
					ncolCount = 1;
					nrowCount++;
					ArrayList arrInner1 = (ArrayList) Inner;
					
					for (int i = 0; i < arrInner1.size(); i++) {
						if (columnHeading[i].alignMent == 1 && arrInner1.get(i) != null) {
							wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr2Fmt);
							lwriSheet.addCell(wc);							
						    
						}
						if (columnHeading[i].alignMent == 2 && arrInner1.get(i) != null) {
							boolean isErrorData = false;
							try {

								if (columnHeading[i].currancyFormated == true && columnHeading[i].dataType == DBConstants.DATA_TYPE_BIGDECIMAL) {
									double bval = Double.parseDouble(arrInner1.get(i).toString());
									wc = new jxl.write.Number(ncolCount++, nrowCount, bval, lHdr1Fmt);

									lwriSheet.addCell(wc);
								} else if (columnHeading[i].currancyFormated == true && columnHeading[i].dataType == DBConstants.DATA_TYPE_LONG) {
									Double dval = (Double) arrInner1.get(i);
									wc = new jxl.write.Number(ncolCount++, nrowCount, dval.doubleValue(), lHdrFmt);
									//  wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString().concat(".00"),lHdr1Fmt);
									lwriSheet.addCell(wc);
								} else {
									// 	wc = new jxl.write.Number( ncolCount++, nrowCount, ((BigDecimal)arrInner1.get(i)).doubleValue(), lHdr1Fmt );
									wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr1Fmt);
									lwriSheet.addCell(wc);
								}
							} catch (Exception e) {
								isErrorData = true;
							}
							if (isErrorData) {
								wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr1Fmt);
								lwriSheet.addCell(wc);
							}
						}
						if (columnHeading[i].alignMent == 3 && arrInner1.get(i) != null) {
							wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdrFmt);
							lwriSheet.addCell(wc);
						}
					}
				}
				ncolCount = 1;
				nrowCount++;
				count++;

				if ((Footer != null) && !Footer.equals("")) //------<<<<<<<-------report footer
				{
					lfooter = new Label(ncolCount, nrowCount, Footer, lHdr2Fmt);
					lwriSheet.addCell(lfooter);
					lwriSheet.mergeCells(ncolCount, nrowCount, total, nrowCount++);
					//  Footer = "";
				}

			}
			lworkbook.write();
			lworkbook.close();

			File objExcelFile = new File(strFilepath);
			FileInputStream objFileInpStr = new FileInputStream(strFilepath);
			BufferedInputStream objBuffInpStr = new BufferedInputStream(objFileInpStr);
			data = new byte[(int) objExcelFile.length()];
			objBuffInpStr.read(data);
			objBuffInpStr.close();
			objExcelFile.delete();

		} catch (Exception ex) {
			logger.error("Exception occurred in: \n" + ex, ex);
		}
		return data;
	}
 public byte[] getExcelReportForPensionACNo(List mainData, List columndata, String[] param, List HeaderData, List footerdata, Integer ncolCountHeader,Integer[] lArrIntHiddenCol) throws IOException {
    	
		int nrowCount = 0;
		int ncolCount = 0;
		String strFilepath = "ExcelReport." + System.currentTimeMillis() + ".xls";
		File reportFile = new File(strFilepath);
		WorkbookSettings lbookSettings = new WorkbookSettings();
		lbookSettings.setSuppressWarnings(true);

		WritableWorkbook lworkbook = Workbook.createWorkbook(reportFile, lbookSettings);
		WritableSheet lwriSheet = lworkbook.createSheet("Sheet1", 0);		
		lwriSheet.getSettings().setProtected(true);
		
		Border showBorder = Border.ALL;
		Label lTitle;
		Label lfooter;
		WritableCell wc = null;
		WritableCell hc = null;
		int count = 0;
		byte[] data = null;
		CellView cellView = new CellView();
		try {
			WritableFont lReportTitleFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE + 2, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);

			WritableFont lReportSubTitleFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE + 1, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);

			WritableFont lLblFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

			WritableFont lWhiteColorFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
			
			WritableCellFormat lTitleFmt = new WritableCellFormat(lReportTitleFont);
			lTitleFmt.setBorder(showBorder, BorderLineStyle.THIN);
			lTitleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lTitleFmt.setBackground(Colour.WHITE);

			WritableCellFormat lSubTitleFmt = new WritableCellFormat(lReportSubTitleFont);
			lSubTitleFmt.setWrap(true);
			lSubTitleFmt.setBorder(showBorder, BorderLineStyle.THIN);
			lSubTitleFmt.setAlignment(Alignment.CENTRE);
			lSubTitleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lSubTitleFmt.setBackground(Colour.GRAY_25);
			
			WritableCellFormat lSubTitleFmtUnlock = new WritableCellFormat(lReportSubTitleFont);
			lSubTitleFmtUnlock.setWrap(true);
			lSubTitleFmtUnlock.setBorder(showBorder, BorderLineStyle.THIN);
			lSubTitleFmtUnlock.setAlignment(Alignment.CENTRE);
			lSubTitleFmtUnlock.setVerticalAlignment(VerticalAlignment.CENTRE);
			lSubTitleFmtUnlock.setBackground(Colour.GRAY_25);
			lSubTitleFmtUnlock.setLocked(false);

			WritableCellFormat lHdrFmt = new WritableCellFormat(lLblFont);
			lHdrFmt.setWrap(true);
			lHdrFmt.setBorder(showBorder, BorderLineStyle.THIN);
			lHdrFmt.setAlignment(Alignment.LEFT);
			lHdrFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lHdrFmt.setBackground(Colour.WHITE);
			
			
			WritableCellFormat lHdr1Fmt = new WritableCellFormat(lLblFont);
			lHdr1Fmt.setBackground(Colour.WHITE);
			lHdr1Fmt.setWrap(false);
			lHdr1Fmt.setBorder(showBorder, BorderLineStyle.THIN);
			lHdr1Fmt.setAlignment(Alignment.RIGHT);
			lHdr1Fmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lHdr1Fmt.setShrinkToFit(true);
			

			WritableCellFormat lHdr2FmtUnlock = new WritableCellFormat(lLblFont);
			lHdr2FmtUnlock.setBackground(Colour.WHITE);
			lHdr2FmtUnlock.setWrap(true);
			lHdr2FmtUnlock.setBorder(showBorder, BorderLineStyle.THIN);
			lHdr2FmtUnlock.setAlignment(Alignment.LEFT);
			lHdr2FmtUnlock.setVerticalAlignment(VerticalAlignment.CENTRE);
			lHdr2FmtUnlock.setLocked(false);
			
			WritableCellFormat lHdr3FmtWhiteFont = new WritableCellFormat(lWhiteColorFont);
			lHdr3FmtWhiteFont.setBackground(Colour.WHITE);
			lHdr3FmtWhiteFont.setWrap(true);		
			lHdr3FmtWhiteFont.setAlignment(Alignment.RIGHT);
			lHdr3FmtWhiteFont.setVerticalAlignment(VerticalAlignment.CENTRE);
			lHdr1Fmt.setShrinkToFit(true);
			
			
			String strParameterString = null;
			ColumnVo[] columnHeading = (ColumnVo[]) columndata.get(count);
			String Footer = null;
			int total = columnHeading.length;
			String Header = null;

			for (Object Outer : mainData) {
				ArrayList arrOuter1 = (ArrayList) Outer;

				if (count < param.length) {
					strParameterString = param[count];
				}

				if (count < columndata.size()) {
					columnHeading = (ColumnVo[]) columndata.get(count);
				}

				if (count < footerdata.size()) {
					Footer = (String) footerdata.get(count);
				}

				if (count < HeaderData.size()) {
					Header = (String) HeaderData.get(count);
				}

				total = columnHeading.length;

				if ((Header != null) && !Header.equals("")) //------<<<<<<<-------report title 
				{
					lTitle = new Label(ncolCountHeader, nrowCount, Header, lTitleFmt);
					lwriSheet.addCell(lTitle);
					Header = "";
				}

				if ((strParameterString != null) && !strParameterString.equalsIgnoreCase("")) {
					lTitle = new Label(ncolCount, nrowCount + 4, strParameterString, lSubTitleFmt);
					lwriSheet.addCell(lTitle);
					lwriSheet.mergeCells(ncolCount, nrowCount + 4, total, nrowCount + 7);
					// strParameterString = "";
				}

				/*	 if( ( datetime != null ) && !datetime.equalsIgnoreCase( "" ) )
				     {
				         lTitle = new Label( 1, 6, datetime, lSubTitleFmt );
				         lwriSheet.addCell( lTitle );
				         lwriSheet.mergeCells( 1, 6, total, 7 );
				         datetime = "";
				     }
				 */

				
			    for (int j = 0; j < columnHeading.length; j++) {
					lwriSheet.setColumnView(j, columnHeading[j].width);

					if(j==6){
						hc = new Label(ncolCount++, nrowCount , columnHeading[j].ColumnName.toUpperCase(), lHdr3FmtWhiteFont);
					}else if(j==5){
						hc = new Label(ncolCount++, nrowCount , columnHeading[j].ColumnName.toUpperCase(), lSubTitleFmtUnlock);
					}else{
						hc = new Label(ncolCount++, nrowCount , columnHeading[j].ColumnName.toUpperCase(), lSubTitleFmt);
					}
					lwriSheet.addCell(hc);
				}
				
				//	String indianNumFmt =DBConstants.DATA_indianCurrencyStyle;
				//	DecimalFormat df = new DecimalFormat( indianNumFmt );
//    			String value =  df.format( bval ) ;
				
				/*Hide Column in Excel
				 * Provide -1 value to default
				 * Column Number to hide particular column
				*/
				for(int i = 0;i< lArrIntHiddenCol.length;i++){
					if(lArrIntHiddenCol[i] != -1){
						cellView=lwriSheet.getColumnView(lArrIntHiddenCol[i]);
						cellView.setHidden(true);//set hidden		
						cellView.setAutosize(true);
						lwriSheet.setColumnView(lArrIntHiddenCol[i], cellView);
						
					}
				}
				
				for (Object Inner : arrOuter1) {
					ncolCount = 0;
					nrowCount++;
					ArrayList arrInner1 = (ArrayList) Inner;
					
					for (int i = 0; i < arrInner1.size(); i++) {
						if (columnHeading[i].alignMent == 1 && arrInner1.get(i) != null) {
							
							if(i==6){								
								wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr3FmtWhiteFont);
							}else if(i==5){
								wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr2FmtUnlock);								
							}else{
								wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdrFmt);
							}							
							lwriSheet.addCell(wc);							
						    
						}
						if (columnHeading[i].alignMent == 2 && arrInner1.get(i) != null) {
							
							if(i==6){								
								wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr3FmtWhiteFont);
							}else if(i==5){
								wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr2FmtUnlock);								
							}else{
								wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr1Fmt);
							}
							lwriSheet.addCell(wc);
						}						
					}
				}
				ncolCount = 0;
				nrowCount++;
				count++;

				if ((Footer != null) && !Footer.equals("")) //------<<<<<<<-------report footer
				{
					lfooter = new Label(ncolCount, nrowCount, Footer, lHdr1Fmt);
					lwriSheet.addCell(lfooter);
					lwriSheet.mergeCells(ncolCount, nrowCount, total, nrowCount++);
					//  Footer = "";
				}

			}
			lworkbook.write();
			lworkbook.close();

			File objExcelFile = new File(strFilepath);
			FileInputStream objFileInpStr = new FileInputStream(strFilepath);
			BufferedInputStream objBuffInpStr = new BufferedInputStream(objFileInpStr);
			data = new byte[(int) objExcelFile.length()];
			objBuffInpStr.read(data);
			objBuffInpStr.close();
			objExcelFile.delete();

		} catch (Exception ex) {
			logger.error("Exception occurred in: \n" + ex, ex);
		}
		return data;
	}
 
	public byte[] getExcelBankPmntReportPrintFormat(List<List<List<Object>>> mainData, List columndata, String[] param, List HeaderData, List footerdata) throws IOException {
		int nrowCount = 0;
		int ncolCount = 1;
		String strFilepath = "ExcelReport." + System.currentTimeMillis() + ".xls";
		File reportFile = new File(strFilepath);
		WorkbookSettings lbookSettings = new WorkbookSettings();
		lbookSettings.setSuppressWarnings(true);

		WritableWorkbook lworkbook = Workbook.createWorkbook(reportFile, lbookSettings);
		WritableSheet lwriSheet = lworkbook.createSheet("Sheet1", 0);
		lwriSheet.getSettings();
//		SheetSettings oSheetSetting = lwriSheet.getSettings();
//		oSheetSetting.setPassword("jignesh");
//		oSheetSetting.setProtected(true);

		Border showBorder = Border.ALL;
		Label lTitle;
		Label lfooter;
		WritableCell wc = null;
		WritableCell hc = null;
		int count = 0;
		byte[] data = null;

		try {
			WritableFont lReportTitleFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE + 2, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);

			WritableFont lReportSubTitleFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE + 1, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);

			WritableFont lLblFont = new WritableFont(WritableFont.TAHOMA, WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

			WritableCellFormat lTitleFmt = new WritableCellFormat(lReportTitleFont);
			lTitleFmt.setWrap(true);
			lTitleFmt.setBorder(showBorder, BorderLineStyle.THIN);
			lTitleFmt.setAlignment(Alignment.CENTRE);
			lTitleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lTitleFmt.setBackground(Colour.WHITE);

			WritableCellFormat lSubTitleFmt = new WritableCellFormat(lReportSubTitleFont);
			lSubTitleFmt.setWrap(true);
			lSubTitleFmt.setBorder(showBorder, BorderLineStyle.THIN);
			lSubTitleFmt.setAlignment(Alignment.CENTRE);
			lSubTitleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lSubTitleFmt.setBackground(Colour.ROSE);

			WritableCellFormat lHdrFmt = new WritableCellFormat(lLblFont);
			lHdrFmt.setWrap(true);
			lHdrFmt.setBorder(showBorder, BorderLineStyle.THIN);
			lHdrFmt.setAlignment(Alignment.CENTRE);
			lHdrFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lHdrFmt.setBackground(Colour.WHITE);

			WritableCellFormat lHdr1Fmt = new WritableCellFormat(lLblFont);
			lHdr1Fmt.setBackground(Colour.WHITE);
			lHdr1Fmt.setWrap(false);
			lHdr1Fmt.setBorder(showBorder, BorderLineStyle.THIN);
			lHdr1Fmt.setAlignment(Alignment.RIGHT);
			lHdr1Fmt.setVerticalAlignment(VerticalAlignment.CENTRE);
			lHdr1Fmt.setShrinkToFit(true);

			WritableCellFormat lHdr2Fmt = new WritableCellFormat(lLblFont);
			lHdr2Fmt.setBackground(Colour.WHITE);
			lHdr2Fmt.setWrap(true);
			lHdr2Fmt.setBorder(showBorder, BorderLineStyle.THIN);
			lHdr2Fmt.setAlignment(Alignment.LEFT);
			lHdr2Fmt.setVerticalAlignment(VerticalAlignment.CENTRE);

			String strParameterString = null;
			ColumnVo[] columnHeading = (ColumnVo[]) columndata.get(count);
			String Footer = null;
			int total = columnHeading.length;
			String Header = null;

			for (List<List<Object>> excelOuter : mainData) {
				for(Object Outer : excelOuter)
				{
					ArrayList arrOuter1 = (ArrayList) Outer;
	
					if (count < param.length) {
						strParameterString = param[count];
					}
	
					if (count < columndata.size()) {
						columnHeading = (ColumnVo[]) columndata.get(count);
					}
	
					if (count < footerdata.size()) {
						Footer = (String) footerdata.get(count);
					}
	
					if (count < HeaderData.size()) {
						Header = (String) HeaderData.get(count);
					}
	
					total = columnHeading.length;
	
					if ((Header != null) && !Header.equals("")) //------<<<<<<<-------report title 
					{
						lTitle = new Label(ncolCount, nrowCount, Header, lTitleFmt);
						lwriSheet.addCell(lTitle);
						lwriSheet.mergeCells(ncolCount, nrowCount, total, nrowCount + 3);
						Header = "";
					}
	
					if ((strParameterString != null) && !strParameterString.equalsIgnoreCase("")) {
						lTitle = new Label(ncolCount, nrowCount + 4, strParameterString, lSubTitleFmt);
						lwriSheet.addCell(lTitle);
						lwriSheet.mergeCells(ncolCount, nrowCount + 4, total, nrowCount + 7);
						// strParameterString = "";
					}
	
					/*	 if( ( datetime != null ) && !datetime.equalsIgnoreCase( "" ) )
					     {
					         lTitle = new Label( 1, 6, datetime, lSubTitleFmt );
					         lwriSheet.addCell( lTitle );
					         lwriSheet.mergeCells( 1, 6, total, 7 );
					         datetime = "";
					     }
					 */
	
					for (int j = 0; j < columnHeading.length; j++) {
						lwriSheet.setColumnView(j + 1, columnHeading[j].width);
	
						hc = new Label(ncolCount++, nrowCount + 8, columnHeading[j].ColumnName.toUpperCase(), lSubTitleFmt);
						lwriSheet.addCell(hc);
					}
					nrowCount = nrowCount + 8;
					//	String indianNumFmt =DBConstants.DATA_indianCurrencyStyle;
					//	DecimalFormat df = new DecimalFormat( indianNumFmt );
	//    			String value =  df.format( bval ) ;
					for (Object Inner : arrOuter1) {
						ncolCount = 1;
						nrowCount++;
						ArrayList arrInner1 = (ArrayList) Inner;
	
						for (int i = 0; i < arrInner1.size(); i++) {
							if (columnHeading[i].alignMent == 1 && arrInner1.get(i) != null) {
								wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr2Fmt);
								lwriSheet.addCell(wc);
							}
							if (columnHeading[i].alignMent == 2 && arrInner1.get(i) != null) {
								boolean isErrorData = false;
								try {
	
									if (columnHeading[i].currancyFormated == true && columnHeading[i].dataType == DBConstants.DATA_TYPE_BIGDECIMAL) {
										double bval = Double.parseDouble(arrInner1.get(i).toString());
										wc = new jxl.write.Number(ncolCount++, nrowCount, bval, lHdr1Fmt);
	
										lwriSheet.addCell(wc);
									} else if (columnHeading[i].currancyFormated == true && columnHeading[i].dataType == DBConstants.DATA_TYPE_LONG) {
										Double dval = (Double) arrInner1.get(i);
										wc = new jxl.write.Number(ncolCount++, nrowCount, dval.doubleValue(), lHdrFmt);
										//  wc = new Label(ncolCount++,nrowCount,arrInner1.get(i).toString().concat(".00"),lHdr1Fmt);
										lwriSheet.addCell(wc);
									} else {
										// 	wc = new jxl.write.Number( ncolCount++, nrowCount, ((BigDecimal)arrInner1.get(i)).doubleValue(), lHdr1Fmt );
										wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr1Fmt);
										lwriSheet.addCell(wc);
									}
								} catch (Exception e) {
									isErrorData = true;
								}
								if (isErrorData) {
									wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdr1Fmt);
									lwriSheet.addCell(wc);
								}
							}
							if (columnHeading[i].alignMent == 3 && arrInner1.get(i) != null) {
								wc = new Label(ncolCount++, nrowCount, arrInner1.get(i).toString(), lHdrFmt);
								lwriSheet.addCell(wc);
							}
						}
					}
					ncolCount = 1;
					nrowCount=nrowCount+2;
					count++;
		
					if ((Footer != null) && !Footer.equals("")) //------<<<<<<<-------report footer
					{
						lfooter = new Label(ncolCount, nrowCount, Footer, lHdr2Fmt);
						lwriSheet.addCell(lfooter);
						lwriSheet.mergeCells(ncolCount, nrowCount, total, nrowCount+2);
						//  Footer = "";
					}
					nrowCount=nrowCount+6;
				}
				
			}
			lworkbook.write();
			lworkbook.close();

			File objExcelFile = new File(strFilepath);
			FileInputStream objFileInpStr = new FileInputStream(strFilepath);
			BufferedInputStream objBuffInpStr = new BufferedInputStream(objFileInpStr);
			data = new byte[(int) objExcelFile.length()];
			objBuffInpStr.read(data);
			objBuffInpStr.close();
			objExcelFile.delete();

		} catch (Exception ex) {
			logger.error("Exception occurred in: \n" + ex, ex);
		}
		return data;
	}
    
}