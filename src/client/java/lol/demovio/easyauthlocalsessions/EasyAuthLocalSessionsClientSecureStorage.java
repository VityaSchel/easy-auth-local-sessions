package lol.demovio.easyauthlocalsessions;

import net.fabricmc.loader.api.FabricLoader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class EasyAuthLocalSessionsClientSecureStorage {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private static Path getSecretFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("EasyAuthLocalSessions");
    }

    private static SecretKey deriveKey() throws Exception {
        String machineId = "TODO: change derivation";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(machineId.getBytes());
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void saveSecret(byte[] secret) throws Exception {
        SecretKey key = deriveKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encrypted = cipher.doFinal(secret);

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        Files.write(getSecretFile(), combined);
    }

    public static byte[] loadSecret() throws Exception {
        if (!Files.exists(getSecretFile())) {
            return null;
        }

        byte[] combined = Files.readAllBytes(getSecretFile());

        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

        SecretKey key = deriveKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        return cipher.doFinal(encrypted);
    }
}
