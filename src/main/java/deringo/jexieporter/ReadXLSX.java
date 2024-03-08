package deringo.jexieporter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadXLSX {

    private final static String FILENAME = "filename.xlsx";
    private final static String SHEETNAME = "sheetname";
    
    private static ArrayList<String> headerNames = new ArrayList<>();
    private static Map<Integer, Map<String, Object>> rows = new HashMap<>();
    
    public static void main(String[] args) {
        try {
            InputStream is = ReadXLSX.class.getClassLoader().getResourceAsStream(FILENAME);
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEETNAME);
            // Get the first row from the sheet
            Row firstrow = sheet.getRow(0);

            // Iterate cells of the row and add data to the List
            for (Cell cell : firstrow) {
                switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        DataFormatter dataFormatter = new DataFormatter();
                        headerNames.add(dataFormatter.formatCellValue(cell));
                    } else {
                        headerNames.add(String.valueOf(cell.getNumericCellValue()));
                    }
                    break;
                case STRING:
                    headerNames.add(cell.getStringCellValue());
                    break;
                case BOOLEAN:
                    headerNames.add(String.valueOf(cell.getBooleanCellValue()));
                    break;
                default:
                    headerNames.add("");
                    break;
                }
            }

            
            for (int rowIndex=1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                rows.put(rowIndex, new HashMap<>());
                for (int colIndex=0; colIndex < headerNames.size(); colIndex++) {
                    String key = headerNames.get(colIndex);
                    Object value;
                    Cell cell = row.getCell(colIndex);
                    switch (cell.getCellType()) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            value = cell.getLocalDateTimeCellValue();
                        } else {
                            value = cell.getNumericCellValue();
                        }
                        break;
                    case STRING:
                        value = cell.getStringCellValue();
                        break;
                    case BOOLEAN:
                        value = cell.getBooleanCellValue();
                        break;
                    default:
                        value = null;
                        break;
                    }
                    rows.get(rowIndex).put(key, value);
                }
            }

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Print the List
        System.out.println(headerNames);

        rows.forEach((k, v) -> v.forEach((key, value) -> System.out.println(key + " : " + value)));
    }
}
