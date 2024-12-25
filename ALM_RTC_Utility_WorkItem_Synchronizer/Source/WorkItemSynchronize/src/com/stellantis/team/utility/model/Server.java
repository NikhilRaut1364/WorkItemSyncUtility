package com.stellantis.team.utility.model;

public class Server {
	private String serverName;
    private String serverURL;
    
    public Server(String display, String value) {
    	this.serverName = display;
        this.serverURL = value;
	}
    
    public String getDisplay() {
        return serverName;
    }

    public String getValue() {
        return serverURL;
    }

    @Override
    public String toString() {
        return serverName;
    }
}
