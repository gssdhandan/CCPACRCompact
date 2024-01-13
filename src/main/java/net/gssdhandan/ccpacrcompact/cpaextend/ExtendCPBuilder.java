package net.gssdhandan.ccpacrcompact.cpaextend;

import net.gssdhandan.ccpacrcompact.CCpaCCConfig;
import net.gssdhandan.ccpacrcompact.util.ConfigAddPortal;
import net.kyrptonaught.customportalapi.CustomPortalApiRegistry;
import net.kyrptonaught.customportalapi.CustomPortalBlock;
import net.kyrptonaught.customportalapi.CustomPortalsMod;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.kyrptonaught.customportalapi.portal.PortalIgnitionSource;
import net.kyrptonaught.customportalapi.util.CPASoundEventData;
import net.kyrptonaught.customportalapi.util.ColorUtil;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.kyrptonaught.customportalapi.util.SHOULDTP;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExtendCPBuilder {
    private final PortalLink portalLink = new PortalLink();
    public ExtendCPBuilder(){}
    public static ExtendCPBuilder beginPortal() {
        return new ExtendCPBuilder();
    }
    public void registerPortal() {
        CustomPortalApiRegistry.addPortal(ForgeRegistries.BLOCKS.getValue(this.portalLink.block), this.portalLink);
    }
    public ExtendCPBuilder setCoordinateDiff(double coordinateDiff){
        ConfigAddPortal.portalLinkCoordinateDiffMap.put(this.portalLink, coordinateDiff);
        return this;
    }

    public ExtendCPBuilder frameBlock(ResourceLocation blockID) {
        this.portalLink.block = blockID;
        return this;
    }

    public ExtendCPBuilder returnDimID(ResourceLocation dimID) {
        return returnDim(dimID, true);
    }
    public ExtendCPBuilder destDimID(ResourceLocation dimID) {
        this.portalLink.dimID = dimID;
        return this;
    }

    public ExtendCPBuilder tintColor(int r, int g, int b) {
        this.portalLink.colorID = ColorUtil.getColorFromRGB(r, g, b);
        return this;
    }

    public ExtendCPBuilder lightWithWater() {
        this.portalLink.portalIgnitionSource = PortalIgnitionSource.WATER;
        return this;
    }

    public ExtendCPBuilder lightWithItem(Item item) {
        this.portalLink.portalIgnitionSource = PortalIgnitionSource.ItemUseSource(item);
        return this;
    }

    public ExtendCPBuilder lightWithFluid(Fluid fluid) {
        this.portalLink.portalIgnitionSource = PortalIgnitionSource.FluidSource(fluid);
        return this;
    }

    public ExtendCPBuilder customIgnitionSource(ResourceLocation customSourceID) {
        this.portalLink.portalIgnitionSource = PortalIgnitionSource.CustomSource(customSourceID);
        return this;
    }

    public ExtendCPBuilder forcedSize(int width, int height) {
        this.portalLink.forcedWidth = width;
        this.portalLink.forcedHeight = height;
        return this;
    }

    public ExtendCPBuilder customPortalBlock(CustomPortalBlock portalBlock) {
        this.portalLink.setPortalBlock(portalBlock);
        return this;
    }

    public ExtendCPBuilder returnDim(ResourceLocation returnDimID, boolean onlyIgnitableInReturnDim) {
        this.portalLink.returnDimID = returnDimID;
        this.portalLink.onlyIgnitableInReturnDim = onlyIgnitableInReturnDim;
        return this;
    }
    public ExtendCPBuilder setLightType(ConfigAddPortal.LightType type, List<String> lightInfo){
        switch (type){
            case ITEM -> {
                if (lightInfo.size() >= 2
                        && ResourceLocation.isValidResourceLocation(lightInfo.get(1))
                        && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(lightInfo.get(1)))){
                    return lightWithItem(ForgeRegistries.ITEMS.getValue(new ResourceLocation(lightInfo.get(1))));
                }else break;
            }
            case WATER -> {return lightWithWater();}
            case FLUID -> {
                if (lightInfo.size() >= 2
                        && ResourceLocation.isValidResourceLocation(lightInfo.get(1))
                        && ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(lightInfo.get(1)))){
                    return lightWithFluid(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(lightInfo.get(1))));
                }else break;
            }
            case CUSTOM -> {
                if (lightInfo.size() >= 2
                        && ResourceLocation.isValidResourceLocation(lightInfo.get(1))){
                    return customIgnitionSource(new ResourceLocation(lightInfo.get(1)));
                }else break;
            }
        }
        return this;
    }
}
