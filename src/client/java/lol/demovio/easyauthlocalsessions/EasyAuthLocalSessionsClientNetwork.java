package lol.demovio.easyauthlocalsessions;

import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;

public class EasyAuthLocalSessionsClientNetwork {
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(
            EasyAuthLocalSessionsNetwork.REQUEST_AUTH_TOKEN_PACKET_ID,
            (client, handler, buf, responseSender) -> {
                UUID remoteUuid = buf.readUuid();
                client.execute(() -> {
                    PlayerEntity player = client.player;
                    if (player == null) {
                        return;
                    }
                    EasyAuthLocalSessionsClientManager.handleRequestAuthToken(player, remoteUuid);
                });
            }
        );
    }
}
