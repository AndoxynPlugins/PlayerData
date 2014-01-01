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

import java.io.File;
import net.daboross.bukkitdev.playerdata.PlayerDataImpl;
import net.daboross.bukkitdev.playerdata.api.PlayerData;
import net.daboross.bukkitdev.playerdata.libraries.dxml.DXMLException;
import net.daboross.bukkitdev.playerdata.libraries.dxml.DXMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLParserFinder {

    public static void save(PlayerData playerData, File file) throws DXMLException {
        XMLFileParserV1.save(playerData, file);
    }

    public static PlayerDataImpl read(File file) throws DXMLException {
        Document document = DXMLHelper.readDocument(file);
        Node root = document.getFirstChild();
        while (true) {
            if (root == null) {
                break;
            } else if (root.getNodeName().equalsIgnoreCase("playerdata")) {
                break;
            }
            root = root.getNextSibling();
        }
        if (root == null) {
            throw new DXMLException("Invalid Save File " + file.getAbsolutePath() + "! Root node incorrectly named!");
        }
        Node versionNode = root.getFirstChild();
        while (true) {
            if (versionNode == null) {
                break;
            } else if (versionNode.getNodeName().equalsIgnoreCase("version")) {
                break;
            }
            versionNode = versionNode.getNextSibling();
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
