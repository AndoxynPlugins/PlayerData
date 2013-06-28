package net.daboross.bukkitdev.playerdata;

import net.daboross.dxml.DXMLException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author daboross
 */
public class IPLogin implements Comparable<IPLogin> {

    private long time;
    private String ip;

    /**
     * The Constructor for an IPLogin. You CAN use null IP's if you don't know
     * what it is. If the IP is null it will be replaced with "Unknown".
     */
    public IPLogin(final long time, final String ip) {
        this.time = time;
        this.ip = ip == null ? "Unknown" : ip;
    }

    public IPLogin(long time) {
        this.time = time;
        this.ip = "Unknown";
    }

    public String ip() {
        return ip;
    }

    public long time() {
        return time;
    }

    /**
     * Gets an IPLogin from a string that has been gotten by IPLogin.toString().
     * THis will work over multiple JVM's, and will decode from a regular login
     * (just a long).
     */
    public static IPLogin fromString(final String coded) {
        String[] firstLast = new String[]{coded};
        if (coded.contains("  :  ")) {
            firstLast = coded.split("  :  ");
        } else if (coded.contains("-")) {
            String[] temp = coded.split("-");
            if (temp.length == 2) {
                firstLast = temp;
            } else if (temp.length > 2) {
                String second = "";
                for (int i = 1; i < temp.length; i++) {
                    second += temp[i];
                }
                firstLast = new String[]{temp[0], second};
            }
        }
        if (firstLast.length > 2) {
            System.out.println("Error Parsing String! 1:" + coded);
            return null;
        } else if (firstLast.length == 0) {
            System.out.println("Error Parsing String! 2:" + coded);
            return null;
        } else if (firstLast.length == 1) {
            long time;
            try {
                time = Long.parseLong(coded);
            } catch (Exception e) {
                return null;
            }
            return new IPLogin(time, null);
        } else if (firstLast.length == 2) {
            long time;
            try {
                time = Long.parseLong(firstLast[0]);
            } catch (Exception e) {
                return null;
            }
            String ip = firstLast[1];
            return new IPLogin(time, ip);
        } else {
            System.out.println("Error Parsing String! 3:" + coded);
            return null;
        }
    }

    /**
     * This returns a coded version of the IPLogin. It is in the form of time +
     * " : " + ip.
     */
    @Override
    public String toString() {
        return time + "  :  " + ip;
    }

    public void putDataOnXML(Element e) {
        e.setAttribute("ip", ip);
        e.setAttribute("timestamp", String.valueOf(time));
    }

    public IPLogin(Node n) throws DXMLException {
        NamedNodeMap nnm = n.getAttributes();
        Node ipNode = nnm.getNamedItem("ip");
        if (ipNode == null) {
            throw new DXMLException("Null IP Node when Creating IPLogin");
        }
        ip = ipNode.getNodeValue();
        Node timeNode = nnm.getNamedItem("timestamp");
        if (timeNode == null) {
            throw new DXMLException("Null TimeStamp Node when Creating IPLogin");
        }
        time = Long.valueOf(timeNode.getNodeValue());
    }

    public int compareTo(IPLogin other) {
        Long l1 = this.time;
        Long l2 = other.time();
        return l1.compareTo(l2);
    }
}
