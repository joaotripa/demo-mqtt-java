package org.eclipse.paho.sample.mqttclient.mqttv3;

import java.util.UUID;
import pt.rics.uninova.demo.constants.Constants;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class MqttV3Connection {

	private String hostURI;
	private String clientID;
	private MqttConnectOptions conOpts = new MqttConnectOptions();
	private boolean automaticReconnect = false;

	/**
	 * Initialises an MQTTv3 Connection Object which holds the configuration
	 * required to open a connection to an MQTTv3.1.1 server
	 *  
	 */
	public MqttV3Connection(){	
            this.hostURI = "tcp://" + Constants.HOST + ":1883"; // Get the Host URI
//            this.conOpts.setKeepAliveInterval(Constants.KEEPALIVE); //Defaults to 60
            this.conOpts.setPassword(Constants.PASSWORD.toCharArray()); //Uncomment for user auth
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
        
        public MqttV3Connection(String clientID){	
            this.hostURI = "tcp://" + Constants.HOST + ":1883"; // Get the Host URI
            this.clientID = clientID;
//            this.conOpts.setKeepAliveInterval(Constants.KEEPALIVE); //Defaults to 60
//            this.conOpts.setPassword(Constants.PASSWORD.toCharArray()); //Uncomment for user auth
//            this.conOpts.setUserName(Constants.USERNAME);  //Uncomment for user auth
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

	public MqttConnectOptions getConOpts() {
		return conOpts;
	}
	
	public boolean isAutomaticReconnectEnabled() {
		return this.automaticReconnect;
	}

}
