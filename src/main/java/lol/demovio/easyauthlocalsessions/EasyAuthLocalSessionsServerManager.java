package lol.demovio.easyauthlocalsessions;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Arrays;

public class EasyAuthLocalSessionsServerManager {
    public static void handleResponseAuthToken(ServerPlayerEntity player, byte[] authToken) {
        EasyAuthLocalSessions.LOGGER.info("Got RESPONSE_AUTH_TOKEN_PACKET_ID" + " " + Arrays.toString(authToken));
    }
}
