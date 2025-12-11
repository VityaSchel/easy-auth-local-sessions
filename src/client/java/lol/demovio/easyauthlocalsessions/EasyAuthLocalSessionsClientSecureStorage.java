package lol.demovio.easyauthlocalsessions;

import net.fabricmc.loader.api.FabricLoader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

public class EasyAuthLocalSessionsClientSecureStorage {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private static SecretKey deriveKey(UUID playerUuid, String serverAddress) throws Exception {
        String derivedKeyString = playerUuid.toString() + " " + serverAddress;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] derivedKeyBytes = digest.digest(derivedKeyString.getBytes());
        return new SecretKeySpec(derivedKeyBytes, "AES");
    }

    private static Path getSessionFilePath(UUID playerUuid, String serverAddress) throws NoSuchAlgorithmException, IOException {
        String sessionFilePathName = playerUuid.toString() + " " + serverAddress;
        byte[] sessionFilePathNameHashed = MessageDigest.getInstance("SHA-256").digest(sessionFilePathName.getBytes());
        String sessionFilePathNameHashedHex = HexFormat.of().formatHex(sessionFilePathNameHashed);
        Path dirPath = FabricLoader.getInstance().getConfigDir().resolve("EasyAuthLocalSessions-client");
        try {
            Files.createDirectory(dirPath);
        } catch (java.nio.file.FileAlreadyExistsException e) {
            // skip
        }
        return dirPath.resolve(sessionFilePathNameHashedHex);
    }

    private final SecretKey secretKey;
    private final Path authTokenFilePath;

    public EasyAuthLocalSessionsClientSecureStorage(UUID playerUuid, String serverAddress) throws Exception {
        this.secretKey = EasyAuthLocalSessionsClientSecureStorage.deriveKey(playerUuid, serverAddress);
        this.authTokenFilePath = EasyAuthLocalSessionsClientSecureStorage.getSessionFilePath(playerUuid, serverAddress);
    }


    public void saveAuthToken(byte[] secret) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, spec);

        byte[] encrypted = cipher.doFinal(secret);

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        Files.write(this.authTokenFilePath, combined);
    }

    public byte[] getAuthToken() throws Exception {
        if (!Files.exists(this.authTokenFilePath)) {
            return null;
        }

        byte[] combined = Files.readAllBytes(this.authTokenFilePath);

        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, this.secretKey, spec);

        return cipher.doFinal(encrypted);
    }
}
