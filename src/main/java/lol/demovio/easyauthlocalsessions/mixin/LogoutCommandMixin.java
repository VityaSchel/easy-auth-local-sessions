package lol.demovio.easyauthlocalsessions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lol.demovio.easyauthlocalsessions.EasyAuthLocalSessionsServerStorage;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nikitacartes.easyauth.commands.LogoutCommand;
import xyz.nikitacartes.easyauth.utils.PlayerAuth;

@Mixin(LogoutCommand.class)
public class LogoutCommandMixin {
    @Inject(method = "logout(Lnet/minecraft/server/command/ServerCommandSource;)I", at = @At(value = "INVOKE", target = "Lxyz/nikitacartes/easyauth/utils/PlayerAuth;easyAuth$setAuthenticated(Z)V", shift = At.Shift.AFTER))
    private static void injected(CallbackInfoReturnable<Integer> cir, @Local(name = "player") ServerPlayerEntity player, @Local(name = "playerAuth") PlayerAuth playerAuth) {
        EasyAuthLocalSessionsServerStorage.deleteAuthorizationToken(player);
        playerAuth
    }
}
