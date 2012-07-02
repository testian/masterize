/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;
import java.io.File;
/**
 *
 * @author testi
 */
public class Environment {
private String[] commandLine;
private File workingDirectory;
private OS os;

    public Environment(String[] commandLine, File workingDirectory, String os) {
        this.commandLine = commandLine;
        if (!workingDirectory.isDirectory()) throw new IllegalArgumentException("working directory must be a directory");
        this.workingDirectory = workingDirectory;
        try {
        this.os = OS.valueOf(os.toUpperCase()); } catch (IllegalArgumentException ex) {
        this.os = OS.OTHER;
        }
    }

    public String[] getCommandLine() {
        return commandLine;
    }

    public OS getOs() {
        return os;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }


public static enum OS {
LINUX,
WINDOWS,
OTHER
}
}
