package rc55.mc.cauldronpp.client.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class CppCauldronModel extends Model<CppCauldronModel.State> {
    private final ModelPart model;

    public CppCauldronModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.model = root.getChild("main");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition data = new MeshDefinition();
        PartDefinition partData = data.getRoot();
        partData.addOrReplaceChild("main", CubeListBuilder.create().texOffs(-32, 0).addBox(-8.0F, 0.1F, -8.0F, 16.0F, 0.1F, 16.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(data, 16, 16);
    }

    public ModelPart getModel() {
        return model;
    }

    public enum State {
        INSTANCE;
    }
}
