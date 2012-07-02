/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;

/**
 *
 * @author testi
 */
public class InitException extends Exception {

    public InitException(Throwable cause) {
        super(cause);
    }

    public InitException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitException(String message) {
        super(message);
    }

    public InitException() {
    }

}
