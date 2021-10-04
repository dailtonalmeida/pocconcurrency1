
package br.nom.dsa.pocconcurrency1;

/**
 *
 * @author dailtonalmeida
 */
public class MyTask {

    public static String doSlowTask() {
        sleepQuietly(1L);
        return "OK";
    }
    
    private static void sleepQuietly(long seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
