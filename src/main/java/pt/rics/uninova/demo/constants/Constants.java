/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.rics.uninova.demo.constants;

/**
 *
 * @author joao
 */
public class Constants {
    public static boolean DEBUG = true;
    public static boolean QUIET = false;
    public static String HOST = "127.0.0.1";
    public static int KEEPALIVE = 60;
    public static String USERNAME = "joao";
    public static String PASSWORD = "joaotripa";
    public static String WILL_TOPIC = "will";
    public static String WILL_PAYLOAD = "goodbye";
    public static int WILL_QOS = 0; //0, 1 or 2
    public static boolean WILL_RETAIN = true;
    public static boolean CLEAN_SESSION = true;
    public static int MAX_INFLIGHT_MSG = 1;
    public static boolean AUTOMATIC_RECONNECT = false;
    public static int ACTION_TIMEOUT = 5000;
    public static boolean VERBOSE = false;
}
