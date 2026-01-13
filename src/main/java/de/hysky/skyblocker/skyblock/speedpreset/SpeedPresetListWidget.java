package de.hysky.skyblocker.skyblock.speedpreset;

import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import org.jspecify.annotations.Nullable;

public class SpeedPresetListWidget extends ContainerObjectSelectionList<SpeedPresetListWidget.AbstractEntry> {

	private static final Pattern NUMBER = Pattern.compile("^-?\\d+(\\.\\d+)?$");
	// Alphanumeric sequence that doesn't start with a number.
	private static final Pattern TITLE = Pattern.compile("^[a-zA-Z]\\w*$");

	private final int TITLE_INPUT_WIDTH = 120;
	private final int NUMERIC_INPUT_WIDTH = 50;
	private final int REMOVE_BUTTON_WIDTH = 20;
	private final int GRID_GAP = 2;

	public SpeedPresetListWidget(int width, int height, int y) {
		super(Minecraft.getInstance(), width, height, y, 25);
		var instance = SpeedPresets.getInstance();
		addEntry(new TitleEntry());
		if (!instance.getPresets().isEmpty())
			instance.getPresets().forEach((title, speed) -> this.addEntry(new SpeedPresetEntry(title, String.valueOf(speed), "", "")));
		else this.addEntry(new SpeedPresetEntry("", "", "", ""));
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 104; // TODO: what? where does it come from i have no idea
	}

