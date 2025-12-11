package lol.demovio.easyauthlocalsessions;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class EasyAuthLocalSessionsClientNetwork {
    @FunctionalInterface
    public interface GlobalReceiverConsumer {
        void accept(PacketByteBuf buf, GlobalReceiverClientConsumer consumer);
    }

    @FunctionalInterface
    public interface GlobalReceiverClientConsumer {
        void accept(GlobalReceiverClientConsumerCallback callback);
    }

    @FunctionalInterface
    public interface GlobalReceiverClientConsumerCallback {
        void accept(PlayerEntity player, String serverAddress);
    }

    private static void registerGlobalReceiver(
            Identifier packetId,
            GlobalReceiverConsumer globalConsumer
    ) {
        ClientPlayNetworking.registerGlobalReceiver(
                packetId,
                (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
                    UUID receivedUuid = packetByteBuf.readUuid();
                    GlobalReceiverClientConsumer clientConsumer = (callback) -> {
                        minecraftClient.execute(() -> {
                            PlayerEntity player = minecraftClient.player;
                            if (player == null) {
                                EasyAuthLocalSessions.LOGGER.error("Couldn't retrieve player info");
                                return;
                            }
                            ServerInfo serverInfo = clientPlayNetworkHandler.getServerInfo();
                            if (serverInfo == null) {
                                EasyAuthLocalSessions.LOGGER.error("Couldn't retrieve server info for key derivation");
                                return;
                            }
                            UUID localUuid = player.getUuid();
                            if (!localUuid.equals(receivedUuid)) {
                                EasyAuthLocalSessions.LOGGER.error("UUID mismatch: localUuid={}, receivedUuid={}", localUuid, receivedUuid);
                                return;
                            }
                            String address = serverInfo.address;
                            callback.accept(player, address);
                        });
                    };
                    globalConsumer.accept(packetByteBuf, clientConsumer);
                }
        );
    }

    public static void initialize() {
        registerGlobalReceiver(
                EasyAuthLocalSessionsNetwork.REQUEST_AUTH_TOKEN_PACKET_ID,
                (buf, execute) -> execute.accept(EasyAuthLocalSessionsClientManager::handleRequestAuthToken)
        );
        registerGlobalReceiver(
                EasyAuthLocalSessionsNetwork.CACHE_AUTH_TOKEN_PACKET_ID,
                (buf, execute) -> {
                    byte[] authToken = buf.readByteArray();
                    execute.accept((player, address) -> EasyAuthLocalSessionsClientManager.handleCacheAuthToken(authToken, player, address));
                }
        );
        registerGlobalReceiver(
                EasyAuthLocalSessionsNetwork.DELETE_AUTH_TOKEN_PACKET_ID,
                (buf, execute) -> execute.accept(EasyAuthLocalSessionsClientManager::handleDeleteAuthToken)
        );
    }
}
