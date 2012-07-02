/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;

import masterize.modules.*;
/**
 *
 * @author testi
 */
public class GameReporterFactory {

    public GameReporter init(Environment environment, ReportListener rl) throws InitException {
    
        GameReporter r;
        r = new SturmovikReporter(rl);
        if (r.init(environment))
            return r;
        throw new InitException("No game detected");
    }

    public Game getGame(String name) {

        Game g;
        g = new SturmovikGame();
        if (g.name().equals(name))
            return g;

        return null;

    }

}
