/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.parsers.xml;

import net.daboross.bukkitdev.playerdata.LoginDataImpl;
import net.daboross.bukkitdev.playerdata.api.LoginData;
import net.daboross.bukkitdev.playerdata.libraries.dxml.DXMLException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author daboross
 */
public class XMLLoginDataParserV1 {

    public static void putDataOnXML(LoginData data, Element e) {
        e.setAttribute("ip", data.getIP());
        e.setAttribute("timestamp", String.valueOf(data.getDate()));
    }

    public static LoginData fromXML(Node n) throws DXMLException {
        NamedNodeMap nnm = n.getAttributes();
        Node ipNode = nnm.getNamedItem("ip");
        if (ipNode == null) {
            throw new DXMLException("Invalid IPLogin node doesn't contain ip node!");
        }
        String ip = ipNode.getNodeValue();
        Node timeNode = nnm.getNamedItem("timestamp");
        if (timeNode == null) {
            throw new DXMLException("Invalid IPLogin node doesn't contain timestamp node!");
        }
        long date;
        try {
            date = Long.parseLong(timeNode.getNodeValue());
        } catch (NumberFormatException nfe) {
            throw new DXMLException("Invalid IPLogin node's timestamp isn't an integer!");
        }
        return new LoginDataImpl(date, ip);
    }
}
