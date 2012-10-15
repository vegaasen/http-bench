package com.vegaasen.http.httpbench.common;

/**
 * @author <a href="vegaasen@gmail.com">Vegard Aasen</a>
 */
public enum RunProperties {

    URL("-u"),
    HEADER("-h"),
    THREADS("-t"),
    REQUESTS_PR_THREAD("-n"),
    INFO("-i"),
    METHOD("-m"),
    KEEPALIVE("-k"),
    CONTENT_TO_PASS("-f"),
    UNSPESIFIED("?");

    private String identifier;

    private RunProperties(final String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public static RunProperties typeOf(final String identifier) {
        for(RunProperties r : RunProperties.values()) {
            if(r.getIdentifier().equals(identifier)) {
                return r;
            }
        }
        return UNSPESIFIED;
    }

}
