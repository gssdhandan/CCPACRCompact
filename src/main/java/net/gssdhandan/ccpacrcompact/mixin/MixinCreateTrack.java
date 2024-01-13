package net.gssdhandan.ccpacrcompact.mixin;

import com.simibubi.create.content.trains.track.AllPortalTracks;
import net.gssdhandan.ccpacrcompact.util.CreatePortalTrackProvider;
import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AllPortalTracks.class)
public class MixinCreateTrack {
    @Inject(at = @At(value = "HEAD"), method = "registerDefaults", remap = false)
    private static void moreRegister(CallbackInfo ci){
        CreatePortalTrackProvider ProviderClass = new CreatePortalTrackProvider();
        AllPortalTracks.registerIntegration(new ResourceLocation(CustomPortalsMod.MOD_ID, "custom_portal_block"),
                ProviderClass::provider);
    }
}
