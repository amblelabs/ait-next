package dev.amble.ait.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.amble.ait.api.AitAPI;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
		import net.minecraft.world.entity.Entity;


@SuppressWarnings("FieldCanBeLocal")
public class PoliceBoxModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(AitAPI.modLoc("policeboxmodel"), "main");
	private final ModelPart base;
	private final ModelPart roof;
	private final ModelPart roof_default;
	private final ModelPart roof_t;
	private final ModelPart signs;
	private final ModelPart sign_n;
	private final ModelPart sign_e;
	private final ModelPart sign_s;
	private final ModelPart sign_w;
	private final ModelPart lamp;
	private final ModelPart glass;
	private final ModelPart top;
	private final ModelPart bottom;
	private final ModelPart pillars;
	private final ModelPart walls;
	private final ModelPart doors;
	private final ModelPart door_l;
	private final ModelPart phone_box;
	private final ModelPart sign_pto;
	private final ModelPart telephone;
	private final ModelPart cable;
	private final ModelPart bell1;
	private final ModelPart bell2;
	private final ModelPart receiver;
	private final ModelPart sign_pto_t;
	private final ModelPart telephone_t;
	private final ModelPart bell1_t;
	private final ModelPart bell2_t;
	private final ModelPart receiver_t;
	private final ModelPart cable_t;
	private final ModelPart door_r;
	private final ModelPart wall_e;
	private final ModelPart wall_s;
	private final ModelPart wall_w;

	public PoliceBoxModel(ModelPart root) {
		this.base = root.getChild("base");
		this.roof = this.base.getChild("roof");
		this.roof_default = this.roof.getChild("roof_default");
		this.roof_t = this.roof.getChild("roof_t");
		this.signs = this.roof.getChild("signs");
		this.sign_n = this.signs.getChild("sign_n");
		this.sign_e = this.signs.getChild("sign_e");
		this.sign_s = this.signs.getChild("sign_s");
		this.sign_w = this.signs.getChild("sign_w");
		this.lamp = this.roof.getChild("lamp");
		this.glass = this.lamp.getChild("glass");
		this.top = this.lamp.getChild("top");
		this.bottom = this.lamp.getChild("bottom");
		this.pillars = this.base.getChild("pillars");
		this.walls = this.base.getChild("walls");
		this.doors = this.walls.getChild("doors");
		this.door_l = this.doors.getChild("door_l");
		this.phone_box = this.door_l.getChild("phone_box");
		this.sign_pto = this.door_l.getChild("sign_pto");
		this.telephone = this.sign_pto.getChild("telephone");
		this.cable = this.telephone.getChild("cable");
		this.bell1 = this.telephone.getChild("bell1");
		this.bell2 = this.telephone.getChild("bell2");
		this.receiver = this.telephone.getChild("receiver");
		this.sign_pto_t = this.door_l.getChild("sign_pto_t");
		this.telephone_t = this.sign_pto_t.getChild("telephone_t");
		this.bell1_t = this.telephone_t.getChild("bell1_t");
		this.bell2_t = this.telephone_t.getChild("bell2_t");
		this.receiver_t = this.telephone_t.getChild("receiver_t");
		this.cable_t = this.telephone_t.getChild("cable_t");
		this.door_r = this.doors.getChild("door_r");
		this.wall_e = this.walls.getChild("wall_e");
		this.wall_s = this.walls.getChild("wall_s");
		this.wall_w = this.walls.getChild("wall_w");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, 22.125F, -12.0F, 24.0F, 2.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.125F, 0.0F));

		PartDefinition roof = base.addOrReplaceChild("roof", CubeListBuilder.create(), PartPose.offset(12.0F, -10.875F, -12.0F));

		PartDefinition roof_default = roof.addOrReplaceChild("roof_default", CubeListBuilder.create().texOffs(0, 58).addBox(-10.0F, -6.0F, -10.0F, 20.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
				.texOffs(0, 27).addBox(-11.0F, -4.0F, -11.0F, 22.0F, 8.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(-12.0F, -4.0F, 12.0F));

		PartDefinition roof_t = roof.addOrReplaceChild("roof_t", CubeListBuilder.create().texOffs(0, 151).addBox(-10.0F, -5.0F, -10.0F, 20.0F, 1.0F, 20.0F, new CubeDeformation(0.0F))
				.texOffs(0, 173).addBox(-9.0F, -6.0F, -9.0F, 18.0F, 1.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(-12.0F, -4.0F, 12.0F));

		PartDefinition signs = roof.addOrReplaceChild("signs", CubeListBuilder.create(), PartPose.offset(-12.0F, 0.0F, 12.0F));

		PartDefinition sign_n = signs.addOrReplaceChild("sign_n", CubeListBuilder.create().texOffs(116, 28).addBox(-9.5F, -4.5F, -12.6F, 19.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(116, 0).addBox(-10.0F, -5.0F, -12.5F, 20.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition sign_e = signs.addOrReplaceChild("sign_e", CubeListBuilder.create().texOffs(119, 32).addBox(-9.5F, -4.5F, -12.6F, 19.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(116, 7).addBox(-10.0F, -5.0F, -12.5F, 20.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition sign_s = signs.addOrReplaceChild("sign_s", CubeListBuilder.create().texOffs(119, 36).addBox(-9.5F, -4.5F, -12.6F, 19.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(116, 14).addBox(-10.0F, -5.0F, -12.5F, 20.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition sign_w = signs.addOrReplaceChild("sign_w", CubeListBuilder.create().texOffs(119, 40).addBox(-9.5F, -4.5F, -12.6F, 19.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(116, 21).addBox(-10.0F, -5.0F, -12.5F, 20.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition lamp = roof.addOrReplaceChild("lamp", CubeListBuilder.create(), PartPose.offset(-12.0F, -9.0F, 12.0F));

		PartDefinition glass = lamp.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(119, 57).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(119, 64).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.15F))
				.texOffs(131, 44).addBox(-2.5F, 0.5F, -2.5F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(131, 49).addBox(-2.5F, -0.5F, -2.5F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition top = lamp.addOrReplaceChild("top", CubeListBuilder.create().texOffs(119, 44).addBox(-3.4142F, -2.0F, -0.5858F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -5.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r1 = top.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(71, 81).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4142F, -2.0F, 1.4142F, 0.0F, -0.7854F, 0.0F));

		PartDefinition bottom = lamp.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(119, 51).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(71, 85).addBox(0.0F, -3.0F, -2.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r2 = bottom.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(89, 27).addBox(0.0F, -3.0F, -2.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r3 = bottom.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(77, 85).addBox(0.0F, -3.0F, -2.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r4 = bottom.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(74, 85).addBox(0.0F, -3.0F, -2.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition pillars = base.addOrReplaceChild("pillars", CubeListBuilder.create().texOffs(0, 81).addBox(-11.5F, -39.0F, -11.5F, 3.0F, 39.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.125F, 0.0F));

		PartDefinition cube_r5 = pillars.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(39, 81).addBox(-11.5F, -39.0F, -11.5F, 3.0F, 39.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r6 = pillars.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(26, 81).addBox(-11.5F, -39.0F, -11.5F, 3.0F, 39.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r7 = pillars.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(13, 81).addBox(-11.5F, -39.0F, -11.5F, 3.0F, 39.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition walls = base.addOrReplaceChild("walls", CubeListBuilder.create(), PartPose.offset(0.0F, 5.625F, 0.0F));

		PartDefinition doors = walls.addOrReplaceChild("doors", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition door_l = doors.addOrReplaceChild("door_l", CubeListBuilder.create().texOffs(52, 81).addBox(0.0F, -16.5F, 0.0F, 8.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(52, 116).addBox(8.0F, -16.5F, -0.5F, 1.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(73, 8).addBox(1.5F, -15.5F, -0.25F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.025F))
				.texOffs(73, 1).addBox(1.5F, -15.0F, -0.1F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.5F, 0.0F, -10.5F));

		PartDefinition phone_box = door_l.addOrReplaceChild("phone_box", CubeListBuilder.create().texOffs(67, 27).addBox(1.0F, -7.5F, -1.0F, 6.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(65, 40).addBox(1.0F, -0.5F, -3.0F, 6.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(81, 25).addBox(1.0F, -7.5F, -3.0F, 0.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(65, 36).addBox(1.0F, -7.5F, -3.0F, 6.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(81, 33).addBox(7.0F, -7.5F, -3.0F, 0.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.0F));

		PartDefinition sign_pto = door_l.addOrReplaceChild("sign_pto", CubeListBuilder.create().texOffs(73, 16).addBox(0.0F, -3.5F, 0.0F, 6.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(84, 1).addBox(1.5F, -2.0F, -0.1F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(84, 10).addBox(5.5F, -1.5F, -1.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -4.0F, 0.0F));

		PartDefinition telephone = sign_pto.addOrReplaceChild("telephone", CubeListBuilder.create().texOffs(61, 58).addBox(1.0F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -0.5F, 1.0F));

		PartDefinition cable = telephone.addOrReplaceChild("cable", CubeListBuilder.create().texOffs(87, 34).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 3.0F, 0.0F, 0.0F, 0.3927F, 0.0F));

		PartDefinition bell1 = telephone.addOrReplaceChild("bell1", CubeListBuilder.create(), PartPose.offset(4.75F, -1.0F, 0.5F));

		PartDefinition cube_r8 = bell1.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(74, 64).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition bell2 = telephone.addOrReplaceChild("bell2", CubeListBuilder.create(), PartPose.offset(4.75F, 2.0F, 0.5F));

		PartDefinition cube_r9 = bell2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(74, 64).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition receiver = telephone.addOrReplaceChild("receiver", CubeListBuilder.create().texOffs(74, 58).addBox(-1.0F, -2.5F, -0.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.25F, 0.5F, 0.5F));

		PartDefinition sign_pto_t = door_l.addOrReplaceChild("sign_pto_t", CubeListBuilder.create().texOffs(7, 163).addBox(-6.0F, -3.5F, 0.0F, 6.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(13, 158).addBox(-4.5F, -2.0F, -0.1F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(10, 158).addBox(-5.5F, -1.5F, -1.0F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -4.0F, 0.0F));

		PartDefinition telephone_t = sign_pto_t.addOrReplaceChild("telephone_t", CubeListBuilder.create().texOffs(61, 68).addBox(1.0F, -2.5F, -1.0F, 5.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.5F, -0.5F, 1.0F));

		PartDefinition bell1_t = telephone_t.addOrReplaceChild("bell1_t", CubeListBuilder.create(), PartPose.offset(4.75F, -1.0F, 0.5F));

		PartDefinition cube_r10 = bell1_t.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(74, 74).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition bell2_t = telephone_t.addOrReplaceChild("bell2_t", CubeListBuilder.create(), PartPose.offset(4.75F, 2.0F, 0.5F));

		PartDefinition cube_r11 = bell2_t.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(74, 74).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition receiver_t = telephone_t.addOrReplaceChild("receiver_t", CubeListBuilder.create().texOffs(74, 68).addBox(-1.0F, -2.5F, -0.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.25F, 0.5F, 0.5F));

		PartDefinition cable_t = telephone_t.addOrReplaceChild("cable_t", CubeListBuilder.create().texOffs(87, 41).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 3.0F, 0.0F, 0.0F, 0.3927F, 0.0F));

		PartDefinition door_r = doors.addOrReplaceChild("door_r", CubeListBuilder.create().texOffs(81, 58).addBox(-8.0F, -16.5F, 0.0F, 8.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(73, 8).addBox(-6.5F, -15.5F, -0.25F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.025F))
				.texOffs(73, 1).addBox(-6.5F, -15.0F, -0.1F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(84, 6).addBox(-6.0F, -6.0F, -0.1F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(87, 10).addBox(-7.5F, -6.0F, -1.0F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(90, 10).addBox(-7.5F, -2.0F, -1.0F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.5F, 0.0F, -10.5F));

		PartDefinition wall_e = walls.addOrReplaceChild("wall_e", CubeListBuilder.create().texOffs(71, 93).addBox(-8.5F, -16.5F, -10.5F, 8.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(57, 116).addBox(-0.5F, -16.5F, -11.0F, 1.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(90, 93).addBox(0.5F, -16.5F, -10.5F, 8.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(73, 8).addBox(-7.0F, -15.5F, -10.75F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.025F))
				.texOffs(73, 1).addBox(-7.0F, -15.0F, -10.6F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(73, 8).addBox(2.0F, -15.5F, -10.75F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.025F))
				.texOffs(73, 1).addBox(2.0F, -15.0F, -10.6F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition wall_s = walls.addOrReplaceChild("wall_s", CubeListBuilder.create().texOffs(97, 0).addBox(-8.5F, -16.5F, -10.5F, 8.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(73, 1).addBox(-7.0F, -15.0F, -10.6F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(73, 8).addBox(-7.0F, -15.5F, -10.75F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.01F))
				.texOffs(62, 116).addBox(-0.5F, -16.5F, -11.0F, 1.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(100, 35).addBox(0.5F, -16.5F, -10.5F, 8.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(73, 8).addBox(2.0F, -15.5F, -10.75F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.01F))
				.texOffs(73, 1).addBox(2.0F, -15.0F, -10.6F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition wall_w = walls.addOrReplaceChild("wall_w", CubeListBuilder.create().texOffs(109, 70).addBox(-8.5F, -16.5F, -10.5F, 8.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 124).addBox(-0.5F, -16.5F, -11.0F, 1.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(109, 105).addBox(0.5F, -16.5F, -10.5F, 8.0F, 33.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(73, 8).addBox(-7.0F, -15.5F, -10.75F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.025F))
				.texOffs(73, 1).addBox(-7.0F, -15.0F, -10.6F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(73, 8).addBox(2.0F, -15.5F, -10.75F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.025F))
				.texOffs(73, 1).addBox(2.0F, -15.0F, -10.6F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float f, float g, float h, float i, float j) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
		base.render(poseStack, vertexConsumer, i, j, k);
	}
}