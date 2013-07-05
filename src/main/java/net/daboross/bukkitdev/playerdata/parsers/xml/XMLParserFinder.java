/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.parsers.xml;

import java.io.File;
import net.daboross.bukkitdev.playerdata.PlayerDataImpl;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.libraries.dxml.DXMLException;
import net.daboross.bukkitdev.playerdata.libraries.dxml.DXMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author daboross
 */
public class XMLParserFinder {

    public static void save(PlayerData playerData, File file) throws DXMLException {
        XMLFileParserV1.save(playerData, file);
    }

    public static PlayerDataImpl read(File file) throws DXMLException {
        Document document = DXMLHelper.readDocument(file);
        Node versionNode = null;
        Node rootTest = document.getFirstChild();
        while (true) {
            if (rootTest == null) {
                break;
            } else if (rootTest.getNodeName().equalsIgnoreCase("version")) {
                versionNode = rootTest;
            }
            rootTest = rootTest.getNextSibling();
        }
        int version;
        if (versionNode == null) {
            version = 1;
        } else {
            Node versionText = versionNode.getFirstChild();
            while (!versionText.getNodeName().equals("#text")) {
                versionText = versionText.getNextSibling();
            }
            if (versionText == null) {
                throw new DXMLException("Invalid Save File " + file.getAbsolutePath() + "! Version node has no children!");
            }
            try {
                version = Integer.parseInt(versionText.getNodeValue());
            } catch (NumberFormatException nfe) {
                throw new DXMLException("Invalid Save File " + file.getAbsolutePath() + "! Version node's first child isn't an integer!");
            }
        }
        if (version == 1) {
            try {
                return XMLFileParserV1.read(document);
            } catch (DXMLException dxmle) {
                throw new DXMLException("Invalid Version1 Save File " + file.getAbsoluteFile() + "! " + dxmle.getMessage());
            }
        } else {
            throw new DXMLException("Save version " + version + " is unknown to this version of PlayerData! File: " + file.getAbsolutePath());
        }
    }
}
