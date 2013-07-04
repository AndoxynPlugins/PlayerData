/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.parsers.xml.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.daboross.bukkitdev.playerdata.libraries.dxml.DXMLException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author daboross
 */
public class ExtraDataParser {

    public static void putOnXML(String dataName, String[] data, Element e) {
        e.setAttribute("name", dataName);
        Element dataElement = e.getOwnerDocument().createElement("data");
        for (int i = 0; i < data.length; i++) {
            dataElement.setAttribute("dataline" + i, data[i]);
        }
        e.appendChild(dataElement);
    }

    public static String getNameFromXML(Node node) throws DXMLException {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node n = attributes.item(i);
            if (n.getNodeName().equals("name")) {
                return n.getNodeValue();
            }
        }
        throw new DXMLException("Not ExtraData Element");
    }

    public static String[] getDataFromXML(Node node) throws DXMLException {
        NodeList childNodes = node.getChildNodes();
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node current = childNodes.item(i);
            if (current.getNodeName().equals("#text")) {
                continue;
            }
            if (current.getNodeName().equals("data")) {
                NamedNodeMap list2 = current.getAttributes();
                Map<String, String> beforeData = new HashMap<String, String>();
                for (int k = 0; k < list2.getLength(); k++) {
                    Node n2 = list2.item(k);
                    if (n2.getNodeName().startsWith("dataline")) {
                        beforeData.put(n2.getNodeName(), n2.getNodeValue());
                    } else {
                        throw new DXMLException("Unknown attribute on data child:" + n2.getNodeName());
                    }
                }
                int k = 0;
                while (true) {
                    String str = beforeData.get("dataline" + k++);
                    if (str == null) {
                        break;
                    }
                    data.add(str);
                }

            } else {
                throw new DXMLException("Not ExtraData Element");
            }
        }
        return data.toArray(new String[data.size()]);
    }
}
