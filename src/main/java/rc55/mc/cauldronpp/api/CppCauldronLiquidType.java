package rc55.mc.cauldronpp.api;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public enum CppCauldronLiquidType {
    NONE(0, false),
    WATER(1, Items.WATER_BUCKET, SoundEvents.BUCKET_FILL, true),
    POTION(2, true),
    LAVA(3, Items.LAVA_BUCKET, SoundEvents.BUCKET_FILL_LAVA, false),
    POWDER_SNOW(4, Items.POWDER_SNOW_BUCKET, SoundEvents.BUCKET_FILL_POWDER_SNOW, false),
    COLORED_WATER(5, true);

    public static CppCauldronLiquidType byId(int id) {
        return id > values().length || id < 0 ? NONE : values()[id];
    }

    private final int id;
    private final Item bucketItem;
    private final SoundEvent bucketFillSound;
    private final boolean shouldRenderColor;

    CppCauldronLiquidType(int id, Item bucketItem, SoundEvent bucketFillSound, boolean shouldRenderColor) {
        this.id = id;
        this.bucketItem = bucketItem;
        this.bucketFillSound = bucketFillSound;
        this.shouldRenderColor = shouldRenderColor;
    }
    CppCauldronLiquidType(int id, boolean shouldRenderColor) {
        this(id, null, null, shouldRenderColor);
    }

    public int getId() {
        return this.ordinal();
    }
    @Nullable
    public Item getBucketItem() {
        return this.bucketItem;
    }
    @Nullable
    public SoundEvent getBucketFillSound() {
        return this.bucketFillSound;
    }
    public boolean shouldRenderColor() {
        return this.shouldRenderColor;
    }
}
