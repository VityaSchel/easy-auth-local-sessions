package lol.demovio.easyauthlocalsessions;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class EasyAuthLocalSessionsClientNetwork {
    @FunctionalInterface
    public interface PlayerAndServerInfoConsumer {
        void accept(PlayerEntity player, String serverAddress);
    }

    private static void executeWithPlayerAndServerInfo(
            MinecraftClient client,
            ClientPlayNetworkHandler handler,
            PlayerAndServerInfoConsumer consumer
    ) {
        client.execute(() -> {
            PlayerEntity player = client.player;
            if (player == null) {
                EasyAuthLocalSessions.LOGGER.error("Couldn't retrieve player info");
                return;
            }
            ServerInfo serverInfo = handler.getServerInfo();
            if (serverInfo == null) {
                EasyAuthLocalSessions.LOGGER.error("Couldn't retrieve server info for key derivation");
                return;
            }
            String address = serverInfo.address;
            consumer.accept(player, address);
        });
    }

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(
                EasyAuthLocalSessionsNetwork.REQUEST_AUTH_TOKEN_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    UUID remoteUuid = buf.readUuid();
                    executeWithPlayerAndServerInfo(client, handler, (player, address) -> {
                        EasyAuthLocalSessionsClientManager.handleRequestAuthToken(remoteUuid, player, address);
                    });
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                EasyAuthLocalSessionsNetwork.CACHE_AUTH_TOKEN_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    byte[] authToken = buf.readByteArray();
                    executeWithPlayerAndServerInfo(client, handler, (player, address) -> {
                        EasyAuthLocalSessionsClientManager.handleCacheAuthToken(authToken, player, address);
                    });
                }
        );
    }
}
