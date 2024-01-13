package net.gssdhandan.ccpacrcompact.util;

import net.gssdhandan.ccpacrcompact.CCpaCCConfig;
import net.gssdhandan.ccpacrcompact.cpaextend.ExtendCPBuilder;
import net.kyrptonaught.customportalapi.CustomPortalBlock;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigAddPortal {
    public enum LightType{
        FIRE("Fire"),WATER("Water"),ITEM("Item"),FLUID("Fluid"),CUSTOM("Custom");
        private final String type;
        LightType(String type) {
            this.type = type;
        }
        public String getType(){
            return type;
        }
    }
    public static ConcurrentHashMap<PortalLink, Double> portalLinkCoordinateDiffMap = new ConcurrentHashMap<>();
    public static void registerPortals(){
        Iterator<? extends List<List<String>>> it = CCpaCCConfig.dimensions.iterator();
        while (it.hasNext()) {
            Iterator<List<String>> dimensionsInfo = it.next().iterator();
            List<String> teleportInfo = dimensionsInfo.next();
            List<String> lightInfo = dimensionsInfo.next();
            List<String> rgbInfo = dimensionsInfo.next();
            ResourceLocation dimensionA = ResourceLocation.tryParse(teleportInfo.get(0));
            ResourceLocation dimensionB = ResourceLocation.tryParse(teleportInfo.get(1));
            ResourceLocation frameBlock = ResourceLocation.tryParse(teleportInfo.get(2));
            double coordinateDiff = 1.0;
            if (teleportInfo.size() >= 4){
                try {
                    coordinateDiff = Double.parseDouble(teleportInfo.get(3));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            LightType typeSelect = LightType.FIRE;
            if (!lightInfo.isEmpty()){
                for (LightType type : LightType.values()){
                    if (lightInfo.get(0).equals(type.getType())){
                        typeSelect = type;
                        break;
                    }
                }
            }
            int[] rgb = {127, 127, 127};
            int i = 0;
            for (String eachRGB : rgbInfo){
                try {
                    rgb[i] = constraintRGB(Integer.parseInt(eachRGB));
                }catch (Exception e){
                    e.printStackTrace();
                }
                i++;
            }
            ExtendCPBuilder.beginPortal()
                    .returnDimID(dimensionA).destDimID(dimensionB).frameBlock(frameBlock)
                    .setLightType(typeSelect, lightInfo).tintColor(rgb[0],rgb[1],rgb[2])
                    .setCoordinateDiff(coordinateDiff).registerPortal();
        }
    }

    private static int constraintRGB(int rgb){
        if (rgb > 255){
            return 255;
        } else if (rgb < 0) {
            return 0;
        }else {
            return rgb;
        }
    }
}
