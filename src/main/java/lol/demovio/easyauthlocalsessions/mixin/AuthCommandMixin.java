package lol.demovio.easyauthlocalsessions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lol.demovio.easyauthlocalsessions.EasyAuthLocalSessionsServerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nikitacartes.easyauth.commands.AuthCommand;

@Mixin(AuthCommand.class)
public class AuthCommandMixin {
    @Inject(method = "removeAccount(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I", at = @At("TAIL"))
    private static void injected(CallbackInfoReturnable<Integer> cir, @Local(name = "playerEntity") ServerPlayerEntity playerEntity) {
        if (playerEntity != null) {
            EasyAuthLocalSessionsServerManager.revokeAllAuthTokens(playerEntity);
        }
    }

    @Inject(method = "lambda$updatePassword$17", at = @At(value = "INVOKE", target = "Lxyz/nikitacartes/easyauth/utils/AuthHelper;hashPassword([C)Ljava/lang/String;", shift = At.Shift.AFTER))
    private static void injected(CallbackInfo ci, @Local(argsOnly = true, name = "arg1") ServerCommandSource source) {
        ServerPlayerEntity playerEntity = source.getPlayer();
        if (playerEntity != null) {
            EasyAuthLocalSessionsServerManager.revokeAllAuthTokens(playerEntity);
        }
    }
}