	public boolean hasBeenChanged() {
		var instance = SpeedPresets.getInstance();
		// If there are fewer children than presets, some were removed, and all further checks are pointless
		if (children().size() < instance.getPresets().size()) return true;
		var childrenMap = this.children().stream()
				.filter(SpeedPresetEntry.class::isInstance)
				.map(SpeedPresetEntry.class::cast)
				.map(SpeedPresetEntry::getMapping)
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(ObjectIntPair::key, ObjectIntPair::valueInt));
		return !instance.arePresetsEqual(childrenMap);
	}

	public void updatePosition() {
		children().forEach(AbstractEntry::updatePosition);
	}

	public void newEntry() {
		var entry = new SpeedPresetEntry("", "", "", "");
		this.addEntry(entry);
		this.centerScrollOn(entry);
		this.setSelected(entry);
		this.setFocused(entry);
	}

	public void save() {
		var instance = SpeedPresets.getInstance();
		instance.getPresets().clear();
		children().stream()
				.filter(SpeedPresetEntry.class::isInstance)
				.map(SpeedPresetEntry.class::cast)
				.forEach(SpeedPresetEntry::save);
		instance.savePresets(); // Write down the changes.
	}

	public abstract static class AbstractEntry extends ContainerObjectSelectionList.Entry<AbstractEntry> {

		protected void updatePosition() {}

		@Override
		public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			this.children().forEach(child -> {
				if (child instanceof LayoutElement widget)
					widget.setY(this.getY());
				if (child instanceof Renderable drawable)
					drawable.render(context, mouseX, mouseY, deltaTicks);
			});
		}
	}

	public class TitleEntry extends AbstractEntry {

		@Override
		public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
			int left = (width - (TITLE_INPUT_WIDTH + NUMERIC_INPUT_WIDTH*3 + REMOVE_BUTTON_WIDTH + GRID_GAP*4))/2;
			// The line height is 25, the height of a single character is always 9.
			// 25 - 9 = 16, 16 / 2 = 8, therefore the Y-offset should be 8.
			context.drawCenteredString(minecraft.font, Component.translatable("skyblocker.config.general.speedPresets.config.title"), left + TITLE_INPUT_WIDTH/2, this.getY() + 8, CommonColors.WHITE);
			context.drawCenteredString(minecraft.font, Component.translatable("skyblocker.config.general.speedPresets.config.speed"), left + TITLE_INPUT_WIDTH + NUMERIC_INPUT_WIDTH/2 + GRID_GAP, this.getY() + 8, CommonColors.WHITE);
			context.drawCenteredString(minecraft.font, Component.translatable("skyblocker.config.general.speedPresets.config.yaw"), left + TITLE_INPUT_WIDTH + NUMERIC_INPUT_WIDTH*3/2 + GRID_GAP, this.getY() + 8, CommonColors.WHITE);
			context.drawCenteredString(minecraft.font, Component.translatable("skyblocker.config.general.speedPresets.config.pitch"), left + TITLE_INPUT_WIDTH + NUMERIC_INPUT_WIDTH*5/2 + GRID_GAP, this.getY() + 8, CommonColors.WHITE);
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return List.of();
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return List.of();
		}
	}

	public class SpeedPresetEntry extends AbstractEntry {

		protected final EditBox titleInput;
		protected final EditBox speedInput;
		protected final EditBox yawInput;
		protected final EditBox pitchInput;
		protected final Button removeButton;

		public SpeedPresetEntry(String title, String speed, String yaw, String pitch) {
			var client = SpeedPresetListWidget.this.minecraft;

			// All Xs and Ys are then set using the initPosition() method.
			this.titleInput = new EditBox(client.font, 0, 0, TITLE_INPUT_WIDTH, 20, Component.empty());
			this.titleInput.setFilter(str -> str.isEmpty() || TITLE.matcher(str).matches());
			this.titleInput.setValue(title);
			this.titleInput.setMaxLength(16);
			this.titleInput.setHint(Component.literal("newPreset").withStyle(ChatFormatting.DARK_GRAY));

			this.speedInput = new EditBox(client.font, 0, 0, NUMERIC_INPUT_WIDTH, 20, Component.empty());
			this.speedInput.setFilter(str -> str.isEmpty() || NUMBER.matcher(str).matches());
			this.speedInput.setValue(speed);
			this.speedInput.setMaxLength(3);
			this.speedInput.setHint(Component.literal("0").withStyle(ChatFormatting.DARK_GRAY));

			this.yawInput = new EditBox(client.font, 0, 0, NUMERIC_INPUT_WIDTH, 20, Component.empty());
			this.yawInput.setFilter(str -> str.isEmpty() || NUMBER.matcher(str).matches());
			this.yawInput.setValue(speed);
			this.yawInput.setMaxLength(3);
			this.yawInput.setHint(Component.literal("0").withStyle(ChatFormatting.DARK_GRAY));

			this.pitchInput = new EditBox(client.font, 0, 0, NUMERIC_INPUT_WIDTH, 20, Component.empty());
			this.pitchInput.setFilter(str -> str.isEmpty() || NUMBER.matcher(str).matches());
			this.pitchInput.setValue(speed);
			this.pitchInput.setMaxLength(3);
			this.pitchInput.setHint(Component.literal("0").withStyle(ChatFormatting.DARK_GRAY));

			this.removeButton = Button.builder(Component.literal("-"), btn -> SpeedPresetListWidget.this.removeEntry(this))
					.bounds(0, 0, REMOVE_BUTTON_WIDTH, 20)
					.build();

			this.updatePosition();
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return List.of(titleInput, speedInput, yawInput, pitchInput, removeButton);
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return List.of(titleInput, speedInput, yawInput, pitchInput, removeButton);
		}

		public void save() {
			var mapping = getMapping();
			if (mapping != null)
				SpeedPresets.getInstance().setPreset(mapping.key(), mapping.valueInt());
		}

		protected boolean isEmpty() {
			return titleInput.getValue().isEmpty() && speedInput.getValue().isEmpty() && yawInput.getValue().isEmpty()  && pitchInput.getValue().isEmpty();
		}

		@Override
		protected void updatePosition() {
			var grid = new GridLayout();
			grid.spacing(GRID_GAP);
			grid.addChild(titleInput, 0, 0, 1, 3);
			grid.addChild(speedInput, 0, 3, 1, 2);
			grid.addChild(yawInput, 0, 5, 1, 2);
			grid.addChild(pitchInput, 0, 7, 1, 2);
			grid.addChild(removeButton, 0, 9, 1, 1);
			grid.arrangeElements();
			FrameLayout.alignInRectangle(grid, 0, 0, width, defaultEntryHeight, 0.5f, 0.5f);
		}

		protected @Nullable ObjectIntPair<String> getMapping() {
			if (isEmpty()) return null;
			try {
				return ObjectIntPair.of(titleInput.getValue(), Integer.parseInt(speedInput.getValue()));
			} catch (NumberFormatException e) {
				return null;
			}
		}
	}
}
