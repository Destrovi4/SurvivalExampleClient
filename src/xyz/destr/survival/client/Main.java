package xyz.destr.survival.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

import xyz.destr.survival.io.NetConstants;
import xyz.destr.survival.io.action.ActorActionWriter;
import xyz.destr.survival.io.action.ActorTypeAction;
import xyz.destr.survival.io.action.UserAction;
import xyz.destr.survival.io.action.UserCommand;
import xyz.destr.survival.io.view.ActorTypeView;
import xyz.destr.survival.io.view.ActorView;
import xyz.destr.survival.io.view.GameView;
import xyz.destr.survival.io.view.UserMessage;

public class Main {
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		
		InetAddress inetAddress = null;
		int port = NetConstants.DEFAULT_PORT;
		
		try {
			for(int i = 0, count = args.length; i < count;) {
				switch(args[i++]) {
					case "-a":{
						String[] addressData = args[i++].split(":");
						if(addressData.length > 0) {
							if("localhost".equals(addressData[0])) {
								inetAddress = InetAddress.getLocalHost();
							} else {
								inetAddress = InetAddress.getByName(addressData[0]);
							}
						}
						if(addressData.length > 1) {
							port = Integer.parseInt(addressData[1]);
						}
					}
					break;
					default:
						throw new RuntimeException("Invalid argument " + args[i-1]);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(inetAddress == null) {
			inetAddress = InetAddress.getLocalHost();
		}
		
		Config.load();
		
		System.out.println("Connection to " + inetAddress + " port " + port);
		final Socket socket = new Socket(inetAddress, port);
		socket.setSoTimeout(1000);
		
		final DataInputStream dis = new DataInputStream(socket.getInputStream());
		final DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		final GameView gameView = new GameView();
		final UserAction userAction = new UserAction();
		final ActorLogics actorLogics = new ActorLogics();
		final ActorActionWriter actorActionWriter = new ActorActionWriter();
		
		dos.write(NetConstants.HANDSHAKE_WORD.getBytes());
		if(Config.userUUID == null) {
			dos.writeLong(0);
			dos.writeLong(0);
		} else {
			System.out.println("Try to login as " + Config.userUUID);
			dos.writeLong(Config.userUUID.getMostSignificantBits());
			dos.writeLong(Config.userUUID.getLeastSignificantBits());
		}
		
		socket.setSoTimeout(0);
		UUID userUUID = new UUID(dis.readLong(), dis.readLong());
		if(!userUUID.equals(Config.userUUID)) {
			Config.userUUID = userUUID;
			Config.save();
			System.out.println("Created new user: " + userUUID);
		} else {
			System.out.println("Sucsess login as: " + userUUID);
		}
		
		while(!Thread.interrupted()) {
			gameView.clear();
			gameView.read(dis);
			
			for(UserMessage message: gameView.user.messageList) {
				switch(message.type) {
				case INFO:
					System.out.println("Info: " + message.text);
					break;
				case WARNING:
					System.err.println("Warning: " + message.text);
					break;
				case ERROR:
					System.err.println("Error: " + message.text);
					break;
				default:
					System.err.println("Unknown: " + message.text);
				}
			}
			
			userAction.clear();
			if(gameView.user.actorTypeList.size() == 0) {
				UserCommand command = userAction.commandList.addInstance();
				command.type = UserCommand.Type.CREATE_ACTOR_TYPE;
				actorLogics.create(command.propertys);
			} else {
				ActorTypeView actorTypeView = gameView.user.actorTypeList.get(0);
				ActorTypeAction actorTypeAction = userAction.actorTypeList.addInstance();
				actorTypeAction.typeUUID = actorTypeView.uuid;
				
				for(ActorView actorView: actorTypeView.actorList) {
					actorActionWriter.set(actorView, actorTypeAction);					
					actorLogics.process(actorView, actorActionWriter);
					actorActionWriter.clear();
				}
			}
			userAction.write(dos);
			dos.flush();
		}
		
		socket.close();
	}

}
