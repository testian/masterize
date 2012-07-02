/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;
import java.util.List;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author testi
 */
public class Report {
private String gameName;
private String serverName;
private String map;
private int port;
private int playerCount;
private int maxPlayerCount;
private Type type;
private List<Player> playerList;
private String gameType;
private static final String PACKET_HEADER = "MASTERIZE";
private boolean passive;
private Map<String, String> properties;

public static Report createFromPacket(byte[] packet) {
byte[] header = PACKET_HEADER.getBytes();
for (int i = 0 ; i < header.length; i++) {
if (packet[i] != header[i]) return null;
}
//Header match
List<byte[]> arrays = parseFields(packet, header.length);

int port = -1;
int playerCount = -1;
int maxPlayerCount = -1;
Type type = null;
String gameName = null;
String serverName = null;
String map = null;
String gameType = null;
List<Player> playerList = new ArrayList<Player>();
String playerName = null;
int playerScore = Integer.MIN_VALUE;
String key = null;
Map<String, String> newProperties = new HashMap<String, String>();

boolean passive = false;

for (byte[] field : arrays) {

    char tc  = (char)field[0];
    
    if (tc == 'P') port = bytes2Int(field,1);
    else if (tc == 'C') playerCount = bytes2Int(field,1);
    else if (tc == 'X') maxPlayerCount = bytes2Int(field,1);
    else if (tc == 'H') playerScore = bytes2Int(field,1);
    else if (tc == 'D') {

        int playerPing = bytes2Int(field,1);
        if (playerName != null) {
        playerList.add(new Player(playerName, playerScore, playerPing));
        }
        playerName = null;
        playerScore = -1;

}

    else if (tc == 'T') {
        try {
            type = Type.valueOf(new String(field,1,field.length-1));
        } catch (IllegalArgumentException ex) {type = null;}
    }
    else if (tc == 'G') { gameName = new String(field,1,field.length-1); }
    else if (tc == 'N') { serverName = new String(field,1,field.length-1);}
    else if (tc == 'M') { map = new String(field,1,field.length-1);}
    else if (tc == 'S') { gameType = new String(field,1,field.length-1);}
    else if (tc == 'L') { playerName = new String(field,1,field.length-1);}
    else if (tc == 'A') { passive = "PASSIVE".equals(new String(field,1,field.length-1));}
    else if (tc == 'A') { new String(field,1,field.length-1);}
    else if (tc == 'K') { key = new String(field,1,field.length-1);}
    else if (tc == 'V') {
    if (key != null) {
        newProperties.put(key,new String(field,1,field.length-1));
        key = null;
    }
    }




}
return new Report(type,  gameName,  port, passive, serverName,  map, gameType,  playerCount, maxPlayerCount,playerList, newProperties);

}

private static List<byte[]> parseFields(byte[] packet, int offset) {
List<byte[]> arrays = new ArrayList<byte[]>();
int position = offset;
while (position < packet.length) {

    char tc = (char)packet[position];
    
    
    if (position+5 > packet.length) {return arrays;}
    byte[] intType = new byte[5];
    for (int i = 0 ; i < intType.length; i++) {
    intType[i] = packet[position+i];
    }
    position+=intType.length;
    if (tc == 'P' || tc == 'C' || tc == 'X' || tc == 'H' || tc == 'D') {
        arrays.add(intType);
    } else {
        int byteStringLength = bytes2Int(intType,1);
        if (byteStringLength < 0 || byteStringLength+position>packet.length) return arrays;
        byte[] stringType = new byte[byteStringLength+1];
        stringType[0] = intType[0];
        System.arraycopy(packet, position, stringType, 1, byteStringLength);
        arrays.add(stringType);
        position+=byteStringLength;
    }

    

}
return arrays;
}

public byte[] asPacket() {

    List<byte[]> arrays = new ArrayList<byte[]>();

    byte[] header = PACKET_HEADER.getBytes();
    arrays.add(header);
    byte[] typeField = createField(type.toString(),'T');
    arrays.add(typeField);
    byte[] gameNameField = createField(gameName,'G');
    arrays.add(gameNameField);
    byte[] portField = createField(port,'P');
    arrays.add(portField);

    //Optional ones
    byte[] serverNameField = createField(serverName,'N');
    arrays.add(serverNameField);
    byte[] mapField = createField(map,'M');
    arrays.add(mapField);
    byte[] gameTypeField = createField(gameType,'S');
    arrays.add(gameTypeField);
    byte[] playerCountField = createField(playerCount,'C');
    arrays.add(playerCountField);
    byte[] maxPlayerCountField = createField(maxPlayerCount,'X');
    arrays.add(maxPlayerCountField);
    byte[] playerListField = createField(playerList);
    arrays.add(playerListField);
    byte[] passiveField = createField((passive ? "PASSIVE" : "ACTIVE"),'A');
    arrays.add(passiveField);
    for (Map.Entry<String, String> e : properties.entrySet()) {
    byte[] keyField = createField(e.getKey(),'K');
    arrays.add(keyField);
    byte[] valueField = createField(e.getValue(),'V');
    arrays.add(valueField);
    }



    int totalSize = 0;
    for (byte[] array : arrays) {
    totalSize+=array.length;
    }
    byte[] finalArray = new byte[totalSize];

int position = 0;
for (byte[] array : arrays) {
    System.arraycopy(array, 0, finalArray, position, array.length);
    position+= array.length;
}

return finalArray;



}

private static byte[] createField(List<Player> list) {
List<byte[]> arrays = new ArrayList<byte[]>();
int totalSize = 0;
for (Player p : list) {
byte[] arrayName = createField(p.getName(), 'L');
arrays.add(arrayName);
byte[] arrayScore = createField(p.getScore(), 'H');
arrays.add(arrayScore);
byte[] arrayPing = createField(p.getPing(), 'D');
arrays.add(arrayPing);
totalSize+=arrayName.length+arrayScore.length+arrayPing.length;
}
byte[] finalArray = new byte[totalSize];


int position = 0;
for (byte[] array : arrays) {
    
    System.arraycopy(array, 0, finalArray, position, array.length);
    position+= array.length;
}
return finalArray;

}

private static byte[] createField(String fieldName, char fieldType) {
if (fieldName == null) {return new byte[0];}
byte[] bytes = fieldName.getBytes();
byte[] fieldBytes = new byte[bytes.length+5];
byte[] lengthCode = int2Bytes(bytes.length);
fieldBytes[0] = (byte)fieldType;
for (int i = 0; i < 4; i++) {
fieldBytes[i+1] = lengthCode[i];
}
for (int i = 0; i < bytes.length; i++) {
fieldBytes[i+5] = bytes[i];
}

return fieldBytes;


}
private static byte[] createField(int fieldValue, char fieldType) {
byte[] bytes = int2Bytes(fieldValue);
byte[] fieldBytes = new byte[5];
fieldBytes[0] = (byte)fieldType;
for (int i = 0; i < bytes.length; i++) {
fieldBytes[i+1] = bytes[i];
}
return fieldBytes;
}

private static byte[] int2Bytes(int value) {
ByteBuffer byteBuffer = ByteBuffer.allocate(4);
byteBuffer.asIntBuffer().put(value);
byteBuffer.rewind();
byte[] lengthCode = new byte[4];
byteBuffer.get(lengthCode);
return lengthCode;
}
private static int bytes2Int(byte[] bytes, int offset) {
ByteBuffer byteBuffer = ByteBuffer.allocate(4);
byteBuffer.put(bytes, offset, 4);
byteBuffer.rewind();
return byteBuffer.asIntBuffer().get();
}





public Report(Type type, String gameName, int port, boolean passive, String serverName, String map, String gameType, int playerCount, int maxPlayerCount, List<Player> playerList) {
this(type,gameName,port,passive,serverName,map,gameType,playerCount,maxPlayerCount,playerList,null);
}

public Report(Type type, String gameName, int port, boolean passive, String serverName, String map, String gameType, int playerCount, int maxPlayerCount, List<Player> playerList, Map<String, String> properties) {
        if (type == null || gameName == null) {throw new IllegalArgumentException("type, gameName and port must be specified");}
        this.gameName = gameName;
        this.serverName = serverName;
        this.map = map;
        this.gameType = gameType;
        if (port < 1 || port > 65535) {throw new IllegalArgumentException("port must be within range of 1-65535");}
        this.port = port;
        this.passive = passive;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.type = type;
        if (playerList != null)
        this.playerList = playerList;
        else
        this.playerList = new ArrayList<Player>();

        if (properties != null)
        this.properties = properties;
        else
        this.properties = new HashMap<String,String>();

    }
    public String getPropertey(String key) {
    return properties.get(key);
    }
    public Map<String, String> getProperties() {
    return properties;
    }

