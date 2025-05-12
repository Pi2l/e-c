package m.lab3;


import m.lab3.server.SimpleServer;

import java.io.IOException;

public class Lab3Application {

	public static void main(String[] args) throws IOException {
		int port = 8080;
		System.out.println("Server listening on port " + port);
		SimpleServer simpleServer = new SimpleServer(port);
		simpleServer.start();
	}

}
