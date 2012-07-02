/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
/**
 *
 * @author testi
 */
public class MasterReportListener implements ReportListener {

    private InetAddress masterServer;
    private int port;
    private static final long KEEPALIVE_INTERVAL = 4 * 60 * 1000;
    private Timer timer;

    public MasterReportListener(InetAddress masterServer, int port) {
        this.masterServer = masterServer;
        this.port = port;
        timer = new Timer(true);
    }


    public void onReport(final Report report) {
        if (report.getType() != Report.Type.KEEPALIVE)
        {
        timer.cancel();
        timer = new Timer(true);
        if (report.getType() != Report.Type.EXIT && !report.isPassive()) {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                onReport(report.generateKeepAlive());
            }
        
        }, KEEPALIVE_INTERVAL, KEEPALIVE_INTERVAL);
        }
        }

        byte[] packet = report.asPacket();
        DatagramPacket dp = new DatagramPacket(packet, packet.length,masterServer,port);
        try {
        new DatagramSocket().send(dp);
        System.out.println("Sent report: " + report);
        }
        
        catch (SocketException ex) {System.err.println("Error sending report to masterserver: " + ex.getMessage());}
        catch (IOException ex) {System.err.println("Error sending report to masterserver: " + ex.getMessage());}
    }

}
