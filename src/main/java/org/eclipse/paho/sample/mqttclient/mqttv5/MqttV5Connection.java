package org.eclipse.paho.sample.mqttclient.mqttv5;

import java.util.UUID;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import pt.rics.uninova.demo.constants.Constants;

public class MqttV5Connection {

	private String hostURI;
	private String clientID;
	private MqttConnectionOptions conOpts = new MqttConnectionOptions();
	private boolean automaticReconnect = false;

	/**
	 * Initialises an MQTTv5 Connection Object which holds the configuration
	 * required to open a connection to an MQTTv5 server
	 * 
	 */
	public MqttV5Connection(){	
            this.hostURI = "tcp://" + Constants.HOST + ":1883"; // Get the Host URI
//            this.conOpts.setKeepAliveInterval(Constants.KEEPALIVE); //Defaults to 60
            this.conOpts.setPassword(Constants.PASSWORD.getBytes()); //Uncomment for user auth
            this.conOpts.setUserName(Constants.USERNAME);  //Uncomment for user auth
//            this.conOpts.setWill(Constants.WILL_TOPIC, Constants.WILL_PAYLOAD.getBytes(),
//                    Constants.WILL_QOS, Constants.WILL_RETAIN);       //Uncomment to defining last will message 
//            this.conOpts.setCleanSession(Constants.CLEAN_SESSION);    //Defaults to false
//            this.conOpts.setMaxInflight(Constants.MAX_INFLIGHT_MSG);  
//            this.conOpts.setAutomaticReconnect(Constants.AUTOMATIC_RECONNECT); //Defaults to false
//            this.automaticReconnect = Constants.AUTOMATIC_RECONNECT;

            // If the client ID was not set, generate one ourselves
            if (this.clientID == null || this.clientID.isEmpty()) {
                    // No client ID provided, generate one from the process ID
                    UUID uuid = UUID.randomUUID();
                    clientID = "mqtt-client-" + uuid;
            }
	}
        
        public MqttV5Connection(String clientID){	
            this.hostURI = "tcp://" + Constants.HOST + ":1883"; // Get the Host URI
            this.clientID = clientID;
//            this.conOpts.setKeepAliveInterval(Constants.KEEPALIVE); //Defaults to 60
            this.conOpts.setPassword(Constants.PASSWORD.getBytes()); //Uncomment for user auth
            this.conOpts.setUserName(Constants.USERNAME);  //Uncomment for user auth
//            this.conOpts.setWill(Constants.WILL_TOPIC, Constants.WILL_PAYLOAD.getBytes(),
//                    Constants.WILL_QOS, Constants.WILL_RETAIN);       //Uncomment to defining last will message 
//            this.conOpts.setCleanSession(Constants.CLEAN_SESSION);    //Defaults to false
//            this.conOpts.setMaxInflight(Constants.MAX_INFLIGHT_MSG);  
//            this.conOpts.setAutomaticReconnect(Constants.AUTOMATIC_RECONNECT); //Defaults to false
//            this.automaticReconnect = Constants.AUTOMATIC_RECONNECT;
	}

	public String getHostURI() {
		return hostURI;
	}

	public String getClientID() {
		return clientID;
	}

	public MqttConnectionOptions getConOpts() {
		return conOpts;
	}

	public boolean isAutomaticReconnectEnabled() {
		return this.automaticReconnect;
	}

}
