/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;

/**
 *
 * @author testi
 */
public class Player {
private String name;
private int score;
private int ping;

    public Player(String name, int score, int ping) {
        this.name = name;
        this.score = score;
        this.ping = ping;
    }

    public String getName() {
        return name;
    }

    public int getPing() {
        return ping;
    }

    public int getScore() {
        return score;
    }

    public int hashCode() {
    return name.hashCode();
    }

    public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof Player)) return false;
    Player  p = (Player)o;
    return name.equals(p.getName());
    }


}
