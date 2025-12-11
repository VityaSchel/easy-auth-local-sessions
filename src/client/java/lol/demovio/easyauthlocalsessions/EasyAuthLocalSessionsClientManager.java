package lol.demovio.easyauthlocalsessions;

import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class EasyAuthLocalSessionsClientManager {
    public static void handleRequestAuthToken(PlayerEntity player, UUID remoteUuid, String serverAddress) {
        UUID localUuid = player.getUuid();
        if (localUuid.equals(remoteUuid)) {
            PacketByteBuf payload = PacketByteBufs.create();
            try {
                EasyAuthLocalSessionsClientSecureStorage secureStorage = new EasyAuthLocalSessionsClientSecureStorage(localUuid, serverAddress);
                byte[] token = secureStorage.getAuthToken();
                if (token != null) {
                    payload.writeByteArray(token);
                    ClientPlayNetworking.send(EasyAuthLocalSessionsNetwork.RESPONSE_AUTH_TOKEN_PACKET_ID, payload);
                }
            } catch (Exception e) {
                EasyAuthLocalSessions.LOGGER.error("Failed to read authorization token, fallback to normal behaviour", e);
            }
        } else {
            EasyAuthLocalSessions.LOGGER.error("UUID mismatch: localUuid={}, remoteUuid={}", localUuid, remoteUuid);
        }
    }
}
