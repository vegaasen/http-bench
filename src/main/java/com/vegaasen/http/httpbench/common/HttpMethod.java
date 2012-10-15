package com.vegaasen.http.httpbench.common;

/**
 * Simple class that just contains "all" of the various http methods out there.
 *
 * @author <a href="vegaasen@gmail.com">Vegard Aasen</a>
 */
public final class HttpMethod {

    public static final String
            GET = "GET",
            POST = "POST",
            OPTIONS = "OPTIONS",
            DELETE = "DELETE",
            HEAD = "HEAD",
            TRACE = "TRACE"; // trace is experimental. Use at own "risk" lol

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
