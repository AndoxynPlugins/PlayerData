package net.daboross.bukkitdev.playerdata;

import java.io.File;
import net.daboross.xmlhelpers.DXMLException;
import net.daboross.xmlhelpers.DXMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static net.daboross.xmlhelpers.DXMLHelper.createElement;

/**
 *
 * @author daboross
 */
public class XMLFileParser {

    public static void writeToFile(PData pData, File fileResult) throws DXMLException {
        Document document = DXMLHelper.newDocument();
        Element root = document.createElement("playerdata");
        document.appendChild(root);
        root.appendChild(createElement(document, "username", pData.userName()));
        root.appendChild(createElement(document, "displayname", pData.nickName(false)));
        root.appendChild(createElement(document, "timeplayed", String.valueOf(pData.timePlayed())));
        {
            IPLogin[] logIns = pData.logIns();
            Element logInsElement = document.createElement("logins");
            for (int i = 0; i < logIns.length; i++) {
                logInsElement.appendChild(createElement(document, String.valueOf("login" + i), logIns[i].toString()));
            }
            root.appendChild(logInsElement);
        }
        {
            Long[] logOuts = pData.logOuts();
            Element logOutsElement = document.createElement("logouts");
            for (int i = 0; i < logOuts.length; i++) {
                logOutsElement.appendChild(createElement(document, String.valueOf("logout" + i), logOuts[i].toString()));
            }
            root.appendChild(logOutsElement);
        }
        {
            Element otherData = document.createElement("otherData");
            for (Data data : pData.getData()) {
                otherData.appendChild(data.toXML(document));
            }
            root.appendChild(otherData);
        }
        DXMLHelper.writeXML(document, fileResult);
    }
}
