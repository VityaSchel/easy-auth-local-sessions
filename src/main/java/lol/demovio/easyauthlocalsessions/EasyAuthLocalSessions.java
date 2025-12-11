package lol.demovio.easyauthlocalsessions;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyAuthLocalSessions implements ModInitializer {
	public static final String MOD_ID = "easyauth-local-sessions";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		EasyAuthLocalSessionsNetwork.initialize();
	}
}