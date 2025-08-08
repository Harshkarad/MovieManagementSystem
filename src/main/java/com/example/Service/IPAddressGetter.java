package com.example.Service;

import java.net.InetAddress;

import org.springframework.stereotype.Service;

@Service
public class IPAddressGetter {
    
    public String getLocalHostName(){
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostName();
        } catch (Exception e) {
           return "Unable to find Host";
        }
    }

    public String getLocalHostAddress(){
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (Exception e) {
            return "Unable to find Host";
        }
    }
    
}
