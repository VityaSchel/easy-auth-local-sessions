package lol.demovio.easyauthlocalsessions;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

public class EasyAuthLocalSessionsServerStorage {
    private static String hashToken(byte[] token) throws NoSuchAlgorithmException {
        byte[] tokenHashed = MessageDigest.getInstance("SHA-256").digest(token);
        return HexFormat.of().formatHex(tokenHashed);
    }

    private static Path getSessionFilePath(String tokenHashedHex) throws IOException {
        Path dirPath = FabricLoader.getInstance().getConfigDir().resolve("EasyAuthLocalSessions-server");
        try {
            Files.createDirectory(dirPath);
        } catch (java.nio.file.FileAlreadyExistsException e) {
            // skip
        }
        return dirPath.resolve(tokenHashedHex);
    }

    public static void saveAuthorizationToken(byte[] token, UUID playerUuid) throws NoSuchAlgorithmException, IOException {
        Path sessionFilePath = getSessionFilePath(hashToken(token));
        Files.write(sessionFilePath, UuidUtils.asBytes(playerUuid));
    }

    public static @Nullable UUID verifyAuthorizationToken(byte[] token) throws NoSuchAlgorithmException {
        try {
            Path sessionFilePath = getSessionFilePath(hashToken(token));
            byte[] sessionFileValue = Files.readAllBytes(sessionFilePath);
            return UuidUtils.asUuid(sessionFileValue);
        } catch (IOException e) {
            return null;
        }
    }

    public static void deleteAuthorizationToken(String tokenHash) throws IOException {
        Path sessionFilePath = getSessionFilePath(tokenHash);
        Files.deleteIfExists(sessionFilePath);
    }

    private static class UuidUtils {
        public static UUID asUuid(byte[] bytes) {
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            long firstLong = bb.getLong();
            long secondLong = bb.getLong();
            return new UUID(firstLong, secondLong);
        }

        public static byte[] asBytes(UUID uuid) {
            ByteBuffer bb = ByteBuffer.allocate(16);
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());
            return bb.array();
        }
    }
}
