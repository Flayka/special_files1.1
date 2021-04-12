import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ParseException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCsv = "data_csv.csv";
        List<Employee> listCsv = parseCSV(columnMapping, fileNameCsv);
        String fileNameXml = "data.xml";
        String fileJson = "data_json.json";

        //1st task
        String json = listToJson(listCsv);
        writeString(json, "data_json.json");
        //2nd task
        List<Employee> listXml = parseXML(fileNameXml);
        writeString(listToJson(listXml), "data_json2.json");
        //3rd task
        String jsonRead = readString(fileJson);
        List<Employee> list = jsonToList(jsonRead);
        for (Employee print : list) {
            System.out.println(print);
        }
    }

    public static List<Employee> jsonToList(String jsonRead) throws ParseException {
        List<Employee> list = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        JSONArray array = (JSONArray) jsonParser.parse(jsonRead);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        for (Object o : array) {
            String string = o.toString();
            Employee employee = gson.fromJson(string, Employee.class);
            list.add(employee);
        }
        return list;
    }

    public static String readString(String fileJson) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileJson))) {
            String s;
            while ((s = br.readLine()) != null) {
                return s;
            }
        } catch (IOException ex) {
            System.out.println("File .json have not been read");
        }
        return null;
    }

    public static List<Employee> parseXML(String fileNameXml) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileNameXml));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node elem = nodeList.item(i);
            if (elem.getNodeType() != Node.TEXT_NODE) {
                NodeList nodeList2 = elem.getChildNodes();
                list.add(new Employee(Long.parseLong(nodeList2.item(1).getTextContent()),
                        nodeList2.item(3).getTextContent(),
                        nodeList2.item(5).getTextContent(),
                        nodeList2.item(7).getTextContent(),
                        Integer.parseInt(nodeList2.item(9).getTextContent())));
            }
        }
        return list;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new
                FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (Exception e) {
            System.out.println("Error! Data have not wrote to .json file");
        }
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> cpms = new ColumnPositionMappingStrategy<>();
            cpms.setType(Employee.class);
            cpms.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(cpms)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
        } catch (Exception e) {
            System.out.println("Error! Data have not wrote to .csv");
        }
        return null;
    }
}
