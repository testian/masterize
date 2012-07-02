/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;

import java.net.InetAddress;

/**
 *
 * @author testi
 */
public interface Game {
public String name();
public Report createReport(InetAddress address, int port, Report.Type type, boolean passive);
}
