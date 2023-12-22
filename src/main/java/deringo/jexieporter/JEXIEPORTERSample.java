package deringo.jexieporter;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class JEXIEPORTERSample {
    private static final Path IMPORT_CSV  = Paths.get("src/main/resources/sampledata.csv");
    private static final Path EXPORT_CSV  = getTMPFile(".csv");
    private static final Path EXPORT_XLSX = getTMPFile(".xlsx");
    
    public static void main(String[] args) throws Exception {
        System.setProperty("file.encoding", "UTF-8");
        
        // Import CSV data
        List<SampleData> list = importCSV();
        list.forEach(data -> System.out.println(data));
        
        System.out.println("###################################");
        // convert data to XML
        list.forEach(data -> System.out.println(toXML(data)));
        
        System.out.println("###################################");
        // convert data to JSON
        list.forEach(data -> System.out.println(toJSON(data)));
        
        System.out.println("###################################");
        // convert data to XML and back to data
        list.forEach(data -> System.out.println(xmlToSampleData( toXML(data) )));
     
        System.out.println("###################################");
        // convert data to JSON and back to data
        list.forEach(data -> System.out.println(jsonToSampleData( toJSON(data) )));

        System.out.println("###################################");
        // export data to CSV
        exportCSV(list);
        // open new CSV file
        Desktop.getDesktop().open(EXPORT_CSV.toFile());
        
        System.out.println("###################################");
        // export data to xlsx
        exportXLS(list);
        // open new xlsx file
        Desktop.getDesktop().open(EXPORT_XLSX.toFile());

        System.out.println("###################################");
        // Import xlsx data
        list = importXLSX();
        list.forEach(data -> System.out.println(data));
    }
    

    // https://mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
    private static List<SampleData> importCSV() throws Exception {
        FileReader fileReader = new FileReader(IMPORT_CSV.toFile());
        List<List<String>> records = new ArrayList<List<String>>();
        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build(); // custom separator
        try(CSVReader reader = new CSVReaderBuilder(fileReader)
                .withCSVParser(csvParser)   // custom CSV parser
                .withSkipLines(1)           // skip the first line, header info
                
                .build()){
            List<String[]> r = reader.readAll();
            r.forEach(x -> records.add(Arrays.asList(x)));
        }
        
        List<SampleData> list = new ArrayList<>();
        records.forEach(x -> list.add(new SampleData(x)));
        return list;
    }
 
    private static String toXML(SampleData sampleData) {
        String result = "";
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(sampleData.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(sampleData, sw);
            result = sw.toString();
        } catch (Exception e) {
            result = e.toString();
        }

        return result;
    }
    
    private static String toJSON(SampleData sampleData) {
        try {
            // Creating the ObjectMapper object
            ObjectMapper mapper = new ObjectMapper();
            // make Jackson use JAXB annotations
            JakartaXmlBindAnnotationModule module = new JakartaXmlBindAnnotationModule();
            mapper.registerModule(module);
            // Converting the Object to JSONString (pretty print)
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sampleData);
            return jsonString;
        } catch (Exception e) {
            return String.format("{\"error\":\"%s\"}", e.toString());
        }
    }
    
    private static SampleData xmlToSampleData(String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SampleData.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            SampleData sampleData = (SampleData) unmarshaller.unmarshal(reader); 
            return sampleData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static SampleData jsonToSampleData(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SampleData sampleData = mapper.readValue(json, SampleData.class);
            return sampleData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // https://mkyong.com/java/how-to-export-data-to-csv-file-java/
    private static void exportCSV(List<SampleData> list) {
        List<String[]> csvData = new ArrayList<>();
        list.forEach(data -> csvData.add(data.toStringArray()));
        
        try (ICSVWriter writer = new CSVWriterBuilder(
                new FileWriter(EXPORT_CSV.toFile(), StandardCharsets.UTF_8))
                .withSeparator(';')
                .build()) {
            writer.writeAll(csvData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // https://www.baeldung.com/java-microsoft-excel
    private static void exportXLS(List<SampleData> list) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sample Data");
        Row header = sheet.createRow(0);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("name");
        // lazy style to set headers, don't try this at home
        int i=1;
        header.createCell(i).setCellValue("phone"); i++;
        header.createCell(i).setCellValue("email"); i++;
        header.createCell(i).setCellValue("address"); i++;
        header.createCell(i).setCellValue("postalZip"); i++;
        header.createCell(i).setCellValue("region"); i++;
        header.createCell(i).setCellValue("country"); i++;
        header.createCell(i).setCellValue("list"); i++;
        header.createCell(i).setCellValue("text"); i++;
        header.createCell(i).setCellValue("numberrange"); i++;
        header.createCell(i).setCellValue("currency"); i++;
        header.createCell(i).setCellValue("alphanumeric"); i++;
        
        int rowNum = 1;
        for (SampleData data : list) {
            Row row = sheet.createRow(rowNum);
            rowNum++;
            String[] array = data.toStringArray();
            for (int j=0; j<array.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(array[j]);
            }
        }
        
        try {
            OutputStream outputStream = new FileOutputStream(EXPORT_XLSX.toFile());
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static List<SampleData> importXLSX() {
        try {
            FileInputStream file = new FileInputStream(EXPORT_XLSX.toFile());
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("Sample Data");

            List<SampleData> liste = new ArrayList<>();
            for (Row row : sheet) {
                SampleData data = new SampleData(
                        row.getCell(0).getStringCellValue(),
                        row.getCell(1).getStringCellValue(),
                        row.getCell(2).getStringCellValue(),
                        row.getCell(3).getStringCellValue(),
                        row.getCell(4).getStringCellValue(),
                        row.getCell(5).getStringCellValue(),
                        row.getCell(6).getStringCellValue(),
                        row.getCell(7).getStringCellValue(),
                        row.getCell(8).getStringCellValue(),
                        row.getCell(9).getStringCellValue(),
                        row.getCell(10).getStringCellValue(),
                        row.getCell(11).getStringCellValue()
                        );
                liste.add(data);
            }
            return liste;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static Path getTMPFile(String postfix) {
        Path tmpdir = Paths.get(System.getProperty ("java.io.tmpdir"));
        Path tmpfile = tmpdir.resolve(String.valueOf(System.currentTimeMillis()) + postfix);
        return tmpfile;
    }
}
