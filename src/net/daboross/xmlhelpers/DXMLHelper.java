package net.daboross.xmlhelpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author daboross
 */
public class DXMLHelper {

    private static final TransformerFactory transformerFactory;
    private static final Transformer transformer;
    private static final DocumentBuilderFactory docFactory;
    private static final DocumentBuilder docBuilder;

    static {
        transformerFactory = TransformerFactory.newInstance();
        Transformer transformerTemp;
        try {
            transformerTemp = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(DXMLHelper.class.getName()).log(Level.SEVERE, null, ex);
            transformerTemp = null;
        }
        if (transformerTemp != null) {
            transformerTemp.setOutputProperty(OutputKeys.INDENT, "yes");
            transformerTemp.setOutputProperty(OutputKeys.STANDALONE, "no");
            try {
                transformerTemp.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            } catch (IllegalArgumentException ex) {
            }
        }
        transformer = transformerTemp;
        docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilderTemp;
        try {
            docBuilderTemp = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DXMLHelper.class.getName()).log(Level.SEVERE, null, ex);
            docBuilderTemp = null;
        }
        docBuilder = docBuilderTemp;
    }

    public static Document newDocument() throws DXMLException {
        if (docBuilder == null) {
            throw new DXMLException("Null DocBuilder");
        }
        return docBuilder.newDocument();
    }

    public static Document readDocument(File source) throws DXMLException {
        if (docBuilder == null) {
            throw new DXMLException("Null DocBuilder");
        }
        try {
            return docBuilder.parse(source);
        } catch (SAXException ex) {
            throw new DXMLException(ex);
        } catch (IOException ex) {
            throw new DXMLException(ex);
        }
    }

    public static Document readDocument(InputStream source) throws DXMLException {
        if (docBuilder == null) {
            throw new DXMLException("Null DocBuilder");
        }
        try {
            return docBuilder.parse(source);
        } catch (SAXException ex) {
            throw new DXMLException(ex);
        } catch (IOException ex) {
            throw new DXMLException(ex);
        }
    }

    public static void writeXML(Document source, File file) throws DXMLException {
        if (source == null || file == null) {
            throw new IllegalArgumentException("1 or more null arguments");
        }
        Result result = new StreamResult(file);
        writeXML(source, result);
    }

    public static void writeXML(Document source, Result result) throws DXMLException {
        if (source == null || result == null) {
            throw new IllegalArgumentException("1 or more null arguments");
        }
        DOMSource sourceDOM = new DOMSource(source);
        writeXML(sourceDOM, result);
    }

    public static void writeXML(Source source, Result result) throws DXMLException {
        if (source == null || result == null) {
            throw new IllegalArgumentException("1 or more null arguments");
        }
        if (transformer == null) {
            throw new DXMLException("Null Transformer");
        }
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            throw new DXMLException(ex);
        }
    }

    public static void writeXML(Source source, File file) throws DXMLException {
        if (source == null || file == null) {
            throw new IllegalArgumentException("1 or more null arguments");
        }
        StreamResult result = new StreamResult(file);
        writeXML(source, result);
    }

    public static Element createElement(Document d, String name, String value) {
        Element e = d.createElement(name);
        e.appendChild(d.createTextNode(value));
        return e;
    }
}
