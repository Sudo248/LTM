package com.sudo248.ltm;

import com.sudo248.ltm.constants.Const;
import com.sudo248.ltm.model.Method;
import com.sudo248.ltm.model.Request;
import com.sudo248.ltm.model.entity.user.Name;
import com.sudo248.ltm.model.entity.user.User;
import com.sudo248.ltm.model.entity.user.UserRequest;
import com.sudo248.ltm.websocket.WebSocketServerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.SerializationUtils;
import org.sudo248.WebSocket;
import org.sudo248.WebSocketImpl;
import org.sudo248.client.WebSocketClient;
import org.sudo248.handshake.server.ServerHandshake;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		WebSocketServerImpl socketServer = new WebSocketServerImpl();
		socketServer.start();
	}

	/*public static void main(String[] args) {
		ArrayList<User> users = new ArrayList<>();
		for (long i =0;i<5;i++){
			users.add(new User(i, new Name("Le", "Duong"), "24/08/2001"));
		}
		UserRequest request = new UserRequest("/user", Method.GET, users);
		System.out.println(request);
		System.out.println(request.toJson());
		UserRequest newRequest = UserRequest.fromJson(request.toJson());
		System.out.println(newRequest.getUser().get(0).getName() instanceof Name);
	}*/

}


/*class DemoClientSocket {
	public static void main(String[] args) throws URISyntaxException, IOException {
//		Socket socket = new Socket(InetAddress.getLocalHost(), Const.WS_PORT);
//		System.out.println(socket.getLocalSocketAddress());
//		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		WebSocketClient client = new WebSocketClient(new URI("ws://localhost:6026")) {
			@Override
			public void onOpen(ServerHandshake handshake) {
				System.out.println("Start send data");
				String s = "Start send data";
				send(s);
				User user = new User(
						123L,
						"Le Hong Duong",
						"24/08/2001"
				);
				System.out.println(user);
				Request<User> request = new Request<>(
						"/users",
						Method.GET,
						new HashMap(
								Map.of(
										"id","123"
								)
						),
						new HashMap(
								Map.of(
										"name", "1"
								)
						),
						user
				);
				send(request);
				System.out.println("Sended data");
			}

			@Override
			public void onMessage(String message) {

			}

			@Override
			public void onClose(int code, String reason, boolean remote) {

			}

			@Override
			public void onError(Exception ex) {

			}

			@Override
			public void onMessage(Object object) {

			}
		};
		client.connect();
	}
}

class DemoServerSocket {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ServerSocket server = new ServerSocket(Const.WS_PORT);
//		while (true) {
			Socket client = server.accept();
			System.out.println("connected: " + client.getInetAddress());
			ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			Object s = ois.readObject();
			System.out.println(s);

			Request<User> request = (Request<User>) ois.readObject();

			System.out.println(request);

//		}
	}
}

class DemoChannel {
	public static void main(String[] args) throws IOException {
		try {
			File file = new File("src/main/java/com/sudo248/ltm/demo.txt");
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			FileChannel channel = raf.getChannel();
			int bufferSize = 1024;
			if (bufferSize > channel.size()) {
				bufferSize = (int) channel.size();
			}
			ByteBuffer buff = ByteBuffer.allocate(bufferSize);
			channel.read(buff);
			System.out.println(new String(buff.array(), StandardCharsets.UTF_8));
			buff = ByteBuffer.wrap("Le Hong Duong".getBytes());
			channel.write(buff);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}*/
