
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Paho {

	public Paho() {
	}

	public static void main(String[] args) {
		try {
			new Paho().publish3();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void publish3() throws IOException {
		try {
			
			// json reader
			JSONArray jsonarray;
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream("resources/data.json"), "UTF-8"))) {
				JSONTokener tokener = new JSONTokener(reader);
				 jsonarray = new JSONArray(tokener);
			}
			
			// client setting
						int i;
						int nclients = jsonarray.length();
						List<MqttClient> clients = new ArrayList<MqttClient>();
						for (i = 1; i < nclients; i++) {
							MqttClient mqttClient = new MqttClient("tcp://localhost:1883", "pahomqttpublish" + i);
							clients.add(mqttClient);
						}

			for (int j = 0; j < jsonarray.length() - 1; j++) {
				MqttClient  client = clients.get(j);
				JSONObject data = jsonarray.getJSONObject(j);

				MqttConnectOptions options = new MqttConnectOptions();
				options.setKeepAliveInterval(480);
				options.setWill(client.getTopic("WillTopic"), "Something bad happend".getBytes(), 1, true);
				client.connect(options);

				MqttMessage message = new MqttMessage();
				message.setPayload(data.toString().getBytes());

				message.setRetained(true); // try with true
				message.setQos(1);

				client.publish("hello-mqtt", message);

				client.disconnect();
			}
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
