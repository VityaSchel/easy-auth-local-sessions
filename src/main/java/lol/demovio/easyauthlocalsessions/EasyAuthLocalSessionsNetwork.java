package lol.demovio.easyauthlocalsessions;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class EasyAuthLocalSessionsNetwork {
    public static final Identifier REQUEST_AUTH_TOKEN_PACKET_ID = new Identifier("easy_auth", "request_auth_token");
    public static final Identifier RESPONSE_AUTH_TOKEN_PACKET_ID = new Identifier("easy_auth", "response_auth_token");
    public static final Identifier CACHE_AUTH_TOKEN_PACKET_ID = new Identifier("easy_auth", "cache_auth_token");
    public static final Identifier DELETE_AUTH_TOKEN_PACKET_ID = new Identifier("easy_auth", "delete_auth_token");

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(
                RESPONSE_AUTH_TOKEN_PACKET_ID,
                (server, player, handler, buf, responseSender) -> {
                    UUID receivedPlayerUuid = buf.readUuid();
                    byte[] authToken = buf.readByteArray();
                    server.execute(() -> {
                        if (player != null && player.getUuid().equals(receivedPlayerUuid)) {
                            EasyAuthLocalSessionsServerManager.handleResponseAuthToken(player, authToken);
                        }
                    });
                }
        );
    }
}