package com.dmcloot.Render;

import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Registry.ModifierRegistry;
import com.dmcloot.Util.ModifierHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GlintRenderTypes extends RenderType {

	//Just add modifier here and create glint texture in resources, mod does everything else.
	enum RENDERTYPES {
		ARCANESHIELDING(ModifierRegistry.MODIFIERS.ARCANESHIELDING.get(), true),
		FIRE(ModifierRegistry.MODIFIERS.FIRE.get(), false),
		FROST(ModifierRegistry.MODIFIERS.FROST.get(), false),
		LIGHTNING(ModifierRegistry.MODIFIERS.LIGHTNING.get(), false),
		LIFESTEAL(ModifierRegistry.MODIFIERS.LIFESTEAL.get(), false);

		private final RenderType rendertype;
		private final RenderType rendertypeEntity;
		private RenderType rendertypeArmor = null;
		private RenderType rendertypeArmorEntity = null;
		private final ModifierBase modifier;
		private final List<RenderType> mixinBuffers = new ArrayList<>();

		RENDERTYPES(ModifierBase modifierBase, boolean createArmor) {
			ResourceLocation texture = new ResourceLocation(DMCLoot.MODID, "textures/effect/" + modifierBase.getModifierName().split("\\.")[1] + "_glint.png");
			modifier = modifierBase;
			mixinBuffers.add(rendertype = createCustomGlint(modifierBase.getModifierName() + "_glint", texture, false));
			mixinBuffers.add(rendertypeEntity = createCustomGlint(modifierBase.getModifierName() + "_glint_entity", texture, true));
			if (createArmor) {
				mixinBuffers.add(rendertypeArmor = createCustomArmorGlint(modifierBase.getModifierName() + "_glint_armor", texture, false));
				mixinBuffers.add(rendertypeArmorEntity = createCustomArmorGlint(modifierBase.getModifierName() + "_glint_armor_entity", texture, true));
			}
		}

		public List<RenderType> getMixinBuffers() {
			return mixinBuffers;
		}

		public RenderType getRendertype(boolean isEntity, boolean isArmor) {
			return isArmor ? (isEntity ? rendertypeArmorEntity : rendertypeArmor) : (isEntity ? rendertypeEntity : rendertype);
		}

		public ModifierBase getModifier() {
			return modifier;
		}
	}

	public static void mixinFixedBuffers(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map) {
		for (RENDERTYPES type : RENDERTYPES.values()) {
			for (RenderType glintType : type.getMixinBuffers()) {
				if (!map.containsKey(glintType)) {
					map.put(glintType, new BufferBuilder(glintType.bufferSize()));
				}
			}
		}
	}

	public GlintRenderTypes(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
		super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
	}

	public static RenderType getRenderTypeFromModifier(ItemStack item, boolean isEntity, boolean isArmor) {
		for (ModifierRegistry.MODIFIERS modifier : ModifierHelper.getAllModifiers(item)) {
			for (RENDERTYPES type : RENDERTYPES.values()) {
				if (modifier.get().equals(type.getModifier())) {
					return type.getRendertype(isEntity, isArmor);
				}
			}
		}
		return null;
	}

	private static RenderType createCustomGlint(String Id, ResourceLocation rl, boolean forEntity) {
		State state = State.builder().setTextureState(new TextureState(rl, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(forEntity ? ENTITY_GLINT_TEXTURING : GLINT_TEXTURING).createCompositeState(false);
		return RenderType.create(Id, DefaultVertexFormats.POSITION_TEX, 7, 256, state);
	}

	private static RenderType createCustomArmorGlint(String Id, ResourceLocation rl, boolean forEntity) {
		State state = State.builder().setTextureState(new TextureState(rl, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(forEntity ? ENTITY_GLINT_TEXTURING : GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false);
		return RenderType.create(Id, DefaultVertexFormats.POSITION_TEX, 7, 256, state);
	}

}
