package mekanism.generators.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.model.ModelSolarGenerator;
import mekanism.generators.common.tile.TileEntitySolarGenerator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

public class RenderSolarGenerator extends TileEntityRenderer<TileEntitySolarGenerator> {

    private ModelSolarGenerator model = new ModelSolarGenerator();

    @Override
    public void render(TileEntitySolarGenerator tileEntity, double x, double y, double z, float partialTick, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "SolarGenerator.png"));
        GlStateManager.rotatef(180, 0, 0, 1);
        model.render(0.0625F);
        GlStateManager.popMatrix();
    }
}