    public Report generateKeepAlive() {
    return new Report(Type.KEEPALIVE, gameName,  port, passive, serverName, map, gameType, playerCount, maxPlayerCount, playerList,properties);
    }

    public boolean isPassive() {
        return passive;
    }
    public boolean isPoor() {
    return (serverName == null && map == null && gameType == null && playerCount < 1);
    }

    public String getMap() {
        return map;
    }


    public String getGameName() {
        return gameName;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public String getGameType() {
        return gameType;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public int getPort() {
        return port;
    }

    public String getServerName() {
        return serverName;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
    String header = type + " " + gameName + "-Server on port " + port + " (" + denull(serverName) + ",PLAYERS:" + denull(playerCount) + ",MAX:" + denull(maxPlayerCount) + ",MAP:" + denull(map) +",GAMETYPE:" + denull(gameType) + ",PASSIVE?:" + passive + ")";
    StringBuffer players = new StringBuffer();
    for (Player p : playerList) {
    players.append("\n    " + p.getName());
    }
    StringBuffer propertyString = new StringBuffer();
    for (Map.Entry<String, String> e : properties.entrySet()) {
    propertyString.append("\n    " + e);
    }

    return header + players + propertyString;
    }
    private String denull(String s) {
    if (s == null) return "";
    return s;
    }
    private String denull(int i) {
    if (i < 0) return "";
    return String.valueOf(i);
    }



public static enum Type {
STARTUP,
UPDATE,
KEEPALIVE,
EXIT
    }
}
