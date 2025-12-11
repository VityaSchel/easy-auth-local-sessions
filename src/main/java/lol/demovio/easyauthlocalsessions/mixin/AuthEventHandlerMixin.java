package lol.demovio.easyauthlocalsessions.mixin;

import lol.demovio.easyauthlocalsessions.EasyAuthLocalSessionsNetwork;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nikitacartes.easyauth.event.AuthEventHandler;

import java.util.UUID;

@Mixin(AuthEventHandler.class)
public class AuthEventHandlerMixin {
    @Inject(at = @At("TAIL"), method = "onPlayerJoin(Lnet/minecraft/server/network/ServerPlayerEntity;)V")
    private static void injected(ServerPlayerEntity player, CallbackInfo ci) {
        PacketByteBuf payload = PacketByteBufs.create();
        UUID playerUuid = player.getUuid();
        payload.writeUuid(playerUuid);
        ServerPlayNetworking.send(player, EasyAuthLocalSessionsNetwork.REQUEST_AUTH_TOKEN_PACKET_ID, payload);
    }
}