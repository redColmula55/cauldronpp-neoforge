package rc55.mc.cauldronpp.datagen.lang;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.item.CauldronppItems;

import static rc55.mc.cauldronpp.api.PotionHelper.potionPrefixTranslationKeys;

public class LangEnDataGen extends LanguageProvider {

    private static final String[] potionPrefixesTranslation = new String[]{"Mundane", "Uninteresting", "Bland", "Clear",
            "Milky", "Diffuse", "Artless", "Thin", "Awkward", "Flat",
            "Bulky", "Bungling", "Buttered", "Smooth", "Suave", "Debonair",
            "Thick", "Elegant", "Fancy", "Charming", "Dashing", "Refined",
            "Cordial", "Sparkling", "Potent", "Foul", "Odorless", "Rank",
            "Harsh", "Acrid", "Gross", "Stinky"};

    public LangEnDataGen(PackOutput output) {
        super(output, Cauldronpp.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(CauldronppBlocks.CPP_CAULDRON, "Cauldron(Legacy)");
        this.add(CauldronppItems.WATER_BOTTLE, "Water Bottle");
        generatePotionNameTranslation(this, potionPrefixesTranslation, "Potion", "Splash Potion", "Lingering Potion", "Tipped Arrow");
        this.add(CauldronppItems.CPP_POTION, "%s Potion");
        this.add(CauldronppItems.CPP_SPLASH_POTION, "%s Splash Potion");
        this.add(CauldronppItems.CPP_LINGERING_POTION, "%s Lingering Potion");
        this.add(CauldronppItems.CPP_TIPPED_ARROW, "%s Arrow");
    }

    private void add(DeferredBlock<?> block, String name) {
        this.add(block.get(), name);
    }

    private void add(DeferredItem<?> item, String name) {
        this.add(item.get(), name);
    }

    //药水名字本地化数据生成
    static void generatePotionNameTranslation(LanguageProvider builder, String[] translation, String potionTranslation,
                                                     String splashPotionTranslation, String lingeringPotionTranslation, String tippedArrowTranslation) {
        for (int i = 0; i < potionPrefixTranslationKeys.length; i++) {
            builder.add(potionPrefixTranslationKeys[i], translation[i]);
        }
        builder.add(CauldronppItems.CPP_POTION.get().getDescriptionId() + ".default_name", potionTranslation);
        builder.add(CauldronppItems.CPP_SPLASH_POTION.get().getDescriptionId() + ".default_name", splashPotionTranslation);
        builder.add(CauldronppItems.CPP_LINGERING_POTION.get().getDescriptionId() + ".default_name", lingeringPotionTranslation);
        builder.add(CauldronppItems.CPP_TIPPED_ARROW.get().getDescriptionId() + ".default_name", tippedArrowTranslation);
    }
}
