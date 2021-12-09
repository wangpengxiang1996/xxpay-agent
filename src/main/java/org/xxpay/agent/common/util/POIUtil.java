//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class POIUtil {
    private static Logger logger = Logger.getLogger(POIUtil.class);
    private static final String xls = "xls";
    private static final String xlsx = "xlsx";

    public POIUtil() {
    }

    public static List<List<String>> readExcel(MultipartFile file) throws IOException {
        checkFile(file);
        Workbook workbook = getWorkBook(file);
        List<List<String>> list = new ArrayList();
        if (workbook != null) {
            for(int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); ++sheetNum) {
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet != null) {
                    int firstRowNum = sheet.getFirstRowNum();
                    int lastRowNum = sheet.getLastRowNum();

                    for(int rowNum = firstRowNum; rowNum <= lastRowNum; ++rowNum) {
                        Row row = sheet.getRow(rowNum);
                        if (row != null) {
                            int firstCellNum = row.getFirstCellNum();
                            int lastCellNum = row.getPhysicalNumberOfCells();
                            List<String> cellList = new LinkedList();

                            for(int cellNum = firstCellNum; cellNum < lastCellNum; ++cellNum) {
                                Cell cell = row.getCell(cellNum);
                                cellList.add(getCellValue(cell));
                            }

                            list.add(cellList);
                        }
                    }
                }
            }

            workbook.close();
        }

        return list;
    }

    public static void checkFile(MultipartFile file) throws IOException {
        if (null == file) {
            logger.error("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        } else {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
                logger.error(fileName + "不是excel文件");
                throw new IOException(fileName + "不是excel文件");
            }
        }
    }

    public static Workbook getWorkBook(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Object workbook = null;

        try {
            InputStream is = file.getInputStream();
            if (fileName.endsWith("xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException var4) {
            logger.info(var4.getMessage());
        }

        return (Workbook)workbook;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        } else {
            if (cell.getCellType() == 0) {
                cell.setCellType(1);
            }

            switch(cell.getCellType()) {
                case 0:
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                case 1:
                    cellValue = String.valueOf(cell.getStringCellValue());
                    break;
                case 2:
                    cellValue = String.valueOf(cell.getCellFormula());
                    break;
                case 3:
                    cellValue = "";
                    break;
                case 4:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case 5:
                    cellValue = "非法字符";
                    break;
                default:
                    cellValue = "未知类型";
            }

            return cellValue;
        }
    }
}
