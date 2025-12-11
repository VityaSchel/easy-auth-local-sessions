package lol.demovio.easyauthlocalsessions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lol.demovio.easyauthlocalsessions.EasyAuthLocalSessionsServerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nikitacartes.easyauth.commands.LoginCommand;
import xyz.nikitacartes.easyauth.utils.PlayerAuth;

@Mixin(LoginCommand.class)
public class LoginCommandMixin {
    @Inject(method = "login(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I", at = @At(value = "INVOKE", target = "Lxyz/nikitacartes/easyauth/utils/PlayerAuth;easyAuth$setAuthenticated(Z)V", shift = At.Shift.AFTER))
    private static void injected(CallbackInfoReturnable<Integer> cir, @Local(name = "player") ServerPlayerEntity player, @Local(name = "playerAuth") PlayerAuth playerAuth) {
        EasyAuthLocalSessionsServerManager.sendAuthToken(player);
    }
}
