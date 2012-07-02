/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
/**
 *
 * @author testi
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    try {
        System.out.println("Master server violetsky.ch:3079");
        ReportListener rl = new MasterReportListener(InetAddress.getByName("violetsky.ch"),3079);
        Environment environment = new Environment(args, new File(System.clearProperty("user.dir")), System.getProperty("os.name"));

        GameReporter r = new GameReporterFactory().init(environment, rl);
        System.out.println("Detected " + r.gameName());
        String[] commandLine;
        if (args.length < 1) {
        commandLine = r.getCommandline();
        }
        else {
        commandLine = args;
        }
        if (!r.isPassive()) {
        Process p = Runtime.getRuntime().exec(commandLine);
        r.parseStream(p.getInputStream());
        System.out.println("Shutdown");
        }
        else {
        System.out.println("The " + r.gameName() + " GameReporter is passive. This means you have to start the server yourself.");
        }
    }
    catch (InitException ex) {
    System.err.println(ex.getMessage());
    } catch (IOException ex) {
    System.err.println(ex.getMessage());
    }
    }

}
