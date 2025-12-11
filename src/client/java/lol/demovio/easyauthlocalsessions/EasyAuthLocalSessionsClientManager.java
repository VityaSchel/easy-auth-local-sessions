package lol.demovio.easyauthlocalsessions;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import java.util.Arrays;

public class EasyAuthLocalSessionsClientManager {
    public static void handleRequestAuthToken() {
        PacketByteBuf payload = PacketByteBufs.create();
        byte[] token = new byte[32];
        // TODO: retrieve from secure storage
        new java.security.SecureRandom().nextBytes(token);
        payload.writeByteArray(token);
        ClientPlayNetworking.send(EasyAuthLocalSessionsNetwork.RESPONSE_AUTH_TOKEN_PACKET_ID, payload);
    }
}
