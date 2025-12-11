package lol.demovio.easyauthlocalsessions;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class EasyAuthLocalSessionsClientManager {
    public static void handleRequestAuthToken(PlayerEntity player, String serverAddress) {
        try {
            UUID localUuid = player.getUuid();
            EasyAuthLocalSessionsClientSecureStorage secureStorage = new EasyAuthLocalSessionsClientSecureStorage(localUuid, serverAddress);
            byte[] token = secureStorage.getAuthToken();
            if (token != null) {
                PacketByteBuf payload = PacketByteBufs.create();
                payload.writeUuid(localUuid);
                payload.writeByteArray(token);
                ClientPlayNetworking.send(EasyAuthLocalSessionsNetwork.RESPONSE_AUTH_TOKEN_PACKET_ID, payload);
            }
        } catch (Exception e) {
            EasyAuthLocalSessions.LOGGER.error("Failed to read authorization token, fallback to normal behaviour", e);
        }
    }

    public static void handleCacheAuthToken(byte[] authToken, PlayerEntity player, String serverAddress) {
        try {
            UUID localUuid = player.getUuid();
            EasyAuthLocalSessionsClientSecureStorage secureStorage = new EasyAuthLocalSessionsClientSecureStorage(localUuid, serverAddress);
            secureStorage.saveAuthToken(authToken);
        } catch (Exception e) {
            EasyAuthLocalSessions.LOGGER.error("Failed to write authorization token", e);
        }
    }

    public static void handleDeleteAuthToken(PlayerEntity player, String serverAddress) {
        try {
            UUID localUuid = player.getUuid();
            EasyAuthLocalSessionsClientSecureStorage secureStorage = new EasyAuthLocalSessionsClientSecureStorage(localUuid, serverAddress);
            secureStorage.deleteAuthToken();
        } catch (Exception e) {
            EasyAuthLocalSessions.LOGGER.error("Failed to delete authorization token", e);
        }
    }
}
