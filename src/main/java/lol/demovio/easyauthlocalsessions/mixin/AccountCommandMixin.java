package lol.demovio.easyauthlocalsessions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lol.demovio.easyauthlocalsessions.EasyAuthLocalSessionsServerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nikitacartes.easyauth.commands.AccountCommand;
import xyz.nikitacartes.easyauth.utils.PlayerAuth;

@Mixin(AccountCommand.class)
public class AccountCommandMixin {
    @Inject(method = "lambda$unregister$6", at = @At(value = "INVOKE", target = "Lxyz/nikitacartes/easyauth/storage/database/DbApi;deleteUserData(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private static void onUnregister(ServerPlayerEntity player, PlayerAuth playerAuth, String pass, ServerCommandSource source, CallbackInfo ci) {
        EasyAuthLocalSessionsServerManager.revokeAllAuthTokens(player);
    }

    @Inject(method = "lambda$changePassword$7", at = @At(value = "INVOKE", target = "Lxyz/nikitacartes/easyauth/utils/AuthHelper;hashPassword([C)Ljava/lang/String;", shift = At.Shift.AFTER))
    private static void onChangePassword(CallbackInfo ci, @Local(argsOnly = true, name = "arg3") ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            EasyAuthLocalSessionsServerManager.revokeAllAuthTokens(player);
        }
    }
}
