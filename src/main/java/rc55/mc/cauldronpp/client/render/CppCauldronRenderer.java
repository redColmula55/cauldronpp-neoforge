package rc55.mc.cauldronpp.client.render;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.api.CppCauldronLiquidType;
import rc55.mc.cauldronpp.blockEntity.CppCauldronBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.EnumMap;
import java.util.Map;

public class CppCauldronRenderer implements BlockEntityRenderer<CppCauldronBlockEntity, CppCauldronRenderer.RenderState> {

    public static final SpriteId WATER_SPRITE_ID = Sheets.BLOCKS_MAPPER.apply(Identifier.fromNamespaceAndPath(Cauldronpp.MODID, "cauldron/inner_water"));
    public static final SpriteId POTION_SPRITE_ID = Sheets.BLOCKS_MAPPER.apply(Identifier.fromNamespaceAndPath(Cauldronpp.MODID, "cauldron/inner_potion"));
    public static final SpriteId LAVA_SPRITE_ID = Sheets.BLOCKS_MAPPER.apply(Identifier.fromNamespaceAndPath(Cauldronpp.MODID, "cauldron/inner_lava"));
    public static final SpriteId SNOW_SPRITE_ID = Sheets.BLOCKS_MAPPER.apply(Identifier.fromNamespaceAndPath(Cauldronpp.MODID, "cauldron/inner_snow"));

    public static final Map<CppCauldronLiquidType, SpriteId> SPRITES = Util.make(new EnumMap<>(CppCauldronLiquidType.class), map -> {
        map.put(CppCauldronLiquidType.WATER, WATER_SPRITE_ID);
        map.put(CppCauldronLiquidType.POTION, POTION_SPRITE_ID);
        map.put(CppCauldronLiquidType.LAVA, LAVA_SPRITE_ID);
        map.put(CppCauldronLiquidType.POWDER_SNOW, SNOW_SPRITE_ID);
        map.put(CppCauldronLiquidType.COLORED_WATER, WATER_SPRITE_ID);
    });

    private final CppCauldronModel model;
    private final SpriteGetter sprites;

    public CppCauldronRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new CppCauldronModel(ctx.bakeLayer(CauldronppRenderer.CAULDRON_INNER));
        this.sprites = ctx.sprites();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(CppCauldronBlockEntity blockEntity, RenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.blockEntity = blockEntity;
    }

    @Override
    public void submit(RenderState state, PoseStack matrices, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        final CppCauldronBlockEntity entity = state.blockEntity;
        Level world = entity.getLevel();
        if (world != null && !entity.isEmpty()) {
            BlockPos pos = entity.getBlockPos();
            int color = entity.getLiquidRenderColor((BlockAndTintGetter) world, pos);
            CppCauldronLiquidType liquidType = entity.getLiquidType();

            double height = entity.getFluidHeight();
            matrices.translate(0.5, height, 0.5);
            matrices.scale(0.75f, 1.0f, 0.75f);

            submitNodeCollector.submitModel(
                    this.model, CppCauldronModel.State.INSTANCE, matrices, state.lightCoords, OverlayTexture.NO_OVERLAY, color,
                    SPRITES.getOrDefault(liquidType, WATER_SPRITE_ID), this.sprites, 0, state.breakProgress
            );
        }
    }

    public static class RenderState extends BlockEntityRenderState {
        public CppCauldronBlockEntity blockEntity;
    }
}
