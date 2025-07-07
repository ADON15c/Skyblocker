package de.hysky.skyblocker.skyblock.fancybars;

import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.skyblock.StatusBarTracker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaManaBar {
	private static final Logger LOGGER = LoggerFactory.getLogger(VanillaManaBar.class);

	private int lastManaValue;
	private int manaEndBlinkTick;

	private static final Identifier MANA_EMPTY_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/mana_empty");
	private static final Identifier MANA_HALF_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/mana_half");
	private static final Identifier MANA_FULL_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/mana_full");
	private static final Identifier OVERFLOW_MANA_HALF_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/overflow_mana_half");
	private static final Identifier OVERFLOW_MANA_FULL_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/overflow_mana_full");
	private static final Identifier MANA_EMPTY_BLINKING_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/mana_empty_blinking");
	private static final Identifier MANA_HALF_BLINKING_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/mana_half_blinking");
	private static final Identifier MANA_FULL_BLINKING_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/mana_full_blinking");
	private static final Identifier OVERFLOW_MANA_HALF_BLINKING_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/overflow_mana_half_blinking");
	private static final Identifier OVERFLOW_MANA_FULL_BLINKING_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/overflow_mana_full_blinking");

	public boolean render(DrawContext context, int top, int right, int ticks) {
		if (!SkyblockerConfigManager.get().uiAndVisuals.bars.enableVanillaManaBar) return false;

		StatusBarTracker.Resource mana = StatusBarTracker.getMana();

		if (lastManaValue > mana.value()+mana.overflow()) {
			manaEndBlinkTick = ticks+20;
		}
		boolean blinking = manaEndBlinkTick > ticks && (manaEndBlinkTick - ticks) / 3 % 2 == 1;

		lastManaValue = mana.value() + mana.overflow();

		int manaAmount = mana.value() * 40 / mana.max();
		int manaMaxAmount = 40;
		int overflowAmount = mana.overflow() * 40 / mana.max();

		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 10; j++) {
				int right_offset = right - j * 8 - 9;
				int notch_pos = i*10 + j;

				if (i < 2 || notch_pos * 2 + 1 <= manaMaxAmount+overflowAmount) {
					context.drawGuiTexture(RenderLayer::getGuiTextured, blinking ? MANA_EMPTY_BLINKING_TEXTURE : MANA_EMPTY_TEXTURE, right_offset, top-i*10, 9, 9);
				}

				if (notch_pos * 2 + 1 < overflowAmount) {
					context.drawGuiTexture(RenderLayer::getGuiTextured, blinking ? OVERFLOW_MANA_FULL_BLINKING_TEXTURE : OVERFLOW_MANA_FULL_TEXTURE, right_offset, top-i*10, 9, 9);
				} else if (notch_pos * 2 + 1 == overflowAmount) {
					if (manaAmount > 0) {
						context.drawGuiTexture(RenderLayer::getGuiTextured, blinking ? MANA_FULL_BLINKING_TEXTURE : MANA_FULL_TEXTURE, right_offset, top-i*10, 9, 9);
					}
					context.drawGuiTexture(RenderLayer::getGuiTextured, blinking ? OVERFLOW_MANA_HALF_BLINKING_TEXTURE : OVERFLOW_MANA_HALF_TEXTURE, right_offset, top-i*10, 9, 9);
				} else if (notch_pos * 2 + 1 < manaAmount+overflowAmount) {
					context.drawGuiTexture(RenderLayer::getGuiTextured, blinking ? MANA_FULL_BLINKING_TEXTURE : MANA_FULL_TEXTURE, right_offset, top-i*10, 9, 9);
				} else if (notch_pos * 2 + 1 == manaAmount+overflowAmount) {
					context.drawGuiTexture(RenderLayer::getGuiTextured, blinking ? MANA_HALF_BLINKING_TEXTURE : MANA_HALF_TEXTURE, right_offset, top-i*10, 9, 9);
				}

			}
		}

		return true;
	}
}
