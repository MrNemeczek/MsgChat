package org.mj.Functions;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.mj.Models.User;

public class MQTTRegistryClientThread extends Thread{
    private User _newClient;
    public MQTTRegistryClientThread(User newClient){
        _newClient = newClient;
    }

    public void run(){
        String broker = "tcp://40.115.61.160:1883";
        String topic = "$CONTROL/dynamic-security/v1";
        String username = "admin";
        String password = "admin";
        String clientid = "newUser";

        int qos = 0;

        try {
            MqttClient client = new MqttClient(broker ,clientid, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);

            client.connect(options);

            MqttMessage message = new MqttMessage(("{\"commands\": [" +
                    "{\"command\": \"createClient\"," +
                    " \"username\": \"" + _newClient.Login + "\"," +
                    "\"password\": \"" + _newClient.Password + "\"," +
                    "\"groups\": [" +
                    "{\"groupname\": \"user\", " +
                    "\"priority\": 1}]" +
                    "}]" +
                    "}").getBytes());
            message.setQos(qos);

            client.publish(topic, message);

            client.disconnect();
            client.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }
}
