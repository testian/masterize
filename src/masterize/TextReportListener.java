/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package masterize;

/**
 *
 * @author testi
 */
public class TextReportListener implements ReportListener {

    public void onReport(Report report) {
        System.out.println(report);
    }

}
