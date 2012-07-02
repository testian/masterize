/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize.modules;
import java.io.IOException;
import java.io.InputStream;
import masterize.Environment;
import masterize.GameReporter;
import masterize.Report;
import masterize.ReportListener;
import java.io.File;
import masterize.InitException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import masterize.Player;
/**
 *
 * @author testi
 */

public class SturmovikReporter implements GameReporter {
    private Environment environment;
    private ReportListener rl;
    private int port;
    private int maxPlayers;
    private String serverName;
    private List<Player> playerList;
    private String map;
    private SturmovikGame game;
    private Report report;
    public SturmovikReporter(ReportListener rl) {
    this.rl = rl;
    environment = null;
    port = -1;
    maxPlayers = -1;
    serverName = null;
    playerList = new ArrayList<Player>();
    map = null;
    game = new SturmovikGame();
    report = null;
    }

    public boolean isPassive() {
    return true;
    }

    public boolean init(Environment environment) throws InitException {

        File root = environment.getWorkingDirectory();
        File config = null;
        File executable = null;
        for (File f : root.listFiles()) {
        if ("il2server.exe".equals(f.getName().toLowerCase()) && f.isFile())
        executable = f;
        if ("confs.ini".equals(f.getName().toLowerCase()) && f.isFile())
        config = f;
        }
        if (config == null || executable == null) return false;
        
        
        
        parseConfig(config);

        Report netReport = null;
        try {netReport = game.createReport(InetAddress.getLocalHost(), port, Report.Type.STARTUP,isPassive());} catch (UnknownHostException ex){}

        if (netReport != null) report = netReport;
        
        emitStartReport();
        this.environment = environment;
        return true;
    }

    private void emitStartReport() {
    //Report report = new Report(Report.Type.STARTUP,gameName(),port,isPassive(),serverName,map,null,playerList.size(),maxPlayers, playerList);
    rl.onReport(report);
    }
    private void emitUpdateReport() {
    //Report report = new Report(Report.Type.UPDATE, gameName(),port,isPassive(),serverName,map,null,playerList.size(),maxPlayers, playerList);
    rl.onReport(report);
    }
    private void emitShutdownReport() {
    //Report report = new Report(Report.Type.EXIT, gameName(),port,isPassive(),serverName,map,null,playerList.size(),maxPlayers, playerList);
    rl.onReport(report);
    }

    private void createReport(Report.Type type) {
    report = new Report(type, gameName(),port,isPassive(),serverName,map,null,playerList.size(),maxPlayers, playerList);
    }

    private void parseConfig(File config) throws InitException {
    try {
        BufferedReader reader = new BufferedReader(new FileReader(config));
        Map<String, String> configEntries = new HashMap<String, String>();
        String line;
        while ((line = reader.readLine()) != null) {
        //String[] split = line.split("=");
        int split = line.indexOf("=");
        if (split > -1) {
        configEntries.put(line.substring(0,split).trim(), line.substring(split+1,line.length()));
        }
        }

        String portString = configEntries.get("localPort");
        if (portString == null) throw new InitException("No localPort field specified in " + config);
        try {
        port = Integer.parseInt(portString.trim());
        } catch (NumberFormatException ex) {
        throw new InitException("Not an integer value specified for localPort in " + config);
        }

        String maxPlayersString = configEntries.get("serverChannels");
        if (maxPlayersString == null) throw new InitException("No serverChannels field specified in " + config);
        try {
        maxPlayers = Integer.parseInt(maxPlayersString.trim());
        } catch (NumberFormatException ex) {
        throw new InitException("Not an integer value specified for serverChannels in " + config);
        }

        serverName = configEntries.get("serverName");
        if (serverName == null) {throw new InitException("No serverName field specified in " + config);}
        createReport(Report.Type.STARTUP);


    } catch (IOException ex) {throw new InitException("Error while reading " + config + ": " + ex.getMessage());}
    }

    public String gameName() {
        return game.name();
    }

    public String[] getCommandline() {
    if (environment == null) throw new IllegalStateException("Must be called after successfull init");
    if (environment.getOs() == Environment.OS.LINUX) {
    return new String[] {"wine", "il2server.exe"};
    }
    return new String[] {"il2server.exe"};
    }

    public void parseStream(InputStream stream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String line;
        while ((line = reader.readLine()) != null) {
        System.out.println(line);
        String startsWith = "Loading mission ";
        String endsWith = "...";
        if (line.startsWith(startsWith) && line.endsWith(endsWith)) {
        String newMap = line.substring(startsWith.length(), line.length() - endsWith.length());
        if (map == null || !map.equals(newMap)) {
            map = newMap;
            emitUpdateReport();
        }
        continue;
        }

        startsWith = "Chat: --- ";
        endsWith =  "joins the game.";

        if (line.startsWith(startsWith) && line.endsWith(endsWith)) {
        String newPlayer = line.substring(startsWith.length(), line.length() - endsWith.length());
        playerList.add(new Player(newPlayer,0,-1));
        emitUpdateReport();
        continue;
        }

        endsWith = "has left the game.";
        if (line.startsWith(startsWith) && line.endsWith(endsWith)) {
        String newPlayer = line.substring(startsWith.length(), line.length() - endsWith.length());
        playerList.remove(new Player(newPlayer,0,-1));
        emitUpdateReport();
        continue;
        }

        }
        
        emitShutdownReport();
    }

}
