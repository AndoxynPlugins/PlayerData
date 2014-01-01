/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.playerdata.parsers.xml;

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
 * @author daboross
 */
public class XMLExtraDataParserV1 {

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
        throw new DXMLException("Invalid ExtraData node! No name attribute!");
    }

    public static String[] getDataFromXML(Node node) throws DXMLException {
        NodeList childNodes = node.getChildNodes();
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node current = childNodes.item(i);
            if (current.getNodeName().equals("#text")) {
                continue;
            } else if (current.getNodeName().equals("data")) {
                NamedNodeMap dataAttributelist = current.getAttributes();
                Map<String, String> beforeData = new HashMap<String, String>();
                for (int k = 0; k < dataAttributelist.getLength(); k++) {
                    Node attributeNode = dataAttributelist.item(k);
                    if (attributeNode.getNodeName().startsWith("dataline")) {
                        beforeData.put(attributeNode.getNodeName(), attributeNode.getNodeValue());
                    } else {
                        throw new DXMLException("Invalid ExtraData node! Unknown attribute on data node: " + attributeNode.getNodeName());
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
                throw new DXMLException("Invalid ExtraData node! Unknown child: " + current.getNodeName());
            }
        }
        return data.toArray(new String[data.size()]);
    }
}
