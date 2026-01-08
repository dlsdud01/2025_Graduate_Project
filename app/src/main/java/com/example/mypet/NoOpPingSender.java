package com.example.mypet;

import org.eclipse.paho.client.mqttv3.MqttPingSender;
import org.eclipse.paho.client.mqttv3.MqttToken;
import org.eclipse.paho.client.mqttv3.internal.ClientComms;

public class NoOpPingSender implements MqttPingSender {

    @Override
    public void init(ClientComms comms) {
        // 아무 것도 안 함
    }

    @Override
    public void start() {
        // 아무 것도 안 함
    }

    @Override
    public void stop() {
        // 아무 것도 안 함
    }

    @Override
    public void schedule(long delayInMilliseconds) {
        // 아무 것도 안 함
    }

    public void cancel() {
        // 아무 것도 안 함
    }
}
