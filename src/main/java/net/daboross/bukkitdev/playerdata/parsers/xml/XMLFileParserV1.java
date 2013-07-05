/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.parsers.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.daboross.bukkitdev.playerdata.PlayerDataImpl;
import net.daboross.bukkitdev.playerdata.api.LoginData;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.libraries.dxml.DXMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static net.daboross.bukkitdev.playerdata.libraries.dxml.DXMLHelper.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author daboross
 */
public class XMLFileParserV1 {
    
    public static void save(PlayerData pd, File fileResult) throws DXMLException {
        Document document = newDocument();
        Element root = document.createElement("playerdata");
        root.appendChild(createElement(document, "version", "1"));
        document.appendChild(root);
        root.appendChild(createElement(document, "username", pd.getUsername()));
        root.appendChild(createElement(document, "displayname", pd.getDisplayname()));
        root.appendChild(createElement(document, "timeplayed", String.valueOf(pd.getTimePlayed())));
        {
            List<? extends LoginData> logins = pd.getAllLogins();
            Element logInsElement = document.createElement("logins");
            for (int i = 0; i < logins.size(); i++) {
                Element e = document.createElement("login" + i);
                XMLLoginDataParserV1.putDataOnXML(logins.get(i), e);
                logInsElement.appendChild(e);
            }
            root.appendChild(logInsElement);
        }
        {
            List<Long> logouts = pd.getAllLogouts();
            Element logOutsElement = document.createElement("logouts");
            for (int i = 0; i < logouts.size(); i++) {
                logOutsElement.appendChild(createElement(document, String.valueOf("logout" + i), logouts.get(i).toString()));
            }
            root.appendChild(logOutsElement);
        }
        {
            Element otherData = document.createElement("data");
            for (String dataName : pd.getExtraDataNames()) {
                Element e = document.createElement(dataName);
                XMLExtraDataParserV1.putOnXML(dataName, pd.getExtraData(dataName), e);
                otherData.appendChild(e);
            }
            root.appendChild(otherData);
        }
        writeXML(document, fileResult);
    }
    
    public static PlayerDataImpl read(Document document) throws DXMLException {
        Node root = null;
        Node rootTest = document.getFirstChild();
        while (true) {
            if (rootTest == null) {
                break;
            } else if (rootTest.getNodeName().equalsIgnoreCase("playerdata")) {
                root = rootTest;
            }
            rootTest = rootTest.getNextSibling();
        }
        if (root == null) {
            throw new DXMLException("No root element found!");
        }
        if (!root.hasChildNodes()) {
            throw new DXMLException("Root element doesn't have any children!");
        }
        NodeList list = root.getChildNodes();
        String username = null;
        String displayname = null;
        String timePlayed = null;
        Node logouts = null;
        Node logins = null;
        Node data = null;
        for (int i = 0; i < list.getLength(); i++) {
            Node current = list.item(i);
            if (current.getNodeName().equals("username")) {
                username = current.getFirstChild().getNodeValue();
            } else if (current.getNodeName().equals("displayname")) {
                displayname = current.getFirstChild().getNodeValue();
            } else if (current.getNodeName().equals("timeplayed")) {
                timePlayed = current.getFirstChild().getNodeValue();
            } else if (current.getNodeName().equals("logins")) {
                logins = current;
            } else if (current.getNodeName().equals("logouts")) {
                logouts = current;
            } else if (current.getNodeName().equals("data")) {
                data = current;
            } else if (!(current.getNodeName().equals("#text") || current.getNodeName().equals("version"))) {
                throw new DXMLException("Root element child node " + current.getNodeName() + " unknown!");
            }
        }
        if (logouts == null) {
            throw new DXMLException("Root doesn't contain logouts node!");
        }
        if (logins == null) {
            throw new DXMLException("Root doesn't contain logins node!");
        }
        if (username == null) {
            throw new DXMLException("Root doesn't contain username node!");
        }
        if (displayname == null) {
            throw new DXMLException("Root doesn't contain displayname node!");
        }
        if (timePlayed == null) {
            throw new DXMLException("Root doesn't  contain timePlayed node!");
        }
        if (data == null) {
            throw new DXMLException("Root doesn't contain data node!");
        }
        NodeList logoutList = logouts.getChildNodes();
        ArrayList<Long> logoutsFinal = new ArrayList<Long>(logoutList.getLength());
        NodeList loginList = logins.getChildNodes();
        ArrayList<LoginData> loginsFinal = new ArrayList<LoginData>(loginList.getLength());
        for (int i = 0; i < logoutList.getLength(); i++) {
            Node current = logoutList.item(i);
            if (current.getNodeName().equals("#text")) {
                continue;
            }
            Node child = current.getFirstChild();
            if (child == null) {
                throw new DXMLException("Logout node " + current.getNodeName() + " doesn't have any children!");
            }
            try {
                logoutsFinal.add(Long.valueOf(child.getNodeValue()));
            } catch (NumberFormatException nfe) {
                throw new DXMLException("Logout node " + current.getNodeName() + "'s first child isn't an integer!");
            }
        }
        for (int i = 0; i < loginList.getLength(); i++) {
            Node current = loginList.item(i);
            if (current.getNodeName().equals("#text")) {
                continue;
            }
            try {
                loginsFinal.add(XMLLoginDataParserV1.fromXML(current));
            } catch (DXMLException dxmle) {
                throw new DXMLException("IPLogin " + current.getNodeName() + " Error!: " + dxmle.getMessage());
            }
        }
        NodeList dataList = data.getChildNodes();
        Map<String, String[]> extraData = new HashMap<String, String[]>();
        for (int i = 0; i < dataList.getLength(); i++) {
            Node current = dataList.item(i);
            if (current.getNodeName().equals("#text")) {
                continue;
            }
            try {
                extraData.put(XMLExtraDataParserV1.getNameFromXML(current), XMLExtraDataParserV1.getDataFromXML(current));
            } catch (DXMLException dxmle) {
                throw new DXMLException("Extra Data " + current.getNodeName() + " Error: " + dxmle.getMessage());
            }
        }
        long timePlayedLong = 0;
        try {
            timePlayedLong = Long.parseLong(timePlayed);
        } catch (NumberFormatException nfe) {
            throw new DXMLException("TimePlayed node isn't a long!");
        }
        return new PlayerDataImpl(username, displayname, loginsFinal, logoutsFinal, timePlayedLong, extraData);
    }
}
