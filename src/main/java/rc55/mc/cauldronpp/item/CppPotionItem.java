package rc55.mc.cauldronpp.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.context.UseOnContext;
import rc55.mc.cauldronpp.api.PotionHelper;

import java.util.Optional;

public class CppPotionItem extends PotionItem {

    public static final Properties SETTING = new Properties().craftRemainder(Items.GLASS_BOTTLE).usingConvertsTo(Items.GLASS_BOTTLE)
            .component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
            .component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK);

    public CppPotionItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
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
