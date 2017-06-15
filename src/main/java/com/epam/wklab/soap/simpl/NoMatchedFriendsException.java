package com.epam.wklab.soap.simpl;

/**
 * Created by sten on 11.04.17.
 */
public class NoMatchedFriendsException extends Exception {
    public NoMatchedFriendsException(String msg) { super(msg); }
    public NoMatchedFriendsException(Throwable cause) { super(cause); }
}
