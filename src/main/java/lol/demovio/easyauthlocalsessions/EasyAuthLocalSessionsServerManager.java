package lol.demovio.easyauthlocalsessions;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.security.SecureRandom;
import java.util.Arrays;

public class EasyAuthLocalSessionsServerManager {
    public static void handleResponseAuthToken(ServerPlayerEntity player, byte[] authToken) {
        try {
            if (authToken.length != 32) {
                return;
            }
            EasyAuthLocalSessions.LOGGER.info("TODO: validate and verify {} {}", player.getUuid(), Arrays.toString(authToken));
        } catch (Exception e) {
            EasyAuthLocalSessions.LOGGER.error("Error while handling authorization token received from client", e);
        }
    }

    public static byte[] generateAuthToken(ServerPlayerEntity player) {
        byte[] token = new byte[32];
        new SecureRandom().nextBytes(token);
        // TODO: write to encrypted db (key = hashed token, value = player since player can have multiple tokens)
        return token;
    }

    public static void sendAuthToken(ServerPlayerEntity player) {
        PacketByteBuf payload = PacketByteBufs.create();
        byte[] token = EasyAuthLocalSessionsServerManager.generateAuthToken(player);
        payload.writeByteArray(token);
        ServerPlayNetworking.send(player, EasyAuthLocalSessionsNetwork.CACHE_AUTH_TOKEN_PACKET_ID, payload);
    }
}
