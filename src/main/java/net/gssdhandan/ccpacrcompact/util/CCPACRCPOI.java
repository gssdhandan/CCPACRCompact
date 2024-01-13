package net.gssdhandan.ccpacrcompact.util;

import com.google.common.collect.ImmutableSet;
import net.gssdhandan.ccpacrcompact.CCPACRCompact;
import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CCPACRCPOI {
    public static DeferredRegister<PoiType> POI
        = DeferredRegister.create(ForgeRegistries.POI_TYPES, CCPACRCompact.MODID);
	public static final RegistryObject<PoiType> CUSTOM_PORTAL =
			POI.register("custom_portal_block", () -> new PoiType(
					ImmutableSet.copyOf(CustomPortalsMod.getDefaultPortalBlock().getStateDefinition().getPossibleStates())
					, 0, 1));
	public static void register(IEventBus eventBus) {
		POI.register(eventBus);
	}
}
