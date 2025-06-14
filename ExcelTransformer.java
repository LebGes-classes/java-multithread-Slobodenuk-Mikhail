import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelTransformer {

    public Map<String, List<List<Object>>> readExcel(String filePath) throws IOException {
        Map<String, List<List<Object>>> sheetData = new LinkedHashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                List<List<Object>> rows = new ArrayList<>();

                for (Row row : sheet) {
                    List<Object> cellValues = new ArrayList<>();

                    for (int col = 0; col < row.getLastCellNum(); col++) {
                        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        cellValues.add(getCellValue(cell));
                    }

                    rows.add(cellValues);
                }

                sheetData.put(sheet.getSheetName(), rows);
            }
        }

        return sheetData;
    }


    public void writeExcel(String filePath, Map<String, List<List<Object>>> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // делаем шапки
            List<Object> taskHead = DataBase.getTaskHead();
            List<Object> empHead = DataBase.getEmpHead();


            boolean flag = true;
            for (Map.Entry<String, List<List<Object>>> entry : data.entrySet()) {
                Sheet sheet = workbook.createSheet(entry.getKey());
                List<List<Object>> rows = entry.getValue();
                if (flag) {
                    Row head = sheet.createRow(0);
                    for (int i = 0; i < taskHead.size(); i++) {
                        Cell cell = head.createCell(i);
                        setCellValue(cell, taskHead.get(i));
                    }
                    flag = false;
                } else {
                    Row head = sheet.createRow(0);
                    for (int i = 0; i < empHead.size(); i++) {
                        Cell cell = head.createCell(i);
                        setCellValue(cell, empHead.get(i));
                    }
                }

                for (int i = 1; i <= rows.size(); i++) {
                    Row row = sheet.createRow(i);
                    List<Object> cells = rows.get(i-1);

                    for (int j = 0; j < cells.size(); j++) {
                        Cell cell = row.createCell(j);
                        setCellValue(cell, cells.get(j));
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    // Получает значение ячейки как Object
    private Object getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // Проверяем, является ли число целым
                    if (numericValue == (int) numericValue) {
                        return (int) numericValue;
                    } else {
                        return numericValue;
                    }
                }
            case BOOLEAN: return cell.getBooleanCellValue();
            case FORMULA: return cell.getCellFormula();
            case BLANK:   return null;
            default:      return null;
        }
    }

    // Устанавливает значение ячейки из Object
    private void setCellValue(Cell cell, Object value) {
        if (value == null) return;

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
