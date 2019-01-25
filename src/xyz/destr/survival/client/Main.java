package xyz.destr.survival.client;

public class Main {
	
	public static void main(String[] args) throws Exception {
		final ClientConnection clientConnection = new ClientConnection();
		clientConnection.applyArguments(args);
		clientConnection.addActorListener(new ActorLogics());
		clientConnection.run();
	}

}
