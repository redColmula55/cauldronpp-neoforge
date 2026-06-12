package rc55.mc.cauldronpp.item;

import rc55.mc.cauldronpp.api.PotionHelper;

import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.alchemy.PotionContents;

public class CppSplashPotionItem extends SplashPotionItem {

    public static final Item.Properties SETTINGS = new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

    public CppSplashPotionItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public Component getName(ItemStack stack) {
        PotionContents component = stack.get(DataComponents.POTION_CONTENTS);
        if (component == null) return PotionHelper.getPotionNameFromNbt(stack);
        Optional<String> optional = component.customName();
        return optional.map(string -> Component.translatable(this.getDescriptionId(), Component.translatable(string)))
                .orElseGet(() -> (MutableComponent) PotionHelper.getPotionNameFromNbt(stack));
    }
}
