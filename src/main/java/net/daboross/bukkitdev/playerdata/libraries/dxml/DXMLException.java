/*
 * Author: Dabo Ross
 * Website: www.daboross.net
 * Email: daboross@daboross.net
 */
package net.daboross.bukkitdev.playerdata.libraries.dxml;

/**
 *
 * @author daboross
 */
public class DXMLException extends Exception {

    private static final long serialVersionUID = 1L;

    public DXMLException() {
        super();
    }

    public DXMLException(String message) {
        super(message);
    }

    public DXMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public DXMLException(Throwable cause) {
        super(cause);
    }
}
