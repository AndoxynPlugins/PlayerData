/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.parsers.xml.v1;

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
public class LoginDataParser {

    public static void putDataOnXML(LoginData data, Element e) {
        e.setAttribute("ip", data.getIP());
        e.setAttribute("timestamp", String.valueOf(data.getDate()));
    }

    public static LoginDataImpl fromXML(Node n) throws DXMLException {
        NamedNodeMap nnm = n.getAttributes();
        Node ipNode = nnm.getNamedItem("ip");
        if (ipNode == null) {
            throw new DXMLException("Null IP Node when Creating IPLogin");
        }
        String ip = ipNode.getNodeValue();
        Node timeNode = nnm.getNamedItem("timestamp");
        if (timeNode == null) {
            throw new DXMLException("Null TimeStamp Node when Creating IPLogin");
        }
        long date = Long.parseLong(timeNode.getNodeValue());
        return new LoginDataImpl(date, ip);
    }
}
