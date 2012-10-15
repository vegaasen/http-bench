package com.vegaasen.http.httpbench.common;

/**
 * @author <a href="vegaasen@gmail.com">Vegard Aasen</a>
 */
public enum ErrorMessages {

    E_1001("Wrong input parameter. Verify parameters"),

    E_2001("Please run setUp(), warmUp() and  before continue");

    private String message;

    private ErrorMessages(String message){
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
