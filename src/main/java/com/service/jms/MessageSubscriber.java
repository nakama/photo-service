package com.service.jms;


public interface MessageSubscriber {

    void handleMessage(String message);
    
}
