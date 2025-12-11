package lol.demovio.easyauthlocalsessions;

import java.util.Arrays;
import net.minecraft.server.network.ServerPlayerEntity;

public class EasyAuthLocalSessionsServerManager {
    public static void handleResponseAuthToken(ServerPlayerEntity player, byte[] authToken) {
        try {
            if (authToken.length != 32) {
                return;
            }
            EasyAuthLocalSessions.LOGGER.info(player.getUuid() + " " + Arrays.toString(authToken));
        } catch (Exception e) {
            EasyAuthLocalSessions.LOGGER.error("Error while handling authorization token received from client", e);
        }
    }

    public static byte[] generateAuthToken(ServerPlayerEntity player) {
        byte[] token = new byte[32];
        new java.security.SecureRandom().nextBytes(token);
        // TODO: write
        return token;
    }
}
