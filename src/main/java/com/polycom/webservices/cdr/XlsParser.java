package com.polycom.webservices.cdr;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.cdr.bean.EndpointUsageCdr;

public class XlsParser {
    public static List<EndpointUsageCdr>
            getEndpointUsageCdr(final File excelFile) throws BiffException,
                    IOException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        final List<EndpointUsageCdr> result = new ArrayList<EndpointUsageCdr>();
        final Map<String, Integer> position = new HashMap<String, Integer>();
        final Workbook workBook = Workbook.getWorkbook(excelFile);
        final Sheet[] sheets = workBook.getSheets();
        final Cell[] cells = sheets[0].getRow(8);
        for (int i = 0; i < cells.length; i++) {
            String contents = cells[i].getContents();
            contents = contents.replaceAll("[\\s|(|)|\\.]", "");
            position.put(contents, i);
        }
        String area = sheets[0].getCell(0,7).getContents();
        int beginLineNumber = 10;
        if (area == null || area.isEmpty()) {
        	beginLineNumber = 9;
        }
        for (int i = beginLineNumber; i < sheets[0].getRows(); i++) {
            final EndpointUsageCdr endpointUsageCdr = new EndpointUsageCdr();
            endpointUsageCdr.setName(sheets[0].getCell(1, 1).getContents());
            endpointUsageCdr.setModel(sheets[0].getCell(1, 2).getContents());
            endpointUsageCdr
                    .setIpAddress(sheets[0].getCell(1, 3).getContents());
            endpointUsageCdr.setISDNNumber1(sheets[0].getCell(1, 4)
                    .getContents());
            endpointUsageCdr.setISDNNumber2(sheets[0].getCell(1, 5)
                    .getContents());
            endpointUsageCdr.setSerialNumber(sheets[0].getCell(1, 6)
                    .getContents());
            for (final String attribute : position.keySet()) {
                final String value = sheets[0].getRow(i)[position
                        .get(attribute)].getContents();
                final String methodName = "set" + attribute;
                final Method method = CommonUtils
                        .getDeclaredMethod(endpointUsageCdr,
                                           methodName,
                                           new Class[] { String.class });
                method.invoke(endpointUsageCdr, new Object[] { value });
            }
            result.add(endpointUsageCdr);
        }
        workBook.close();
        return result;
    }

    public static List<String> getTitle(final File excelFile) {
        final List<String> result = new ArrayList<String>();
        if (excelFile.exists()) {
            Workbook workBook = null;
            try {
                workBook = Workbook.getWorkbook(excelFile);
            } catch (BiffException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final Sheet[] sheets = workBook.getSheets();
            final Cell[] cells = sheets[0].getRow(8);
            for (final Cell cell : cells) {
                String contents = cell.getContents();
                contents = contents.replaceAll("[\\s|(|)|\\.]", "");
                System.out.println(contents);
                result.add(contents);
            }
        }
        return result;
    }

    public static void main(final String[] args) throws BiffException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, IOException {
        // getTitle(new File("cdr/ENDPOINT_CDR_DETAIL-6.xls"));
        final List<EndpointUsageCdr> cdrs = getEndpointUsageCdr(new File(
                "cdr/ENDPOINT_CDR_DETAIL-6.xls"));
        System.out.println(cdrs.size());
        for (final EndpointUsageCdr cdr : cdrs) {
            System.out.println(cdr + cdr.getRemoteSystemName());
        }
    }
}
