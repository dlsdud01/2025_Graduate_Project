package com.example.mypet;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class MqttHandler {

    private final Context context;
    private final MqttAndroidClient mqttClient;

    // ✅ HiveMQ Cloud 서버 주소
    private final String SERVER_URI = "ssl://2df4949d872142379108942bedb86c9f.s1.eu.hivemq.cloud:8883";
    private final String CLIENT_ID = MqttClient.generateClientId();

    // ✅ MQTT 토픽 설정
    private final String SENSOR_TOPIC = "iot/raspberrypi/temperature";
    private final String MOTION_TOPIC = "mypet/motion";

    // ✅ 인증 정보
    private final String MQTT_USERNAME = "seungyongJee";
    private final String MQTT_PASSWORD = "Marvel@7220";

    private final OnSensorDataListener sensorListener;
    private final OnMotionReceivedListener motionListener;

    public interface OnSensorDataListener {
        void onSensorDataReceived(double temperature, double humidity);
    }

    public interface OnMotionReceivedListener {
        void onMotionReceived(String zone);
    }

    public MqttHandler(Context context,
                       OnSensorDataListener sensorListener,
                       OnMotionReceivedListener motionListener) {

        this.context = context;
        this.sensorListener = sensorListener;
        this.motionListener = motionListener;

        this.mqttClient = new MqttAndroidClient(context, SERVER_URI, CLIENT_ID);


        init();
    }

    private void init() {
        mqttClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("MQTT", "브로커 연결 완료: " + serverURI);
                subscribe(SENSOR_TOPIC);
                subscribe(MOTION_TOPIC);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e("MQTT", "MQTT 연결 끊김", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d("MQTT", "메시지 수신됨: [" + topic + "] " + message.toString());

                if (topic.equals(SENSOR_TOPIC)) {
                    handleSensorData(message.toString());
                } else if (topic.equals(MOTION_TOPIC)) {
                    if (motionListener != null) {
                        motionListener.onMotionReceived(message.toString());
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // 메시지 전송 완료
            }
        });

        connect();
    }

    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        // ✅ PingSender 비활성화 시 keepAliveInterval은 0으로 설정
        options.setKeepAliveInterval(0);

        options.setUserName(MQTT_USERNAME);
        options.setPassword(MQTT_PASSWORD.toCharArray());

        try {
            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "브로커 연결 성공");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "브로커 연결 실패", exception);
                }
            });
        } catch (MqttException e) {
            Log.e("MQTT", "연결 예외 발생", e);
        }
    }

    private void subscribe(String topic) {
        try {
            mqttClient.subscribe(topic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "구독 성공: " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "구독 실패: " + topic, exception);
                }
            });
        } catch (MqttException e) {
            Log.e("MQTT", "구독 예외 발생: " + topic, e);
        }
    }

    private void handleSensorData(String payload) {
        try {
            JSONObject json = new JSONObject(payload);
            double temp = json.getDouble("temperature");
            double humid = json.getDouble("humidity");

            if (sensorListener != null) {
                sensorListener.onSensorDataReceived(temp, humid);
            }
        } catch (Exception e) {
            Log.e("MQTT", "센서 JSON 파싱 오류", e);
        }
    }
}
