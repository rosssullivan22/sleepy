package IO;

import java.io.*;

import encryption.Encryptor;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.security.KeyException;
import java.util.Map;

public class FileHandler {

    private static File file = new File("config.xml");

    public static String getValueFromXMLForKey(String key) throws ParserConfigurationException, IOException, SAXException, KeyException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file.getName());

        if (elementExists(doc, key)) {
            String rawValue = doc.getElementsByTagName(key).item(0).getTextContent();
            return Encryptor.decrypt(rawValue);
        } else {
            throw new KeyException("key not found");
        }
    }

    public static void writeToXML(Map<String, String> values) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc;
        Element rootElement;

        if (file.exists()) {
            doc = dBuilder.parse(file.getName());
            rootElement = doc.getDocumentElement();
        } else {
            doc = dBuilder.newDocument();
            rootElement = doc.createElement("config");
            doc.appendChild(rootElement);
        }

        for (String key : values.keySet()) {
            String encryptedValue = Encryptor.encrypt(values.get(key));

            if (elementExists(doc, key)) {
                doc.getElementsByTagName(key).item(0).setTextContent(encryptedValue);
            } else {
                Element element = doc.createElement(key);
                element.appendChild(doc.createTextNode(encryptedValue));
                rootElement.appendChild(element);
            }
        }

        saveXMLToFile(doc);
    }

    private static boolean elementExists(Document doc, String key) {
        try {
            Node e = doc.getElementsByTagName(key).item(0);
            return e != null;
        } catch (IndexOutOfBoundsException ignored) {
            return false;
        }
    }

    public static void configFileCreator() {
        if (!file.exists()) {
            try {

                file.createNewFile();

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();
                Element rootElement = doc.createElement("config");
                doc.appendChild(rootElement);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeValueForKey(String key) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file.getName());

            Element root = doc.getDocumentElement();

            if (!elementExists(doc, key)) {
                return;
            }

            Node nodeToRemove = root.getElementsByTagName(key).item(0);
            System.out.println(nodeToRemove.getTextContent());
            root.removeChild(nodeToRemove);

            saveXMLToFile(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void saveXMLToFile(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

}
