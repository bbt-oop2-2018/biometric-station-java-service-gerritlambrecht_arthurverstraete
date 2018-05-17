package javaserialport;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttDataService implements MqttCallback {
    private MqttClient client;
    //private String broker = "tcp://m23.cloudmqtt.com:1883";
    private String broker = "tcp://labict.be:1883";
    private String clientId;
    private int qos = 2;            // Exactly once
    private MemoryPersistence persistence;
    private MqttConnectOptions connectionOptions;
    private IMqttDataHandler messageHandler = null;
    
    private final String BASE_TOPIC = "java/data";
    private String channelName;
    private String channelTopic;
    
    public MqttDataService(String clientId, String channelName) {
        Random random = new Random();
        this.clientId = clientId + random.nextInt();
        this.channelName = channelName;
        this.channelTopic = BASE_TOPIC + "/" + this.channelName;
        setupMqtt();
    }
    
    public MqttDataService() {
        this("guest", "general");
    }
    
    private void setupMqtt() {
        try {
            persistence = new MemoryPersistence();
            client = new MqttClient(broker, clientId, persistence);
            connectionOptions = new MqttConnectOptions();
            connectionOptions.setCleanSession(true);
            client.connect(connectionOptions);
            client.setCallback(this);
            client.subscribe(channelTopic);
        } catch(MqttException me) {
            System.out.println("Failed to connect to broker");
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    
    public void switchChannel (String channel) {
        try {
            client.unsubscribe(channelTopic);
            this.channelName = channel;
            this.channelTopic = BASE_TOPIC + "/" + this.channelName;
            client.subscribe(channelTopic);
        } catch (MqttException ex) {
            Logger.getLogger(MqttDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void sendData(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(qos);
            client.publish(channelTopic, mqttMessage);
        } catch (MqttException ex) {
            Logger.getLogger(MqttDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setMessageHandler(IMqttDataHandler handler) {
        this.messageHandler = handler;
    }
    
    public void disconnect() {
        try {
            client.disconnect();
            client.close();
        } catch (MqttException ex) {
            Logger.getLogger(MqttDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        System.out.println("Lost connection with broker");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        if (messageHandler != null) {
            messageHandler.messageArrived(channelName, mm.toString());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
    }
}
