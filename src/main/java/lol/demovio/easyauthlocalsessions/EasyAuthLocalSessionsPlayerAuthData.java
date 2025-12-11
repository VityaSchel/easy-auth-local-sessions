package lol.demovio.easyauthlocalsessions;

import org.jetbrains.annotations.Nullable;

public class EasyAuthLocalSessionsPlayerAuthData {
    @Nullable
    public String authorizationTokenHash = null;

    public EasyAuthLocalSessionsPlayerAuthData(@Nullable String tokenHash) {
        this.authorizationTokenHash = tokenHash;
    }
}
