/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author testi
 */
public class ReportTest {

    public static void main(String[] args) {
    System.out.println("Report serialization test");

    java.util.List<Player> playerList = new java.util.ArrayList<Player>();
    playerList.add(new Player("stephan",1,2));
    playerList.add(new Player("Anderer scheiss Spieler",2,3));
    playerList.add(new Player("Blüdspieler SPIELT NACKT!!!!",3,4));
    Map<String, String> map = new HashMap<String, String>();
    map.put("SCHEISS COCKPITS AN?", "Eh voll");
    map.put("Und weiter?", "Nichts");
    Report report = new Report(Report.Type.STARTUP, "", 1337,true, null,"Schöne Map","BloederGameType", 50, 61, playerList, map);
    System.out.println(report);
    byte[] array = report.asPacket();
    for (int i = 0 ; i < array.length; i++) {
    char c = (char)array[i];
    System.out.print(c);
    System.out.print("|");
    }
    System.out.println();
    Report newReport = Report.createFromPacket(array);
    System.out.println(newReport);
    for (Player p : report.getPlayerList()) {
    System.out.println(p.getName() + " " +p.getScore() + " " + p.getPing());
    }
    for (Map.Entry<String, String> e : report.getProperties().entrySet()) {
    System.out.println("Entry: " + e);
    }

    }

}
