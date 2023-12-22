package deringo.jexieporter;

import java.awt.Desktop;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private static final Path IMPORT_CSV = Paths.get("src/main/resources/sampledata.csv");
    private static final Path EXPORT_CSV = getTMPFile();
    
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
            // Converting the Object to JSONString
            String jsonString = mapper.writeValueAsString(sampleData);
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
    
    private static Path getTMPFile() {
        Path tmpdir = Paths.get(System.getProperty ("java.io.tmpdir"));
        Path tmpfile = tmpdir.resolve(String.valueOf(System.currentTimeMillis()) + ".csv");
        return tmpfile;
    }
}
