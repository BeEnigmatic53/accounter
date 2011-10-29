/**
 * 
 */
package com.vimukti.accounter.mobile;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.vimukti.accounter.mobile.MobileAdaptor.AdaptorType;
import com.vimukti.accounter.mobile.store.CommandsFactory;
import com.vimukti.accounter.mobile.store.PatternStore;

/**
 * @author Prasanna Kumar G
 * 
 */
public class ConsoleChatServer extends Thread {

	private MobileMessageHandler messageHandler;

	/**
	 * Creates new Instance
	 */
	public ConsoleChatServer() {
		this.messageHandler = new MobileMessageHandler();
	}

	public void startChat() {
		try {
			ServerSocket server = new ServerSocket(8080);
			loadCommandsAndPatterns();
			Socket socket = null;
			while ((socket = server.accept()) != null) {
				new ConsoleSocketHandler(socket, messageHandler).start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws AccounterMobileException
	 * 
	 */
	private void loadCommandsAndPatterns() throws AccounterMobileException {
		CommandsFactory.INSTANCE.reload();
		PatternStore.INSTANCE.reload();
	}

	@Override
	public void run() {
		startChat();
	}

	private static class ConsoleSocketHandler extends Thread {

		private MobileMessageHandler handler;
		private Socket socket;

		public ConsoleSocketHandler(Socket socket, MobileMessageHandler handler) {
			this.handler = handler;
			this.socket = socket;
		}

		public void run() {
			try {
				final ObjectOutputStream out = new ObjectOutputStream(
						socket.getOutputStream());
				InputStream inputStream = socket.getInputStream();
				ObjectInputStream in = new ObjectInputStream(inputStream);
				out.writeObject("Connection Successfull");
				System.out.println("Console Chat Server Started.");
				while (true) {
					String user = (String) in.readObject();
					Object readObject = in.readObject();
					String msg = (String) readObject;
					System.out.println(msg);
					try {
						handler.messageReceived(user, msg, AdaptorType.CHAT,
								AccounterChatServer.NETWORK_TYPE_CONSOLE);
					} catch (AccounterMobileException e) {
						e.printStackTrace();
						out.writeObject(e);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
