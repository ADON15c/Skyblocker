package de.hysky.skyblocker.skyblock.fancybars;

import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.skyblock.StatusBarTracker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaManaBar {
	private static final Logger LOGGER = LoggerFactory.getLogger(VanillaManaBar.class);
	private static final Identifier MANA_EMPTY_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/mana_empty");
	private static final Identifier MANA_HALF_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/mana_half");
	private static final Identifier MANA_FULL_TEXTURE = Identifier.of(SkyblockerMod.NAMESPACE, "bars/vanilla_mana/mana_full");

	public boolean render(DrawContext context, int top, int right) {
		StatusBarTracker.Resource mana = StatusBarTracker.getMana();

		int manaBarValue = mana.value() * 40 / mana.max();


		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 10; j++) {
				int right_offset = right - j * 8 - 9;
				int notch_pos = i*10 + j;

				context.drawGuiTexture(RenderLayer::getGuiTextured, MANA_EMPTY_TEXTURE, right_offset, top-i*10, 9, 9);
				if (notch_pos * 2 + 1 < manaBarValue) {
					context.drawGuiTexture(RenderLayer::getGuiTextured, MANA_FULL_TEXTURE, right_offset, top-i*10, 9, 9);
				}
				if (notch_pos * 2 + 1 == manaBarValue) {
					context.drawGuiTexture(RenderLayer::getGuiTextured, MANA_HALF_TEXTURE, right_offset, top-i*10, 9, 9);
				}
			}
		}

		return true;
	}
}
