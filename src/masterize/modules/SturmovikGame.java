/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize.modules;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import masterize.Game;
import masterize.Player;
import masterize.Report;
import masterize.Report.Type;

/**
 *
 * @author testi
 */
public class SturmovikGame implements Game {

    public Report createReport(InetAddress address, int port, Type type, boolean passive) {
        byte[] statusPacket = "\\status\\".getBytes();
        DatagramPacket p = new DatagramPacket(statusPacket, statusPacket.length);
        p.setAddress(address);
        p.setPort(port);
        try {
        DatagramSocket s = new DatagramSocket();
        s.setSoTimeout(2000);
        s.send(p);
        DatagramPacket receivePacket = new DatagramPacket(new byte[4096], 4096);
        s.receive(receivePacket);
        Map<String, String> map = new HashMap<String, String>();
        String propertyString = new String(receivePacket.getData(),0,receivePacket.getLength());
        String[] split = propertyString.split("\\\\");
        for (int i = 1; i+1 < split.length; i+=2) {
        map.put(split[i], split[i+1]);
        }
        String serverName = map.remove("hostname");
        String mapName = map.remove("mapname");
        String gameType = map.remove("gametype");
        int numPlayers = -1;
        int maxPlayers = -1;
        try {numPlayers = Integer.parseInt(map.remove("numplayers"));} catch (NumberFormatException ex){}
        try {maxPlayers = Integer.parseInt(map.remove("maxplayers"));} catch (NumberFormatException ex){}
        List<Player> playerList = new LinkedList<Player>();
        for (int i = 0 ; i < numPlayers; i++) {
        String player = map.remove("player_" + i);

        if (player == null) break;
        int score = Integer.MIN_VALUE;
        int ping = -1;
        try { score = Integer.parseInt(map.remove("score_"  + i));} catch (NumberFormatException ex) {}
        try { ping = Integer.parseInt(map.remove("ping_"  + i));} catch (NumberFormatException ex) {}


        playerList.add(new Player(player, score, ping));
        }

        Report newReport = new Report(type, name(), port, passive, serverName, mapName, gameType, numPlayers, maxPlayers, playerList, map);
        
        return newReport;





        } catch (SocketException ex) {return null;}
         catch (SocketTimeoutException ex) {return null;}
        catch (IOException ex) {return null;}
    }

    public String name() {
        return "IL-2 Sturmovik";
    }

}
