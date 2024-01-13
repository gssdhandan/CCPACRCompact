package net.gssdhandan.ccpacrcompact;

import net.gssdhandan.ccpacrcompact.util.ConfigAddPortal;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = CCPACRCompact.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCpaCCConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<List<? extends List<List<String>>>> DIMENSIONS = BUILDER
            .comment("""
                    Here is the configs to add portal betweem 2 dimensions using custom portal api.
                    All conifgs of one custom portal  shoud in a list([...]), A list([...]) of conifgs of one custom portal should have at least 3 list([...]).
                    Each list sholud in format [[Dimensions' Info], [Light portal type], [portal RGB color]].
                    [Dimensions' Info] -> [modid:DimAID, modid:DimBID, modid:FrameBlockID, diffPara(Optional)].
                                      DimA(X, Y, Z) = diffPara * DimB(X, Y, Z), diffPara is vacant or can not be convert to Double, diffPara = 1.0.
                    [Light portal type] -> [Light type, modid:ID], Light type -> Fire, Item, Water, Fluid, Custom. Empty, not any or modid:ID is not exist,Light type = 'Fire'.
                    [portal RGB color] -> [R, G, B]. R/G/B is empty or can not be convert to Integer, R/G/B = 127.
                    [Light portal type], [portal RGB color]  can be Empty list([]) but can not be not any thing.
                    """)
            .defineListAllowEmpty("Dimension Configs",
                    List.of(List.of(List.of("minecraft:overworld", "minecraft:the_end","minecraft:end_stone_bricks", "8.0"),
                                    List.of("Item", "minecraft:ender_eye"),
                                    List.of("45", "61", "101")),
                            List.of(List.of("minecraft:the_nether", "minecraft:the_end", "minecraft:end_stone", "0.015625"),
                                    List.of("Fire"),
                                    List.of("77", "88", "22")
                            )
                    ),
                    CCpaCCConfig::validateDimensionConfig);
    private static final ForgeConfigSpec.ConfigValue<Boolean> VANILLA_MOVE_ENTITY = BUILDER
            .comment("""
                    Whether to use the Custom Portal API Vanilla teleport entity method.
                    If this set to 'true', the diffPara of Dimension config wll be invalid.(Default=false).
                    """).
            define("Vanilla CPA teleport", false);
    private static final ForgeConfigSpec.ConfigValue<Boolean> VANILLA_PORTAL = BUILDER
            .comment("""
                    Whether to use the Custom Portal API Vanilla Find, link and create portal method.
                    If the previous config(Vanilla CPA teleport) is set to 'true', this will be invalid.
                    Tips:Custom Portal API will save the link of portal between 2 dimension in .nbt file.
                         Once a Link is created, it is not deleted, even if the portal is destroyed.
                         If this set to 'false', the program will dynamically locate the portal at the destination.(Default=false).
                    """).
            define("Vanilla CPA Portal", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static List<? extends List<List<String>>> dimensions;
    public static Boolean vanilla_portal;
    public static Boolean vanilla_move_entity;

    private static boolean validateDimensionConfig(final Object obj) {
        if (obj instanceof final List dimensionInfo && dimensionInfo.size() >= 3){
            if (dimensionInfo.get(0) instanceof final List teleportInfo){
                if (teleportInfo.size() >= 3){
                    if (ResourceLocation.isValidResourceLocation((String) teleportInfo.get(0))
                            && ResourceLocation.isValidResourceLocation((String) teleportInfo.get(1))
                            && ResourceLocation.isValidResourceLocation((String) teleportInfo.get(2))
                            && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation((String) teleportInfo.get(2)))){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        dimensions = DIMENSIONS.get();
        vanilla_portal = VANILLA_PORTAL.get();
        vanilla_move_entity = VANILLA_MOVE_ENTITY.get();
        ConfigAddPortal.registerPortals();
    }
}
