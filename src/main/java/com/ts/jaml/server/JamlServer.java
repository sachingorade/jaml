package com.ts.jaml.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ts.jaml.jmx.JamlRegistry;
import com.ts.jaml.pojo.ExecutionTimeMonitorInfo;
import com.ts.jaml.pojo.JamlCommand;
import com.ts.jaml.pojo.MethodMonitorInfo;

/**
 * @author saching
 *
 */
public class JamlServer {

	private int port;
	private ServerSocket serverSocket;
	private boolean stopServer;
	private Thread serverThread;

	public JamlServer(int port) {
		this.port = port;
	}
	
	public synchronized void startServer() throws IOException {
		if (serverSocket != null) {
			return;
		}
		serverSocket = new ServerSocket(port);
		serverThread = new Thread(new ConnectionListener());
		serverThread.start();
	}
	
	public void stopServer() {
		stopServer = true;
		if (serverThread != null) {
			serverThread.interrupt();
		}
	}
	
	class ConnectionListener implements Runnable {
		@Override
		public void run() {
			Socket socket = null;
			BufferedReader reader = null;
			while (!stopServer) {
				try {
					if (socket == null) {
						socket = serverSocket.accept();
						reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					}
					if (reader.ready()) {
						String line = reader.readLine();
						try {
							handleCommand(line);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					socket = null;
					reader = null;
					e.printStackTrace();
					// TODO add log handling
				}
			}
		}
	}

	/**
	 * @param line
	 * @throws IOException 
	 */
	public void handleCommand(String line) throws Exception {
		List<String> args = Arrays.asList(line.split(" "));
		JamlCommand command = JamlCommand.valueOf(args.get(0));
		switch (command) {
		case ADDCLASS:
			{
				String[] options = args.get(1).split(",");
				Map<String, Map<String, MethodMonitorInfo>> map = new HashMap<>();
				Map<String, MethodMonitorInfo> methods = null;
				if(options.length > 1) {
					methods = new HashMap<>();
					for (int i=1;i<options.length;i++) {
						methods.put(options[i], new ExecutionTimeMonitorInfo(options[i]));
					}
				}
				map.put(options[0], methods);
				JamlRegistry.getInstance().addClassesToMonitor(map);
			}
			break;
		case REMOVECLASS:
			{
				JamlRegistry.getInstance().removeClassToMonitor(args.get(1));
			}
		default:
			break;
		}
	}

}
