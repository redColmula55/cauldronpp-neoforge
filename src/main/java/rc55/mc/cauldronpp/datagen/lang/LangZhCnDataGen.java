package rc55.mc.cauldronpp.datagen.lang;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.item.CauldronppItems;

public class LangZhCnDataGen extends LanguageProvider {
    public LangZhCnDataGen(PackOutput output) {
        super(output, Cauldronpp.MODID, "zh_cn");
    }

    private static final String[] potionPrefixesTranslation = new String[]{"平凡的", "枯燥的", "平淡的", "清澈的",
            "乳白的", "弥漫的", "朴实的", "稀薄的", "粗制的", "平坦的",
            "笨重的", "笨拙的", "圆滑的", "平滑的", "倜傥的", "温和的",
            "浓稠的", "高雅的", "花哨的", "迷人的", "迅速的", "精致的",
            "亲切的", "闪亮的", "有力的", "犯规的", "无味的", "稠密的",
            "苛刻的", "辛辣的", "多毛的", "发臭的"};

    @Override
    protected void addTranslations() {
//        this.add("modmenu.nameTranslation.cauldronpp", "Cauldron++");
//        this.add("modmenu.descriptionTranslation.cauldronpp", "Cauldron++是一个小型mod，旨在高版本中还原Beta 1.9-pre2中使用炼药锅的酿造系统。");
        this.add(CauldronppBlocks.CPP_CAULDRON, "炼药锅（旧版）");
        this.add(CauldronppItems.WATER_BOTTLE, "水瓶");
        LangEnDataGen.generatePotionNameTranslation(this, potionPrefixesTranslation, "药水", "喷溅型药水", "滞留型药水", "药箭");
        this.add(CauldronppItems.CPP_POTION, "%s药水");
        this.add(CauldronppItems.CPP_SPLASH_POTION, "喷溅型%s药水");
        this.add(CauldronppItems.CPP_LINGERING_POTION, "滞留型%s药水");
        this.add(CauldronppItems.CPP_TIPPED_ARROW, "%s药箭");
    }

    private void add(DeferredBlock<?> block, String name) {
        this.add(block.get(), name);
    }

    private void add(DeferredItem<?> item, String name) {
        this.add(item.get(), name);
    }
}
