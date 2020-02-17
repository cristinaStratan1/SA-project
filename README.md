# SA-project

MQTT is very simple and lightweight compared to any other IOT Messaging protocols. That's why in this code we are using MQTT protocol to simulate getting data from the edge devices.

First of all, the data flow is simulated with the help of data.json file. The reader is extracting the data and inputting it in a json array.


			// json reader
			JSONArray jsonarray;
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream("resources/data.json"), "UTF-8"))) {
				JSONTokener tokener = new JSONTokener(reader);
				 jsonarray = new JSONArray(tokener);
			}

Secondly, simulating multiple publishers by setting a message payload for each MqttClient. The MqqtClient is connected to the tcp://localhost:1883 port and the message is published on "hello-mqtt" topic.

	for (int j = 0; j < jsonarray.length() - 1; j++) {
				MqttClient  client = clients.get(j);
				JSONObject data = jsonarray.getJSONObject(j);

				MqttConnectOptions options = new MqttConnectOptions();
				options.setKeepAliveInterval(480);
				options.setWill(client.getTopic("WillTopic"), "Something bad happend".getBytes(), 1, true);
				client.connect(options);

				MqttMessage message = new MqttMessage();
				message.setPayload(data.toString().getBytes());

				message.setRetained(true);
				message.setQos(1);

				client.publish("hello-mqtt", message);

				client.disconnect();
			}
