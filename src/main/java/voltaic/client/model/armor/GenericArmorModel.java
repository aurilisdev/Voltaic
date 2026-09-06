package voltaic.client.model.armor;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class GenericArmorModel<T extends LivingEntity> extends HumanoidModel<T> {

    protected final ModelPart parentHat;
    protected final ModelPart parentHead;
    protected final ModelPart parentChest;
    protected final ModelPart parentRightArm;
    protected final ModelPart parentLeftArm;
    protected final ModelPart parentRightLeg;
    protected final ModelPart parentLeftLeg;

    protected static final String HAT = "hat";
    protected static final String HEAD = "head";
    protected static final String CHEST = "body";
    protected static final String RIGHT_ARM = "right_arm";
    protected static final String LEFT_ARM = "left_arm";
    protected static final String RIGHT_LEG = "right_leg";
    protected static final String LEFT_LEG = "left_leg";

    public GenericArmorModel(ModelPart root, Function<ResourceLocation, RenderType> function) {
	super(root, function);
	parentHat = root.getChild(HAT);
	parentHead = root.getChild(HEAD);
	parentChest = root.getChild(CHEST);
	parentRightArm = root.getChild(RIGHT_ARM);
	parentLeftArm = root.getChild(LEFT_ARM);
	parentRightLeg = root.getChild(RIGHT_LEG);
	parentLeftLeg = root.getChild(LEFT_LEG);
    }

    public GenericArmorModel(ModelPart root) {
	this(root, RenderType::entityCutoutNoCull);
    }

    protected VertexConsumer getCustomConsumer(RenderType type) {
	return Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(type);
    }

}
