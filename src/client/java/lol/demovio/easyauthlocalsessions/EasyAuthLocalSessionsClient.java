package lol.demovio.easyauthlocalsessions;

import net.fabricmc.api.ClientModInitializer;

public class EasyAuthLocalSessionsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EasyAuthLocalSessionsClientNetwork.initialize();
	}
}