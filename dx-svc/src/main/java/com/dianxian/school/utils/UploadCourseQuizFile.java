package com.dianxian.school.utils;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.ResponseConstants;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by y on 2016/9/9.
 */
public class UploadCourseQuizFile {
    public static List<List<String>> readExcel(InputStream fs, FormDataContentDisposition contentDispositionHeader) {
        List<List<String>> result = new ArrayList<List<String>>();

        try {
            Workbook workbook = WorkbookFactory.create(fs);
            result = readXlsx(workbook);
        } catch (Exception e) {
            throw new BizLogicException(ResponseConstants.INVALID_PARAM, "文件格式有误");
        }

        return result;
    }
    private static List<List<String>> readXlsx(Workbook workbook) {
        List<List<String>> result = new ArrayList<List<String>>();
        try {
            //读取第一页
            Sheet hssfSheet = workbook.getSheetAt(0);
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                Row row = hssfSheet.getRow(rowNum);
                int minColIx = row.getFirstCellNum();
                int maxColIx = row.getLastCellNum();
                List<String> rowList = new ArrayList<String>();
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    Cell cell = row.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
                    rowList.add(getStringVal(cell));
                }
                if (rowNum == 0) {
                    validFileHead(rowList);
                } else {
                    result.add(rowList);
                }
            }
        } catch (Exception e) {
            throw new BizLogicException(ResponseConstants.INVALID_PARAM, "文件格式有误");
        }
        return result;
    }

    private static void validFileHead(List<String> rowList) {
        String[] head = {"学号", "姓名", "课程", "成绩"};
        int num = 0;
        if(rowList.isEmpty() || rowList.size() != head.length){
            throw new BizLogicException(ResponseConstants.INVALID_PARAM, "文件格式有误");
        }
        for (String headCell : rowList) {
            if (!headCell.equals(head[num])) {
                throw new BizLogicException(ResponseConstants.INVALID_PARAM, "文件格式有误");
            }
            num++;
        }
    }
    private static String getStringVal(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            case Cell.CELL_TYPE_NUMERIC:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default:
                return "";
        }

    }
}