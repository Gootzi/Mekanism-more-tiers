package mekanism.client.gui.element;

import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.text.ITextComponent;

public class GuiGraph extends GuiTexturedElement {

    private static int textureWidth = 3;
    private static int textureHeight = 2;

    private final GuiInnerScreen innerScreen;
    private final LongList graphData = new LongArrayList();
    private final GraphDataHandler dataHandler;

    private long currentScale = 10;
    private boolean fixedScale = false;

    public GuiGraph(IGuiWrapper gui, int x, int y, int width, int height, GraphDataHandler handler) {
        super(MekanismUtils.getResource(ResourceType.GUI, "graph.png"), gui, x, y, width, height);
        innerScreen = new GuiInnerScreen(gui, x - 1, y - 1, width + 2, height + 2);
        dataHandler = handler;
    }

    public void enableFixedScale(long scale) {
        fixedScale = true;
        currentScale = scale;
    }

    public void addData(long data) {
        if (graphData.size() == width) {
            graphData.removeLong(0);
        }

        graphData.add(data);
        if (!fixedScale) {
            for (long i : graphData) {
                if (i > currentScale) {
                    currentScale = i;
                }
            }
        }
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        //Draw Black and border
        innerScreen.renderButton(mouseX, mouseY, partialTicks);
        minecraft.textureManager.bindTexture(getResource());
        //Draw the graph
        int size = graphData.size();
        for (int i = 0; i < size; i++) {
            long data = Math.min(currentScale, graphData.getLong(i));
            int relativeHeight = (int) (data * height / (double) currentScale);
            blit(x + i, y + height - relativeHeight, 0, 0, 1, 1, textureWidth, textureHeight);

            RenderSystem.shadeModel(GL11.GL_SMOOTH);
            RenderSystem.disableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

            RenderSystem.color4f(1, 1, 1, 0.2F + 0.8F * i / size);
            blit(x + i, y + height - relativeHeight, 1, 0, 1, relativeHeight, textureWidth, textureHeight);

            int hoverIndex = mouseX - getButtonX();
            if (hoverIndex == i && mouseY >= getButtonY() && mouseY < getButtonY() + height) {
                RenderSystem.color4f(1, 1, 1, 0.5F);
                blit(x + i, y, 2, 0, 1, height, textureWidth, textureHeight);
                MekanismRenderer.resetColor();
                blit(x + i, y + height - relativeHeight, 0, 1, 1, 1, textureWidth, textureHeight);
            }

            MekanismRenderer.resetColor();
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
        }
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        int hoverIndex = mouseX - relativeX;
        if (hoverIndex >= 0 && hoverIndex < graphData.size()) {
            displayTooltip(dataHandler.getDataDisplay(graphData.getLong(hoverIndex)), mouseX, mouseY);
        }
    }

    public interface GraphDataHandler {

        ITextComponent getDataDisplay(long data);
    }
}