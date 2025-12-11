package lol.demovio.easyauthlocalsessions;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nikitacartes.easyauth.EasyAuth;
import xyz.nikitacartes.easyauth.storage.PlayerEntryV1;
import xyz.nikitacartes.easyauth.utils.PlayerAuth;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.UUID;

public class EasyAuthLocalSessionsServerManager {
    public static void handleResponseAuthToken(ServerPlayerEntity player, byte[] authToken) {
        try {
            if (authToken.length != 32) {
                return;
            }
            PlayerAuth playerAuth = (PlayerAuth) player;
            if (!playerAuth.easyAuth$isAuthenticated()) {
                PlayerEntryV1 playerData = playerAuth.easyAuth$getPlayerEntryV1();
                boolean isRegistered = playerData != null;
                if (isRegistered) {
                    UUID authorizingPlayerUuid = player.getUuid();
                    UUID retrievedPlayerUuid = EasyAuthLocalSessionsServerStorage.verifyAuthorizationToken(authToken);
                    if (authorizingPlayerUuid.equals(retrievedPlayerUuid)) {
                        saveAuthWith(player, authToken);
                        EasyAuth.langConfig.successfullyAuthenticated.send(player);
                        playerAuth.easyAuth$setAuthenticated(true);
                        playerAuth.easyAuth$restoreTrueLocation();
                        playerData.lastAuthenticatedDate = ZonedDateTime.now();
                        playerData.loginTries = 0L;
                        playerData.lastIp = playerAuth.easyAuth$getIpAddress();
                        playerData.update();
                    }
                }
            }
        } catch (Exception e) {
            EasyAuthLocalSessions.LOGGER.error("Error while handling authorization token received from client", e);
        }
    }

    public static byte[] generateAuthToken(ServerPlayerEntity player) throws NoSuchAlgorithmException, IOException {
        byte[] token = new byte[32];
        new SecureRandom().nextBytes(token);
        EasyAuthLocalSessionsServerStorage.saveAuthorizationToken(token, player.getUuid());
        return token;
    }

    public static void sendAuthToken(ServerPlayerEntity player) {
        PacketByteBuf payload = PacketByteBufs.create();
        try {
            byte[] token = EasyAuthLocalSessionsServerManager.generateAuthToken(player);
            payload.writeUuid(player.getUuid());
            payload.writeByteArray(token);
            ServerPlayNetworking.send(player, EasyAuthLocalSessionsNetwork.CACHE_AUTH_TOKEN_PACKET_ID, payload);
            saveAuthWith(player, token);
        } catch (Exception e) {
            EasyAuthLocalSessions.LOGGER.error("Failed to send generate token", e);
        }
    }

    public static void revokeAuthToken(ServerPlayerEntity player) {
        EasyAuthLocalSessionsPlayerAuth playerAuth = (EasyAuthLocalSessionsPlayerAuth) player;
        EasyAuthLocalSessionsPlayerAuthData authData = playerAuth.easyAuthLocalSessions$getAuthData();
        if (authData != null && authData.authorizationTokenHash != null) {
            try {
                EasyAuthLocalSessionsServerStorage.deleteAuthorizationToken(authData.authorizationTokenHash);
            } catch (IOException e) {
                EasyAuthLocalSessions.LOGGER.error("Couldn't revoke authorization token", e);
            }
            PacketByteBuf payload = PacketByteBufs.create();
            payload.writeUuid(player.getUuid());
            ServerPlayNetworking.send(player, EasyAuthLocalSessionsNetwork.DELETE_AUTH_TOKEN_PACKET_ID, payload);
        }
    }

    public static void saveAuthWith(ServerPlayerEntity player, byte[] token) throws NoSuchAlgorithmException {
        EasyAuthLocalSessionsPlayerAuth playerAuth = (EasyAuthLocalSessionsPlayerAuth) player;
        String tokenHash = EasyAuthLocalSessionsServerStorage.hashToken(token);
        playerAuth.easyAuthLocalSessions$setAuthData(new EasyAuthLocalSessionsPlayerAuthData(tokenHash));
    }
}
