/**
 * Contributors: muhammad.ahmed@ihsinformatics.com
 */
package org.opensrp.util;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class FileCreator {

    // private static String directory=null;//= System.getProperty("user.home");

    // public FileCreator() {
    // directory = System.getProperty("user.home");
    //
    // }

    public void createFile(String filename, String directory, byte[] content) throws FileNotFoundException, IOException {

        File f = new File(directory);
        if (f.mkdirs()) {

        }
        //System.out.println(s);
        FileOutputStream fos2 = new FileOutputStream(f.getPath() + System.getProperty("file.separator") + filename);
        fos2.write(content);
        fos2.close();

    }

    public String createDirectory(String directory) {

        File file = new File(osDirectorySet(directory));
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }

        return file.getAbsolutePath();
    }

    public boolean createFormFiles(String directory, String formId, byte[] form, byte[] model, byte[] formjson) {

        try {
            //	System.out.println("before creating files "+directory);
            createFile("form.xml", directory, form);
            createFile("model.xml", directory, model);
            createFile("form.json", directory, formjson);
            //	System.out.println("before creating files "+directory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createTextFile(String directory, byte[] context, String formId) {
        try {
            directory = createDirectory(directory);
            createFile(formId + ".txt", directory, context);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createModelFile(String directory, String formId, byte[] context) {
        try {
            directory = createDirectory(directory);
            createFile("model.xml", directory, context);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean createFormFile(String directory, String formId, byte[] context) {
        try {
            directory = createDirectory(directory);
            createFile("form.xml", directory, context);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createFormJsonFile(String directory, String formId, byte[] context) {
        try {
            directory = createDirectory(directory);
            createFile("form.json", directory, context);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String osDirectorySet(String name) {

        if (name.startsWith("/")) {
            name += "/";
            // directory += "/"+name+"/";
        } else {
            // directory += "\\"+name+"\\";
            name += "\\";
        }
        return name;
    }

    private String prettyFormat(String input, int indent) {
        try {
            //			Source xmlInput = new StreamSource(new StringReader(input));
            //			StringWriter stringWriter = new StringWriter();
            //			StreamResult xmlOutput = new StreamResult(new OutputStreamWriter(System.out));
            //
            final InputSource src = new InputSource(new StringReader(input));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();
            System.out.println(xmlString);

            return xmlString;
        } catch (Throwable e) {
            e.printStackTrace();
            // You'll come here if you are using JDK 1.5
            // you are getting an the following exeption
            // java.lang.IllegalArgumentException: Not supported: indent-number
            // Use this code (Set the output property in transformer.
            try {
                Source xmlInput = new StreamSource(new StringReader(input));
                StringWriter stringWriter = new StringWriter();
                StreamResult xmlOutput = new StreamResult(stringWriter);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString();
            } catch (Throwable t) {
                return input;
            }
        }
    }

    public String prettyFormat(String input) {
        return prettyFormat(input, 2);
    }

}
