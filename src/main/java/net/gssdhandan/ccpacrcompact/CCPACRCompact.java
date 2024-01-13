package net.gssdhandan.ccpacrcompact;

import com.mojang.logging.LogUtils;
import net.gssdhandan.ccpacrcompact.util.CCPACRCPOI;
import net.kyrptonaught.customportalapi.CustomPortalBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//import net.minecraft.client.gui;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraft.sounds.SoundSource;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;


@Mod(CCPACRCompact.MODID)
public class CCPACRCompact {
    public static final String MODID = "ccpacrcompact";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CCPACRCompact() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CCPACRCPOI.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CCpaCCConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

}
