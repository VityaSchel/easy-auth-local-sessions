package lol.demovio.easyauthlocalsessions.mixin;

import lol.demovio.easyauthlocalsessions.EasyAuthLocalSessionsPlayerAuth;
import lol.demovio.easyauthlocalsessions.EasyAuthLocalSessionsPlayerAuthData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerAuthMixin implements EasyAuthLocalSessionsPlayerAuth {

    @Unique
    private EasyAuthLocalSessionsPlayerAuthData easyAuthLocalSessions$authData = null;


    public EasyAuthLocalSessionsPlayerAuthData easyAuthLocalSessions$getAuthData() {
        return this.easyAuthLocalSessions$authData;
    }

    public void easyAuthLocalSessions$setAuthData(EasyAuthLocalSessionsPlayerAuthData authData) {
        this.easyAuthLocalSessions$authData = authData;
    }
}
