package com.dmcloot.Render;

import com.dmcloot.DMCLoot;
import com.dmcloot.Modifier.ModifierBase;
import com.dmcloot.Registry.ModifierRegistry;
import com.dmcloot.Util.ModifierHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTypes extends RenderType {

	//Just add modifier here and create glint texture in resources, mod does everything else.
	enum RENDERTYPES {
		FIRE(ModifierRegistry.MODIFIERS.FIRE.get()),
		FROST(ModifierRegistry.MODIFIERS.FROST.get()),
		LIGHTNING(ModifierRegistry.MODIFIERS.LIGHTNING.get()),
		LIFESTEAL(ModifierRegistry.MODIFIERS.LIFESTEAL.get());

		private final RenderType rendertype;
		private final ModifierBase modifier;

		RENDERTYPES(ModifierBase modifierBase) {
			modifier = modifierBase;
			rendertype = createCustomGlint(modifierBase.getModifierName() + "_glint", new ResourceLocation(DMCLoot.MODID, "textures/effect/" + modifierBase.getModifierName().split("\\.")[1] + "_glint.png"));
		}

		public RenderType getRendertype() {
			return rendertype;
		}

		public ModifierBase getModifier() {
			return modifier;
		}
	}

	public static void mixinFixedBuffers(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map) {
		for (RENDERTYPES type : RENDERTYPES.values()) {
			if (!map.containsKey(type.getRendertype())) {
				map.put(type.getRendertype(), new BufferBuilder(type.getRendertype().bufferSize()));
			}
		}
	}

	public RenderTypes(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
		super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
	}

	public static RenderType getRenderTypeFromModifier(ItemStack item) {
		for (ModifierRegistry.MODIFIERS modifier : ModifierHelper.getAllModifiers(item)) {
			for (RENDERTYPES type : RENDERTYPES.values()) {
				if (modifier.get().equals(type.getModifier())) {
					return type.getRendertype();
				}
			}
		}
		return null;
	}

	private static RenderType createCustomGlint(String Id, ResourceLocation rl) {
		State state = State.builder().setTextureState(new TextureState(rl, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(RenderState.GLINT_TEXTURING).createCompositeState(false);
		return RenderType.create(Id, DefaultVertexFormats.POSITION_TEX, 7, 256, state);
	}

}
