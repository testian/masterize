/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;
import java.io.InputStream;
import java.io.IOException;
/**
 *
 * @author testi
 */
public interface GameReporter {
    /**
     * This method is called if the caller tries to start a game of this type. If this method returns true, the caller will start a gameserver of the gametype represented by this class.
     * @param environment
     * @return if this call was successful. Means that this instance of GameReporter detected the environment of the game represented by this GameReporter
     * @exception InitException if this call (partially) the game environment, but detected that it is configured wrong.
     */
    public boolean init(Environment environment) throws InitException;
    public void parseStream(InputStream stream) throws IOException;
    public String gameName();
    public String[] getCommandline();
    public boolean isPassive();
}
