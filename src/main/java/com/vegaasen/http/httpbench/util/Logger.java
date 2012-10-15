package com.vegaasen.http.httpbench.util;

/**
 * @author <a href="vegaasen@gmail.com">Vegard Aasen</a>
 */
public final class Logger {

    public static final String
            INFO = "info",
            DEBUG = "debug";

    private static String log_type;

    private Logger() {}

    public static void init(final String type){
        log_type = type;
    }

    public static void log(final Object element) {
        if(element!=null) {
            System.out.println(String.format("[%s] %s", log_type, element));
        }
    }

}
