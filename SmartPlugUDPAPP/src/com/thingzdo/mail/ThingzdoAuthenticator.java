package com.thingzdo.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class ThingzdoAuthenticator extends Authenticator {
	String userName=null;  
    String password=null;  
       
    public ThingzdoAuthenticator(){  
    }  
    public ThingzdoAuthenticator(String username, String password) {   
        this.userName = username;   
        this.password = password;   
    }   
    protected PasswordAuthentication getPasswordAuthentication(){  
        return new PasswordAuthentication(userName, password);  
    } 
}
