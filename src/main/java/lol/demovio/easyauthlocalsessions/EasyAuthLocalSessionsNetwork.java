package lol.demovio.easyauthlocalsessions;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class EasyAuthLocalSessionsNetwork {
    public static final Identifier REQUEST_AUTH_TOKEN_PACKET_ID = new Identifier("easy_auth", "request_auth_token");
    public static final Identifier RESPONSE_AUTH_TOKEN_PACKET_ID = new Identifier("easy_auth", "response_auth_token");

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(RESPONSE_AUTH_TOKEN_PACKET_ID,
            (server, player, handler, buf, responseSender) -> {
                byte[] authToken = buf.readByteArray();
                server.execute(() -> {
                    if (player == null) {
                        return;
                    }
                    EasyAuthLocalSessionsServerManager.handleResponseAuthToken(player, authToken);
                });
            });
    }
}