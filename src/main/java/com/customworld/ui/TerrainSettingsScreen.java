package com.customworld.ui;

import com.customworld.config.WorldGenConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 世界创建界面下方那个「地形设置」按钮打开的页面。
 * <p>
 * 三个维度各一套数值，可随时切换维度编辑；所有数值都允许超出原版范围，
 * 「跟随原版」状态用「原版」字样明确显示，避免玩家不知道自己改没改。
 */
public class TerrainSettingsScreen extends Screen {
	private static final int ROW_GAP = 26;
	/** 第一行数值控件的 y：顶栏 46 + 接管开关占 40。 */
	private static final int FIRST_ROW = 86 + ROW_GAP;
	/** 数值显示位置：放在加减按钮右侧，避免压住「−」按钮。 */
	private static final int VALUE_X = 196;

	private final Screen parent;
	private WorldGenConfig config;
	private WorldGenConfig.Dimension dimension;
	private int dimensionIndex = 0;

	public TerrainSettingsScreen(Screen parent) {
		super(Component.translatable("customworld.terrain.title"));
		this.parent = parent;
		this.config = WorldGenConfig.get();
		this.dimension = this.config.overworld;
	}

	@Override
	protected void init() {
		int left = 20;
		int top = 46;

		String[] dimensionKeys = {"overworld", "nether", "end"};
		for (int i = 0; i < dimensionKeys.length; i++) {
			final int index = i;
			this.addRenderableWidget(Button.builder(dimensionLabel(i), b -> {
				dimensionIndex = index;
				dimension = selected();
				refresh();
			}).bounds(left + i * 110, top, 100, 20).build());
		}

		int row = top + 40;
		this.addRenderableWidget(Button.builder(toggleLabel("enabled", dimension.enabled), b -> {
			dimension.enabled = !dimension.enabled;
			b.setMessage(toggleLabel("enabled", dimension.enabled));
			save();
		}).bounds(left, row, 220, 20).build());

		row += ROW_GAP;

		this.addNumberRow(left, row, "seaLevel", -4000, 4000, 16);
		this.addNumberRow(left, row + ROW_GAP, "noiseH", 1, 64, 1);
		this.addNumberRow(left, row + ROW_GAP * 2, "noiseV", 1, 64, 1);

		int flagRow = row + ROW_GAP * 3 + 6;
		this.addFlag(left, flagRow, "mobs", dimension.disableMobGeneration);
		this.addFlag(left, flagRow + ROW_GAP, "aquifers", dimension.disableAquifers);
		this.addFlag(left, flagRow + ROW_GAP * 2, "oreVeins", dimension.disableOreVeins);

		this.addRenderableWidget(Button.builder(Component.translatable("customworld.terrain.done"), b -> {
			save();
			this.minecraft.gui.setScreen(parent);
		}).bounds(this.width / 2 - 60, this.height - 30, 120, 20).build());
	}

	/** 切换维度后重建控件，让数值跟着换。 */
	private void refresh() {
		save();
		this.rebuildWidgets();
	}

	private WorldGenConfig.Dimension selected() {
		if (dimensionIndex == 1) {
			return config.nether;
		}
		if (dimensionIndex == 2) {
			return config.end;
		}
		return config.overworld;
	}

	private void addNumberRow(int x, int y, String key, int min, int max, int step) {
		this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
			step(key, -step, min, max, step);
		}).bounds(x + 150, y, 20, 20).build());
		this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
			step(key, step, min, max, step);
		}).bounds(x + 220, y, 20, 20).build());
	}

	private void step(String key, int delta, int min, int max, int stepSize) {
		int current;
		if (key.equals("seaLevel")) {
			current = dimension.seaLevelSet() ? dimension.seaLevel : 0;
		} else if (key.equals("noiseH")) {
			current = dimension.noiseHorizontal > 0 ? dimension.noiseHorizontal : 2;
		} else {
			current = dimension.noiseVertical > 0 ? dimension.noiseVertical : 2;
		}
		int next = Math.max(min, Math.min(max, current + delta));
		if (key.equals("seaLevel")) {
			dimension.seaLevel = next;
		} else if (key.equals("noiseH")) {
			dimension.noiseHorizontal = next;
		} else {
			dimension.noiseVertical = next;
		}
		save();
	}

	private void addFlag(int x, int y, String key, boolean value) {
		this.addRenderableWidget(Button.builder(flagLabel(key, value), b -> {
			boolean next;
			if (key.equals("mobs")) {
				dimension.disableMobGeneration = !dimension.disableMobGeneration;
				next = dimension.disableMobGeneration;
			} else if (key.equals("aquifers")) {
				dimension.disableAquifers = !dimension.disableAquifers;
				next = dimension.disableAquifers;
			} else {
				dimension.disableOreVeins = !dimension.disableOreVeins;
				next = dimension.disableOreVeins;
			}
			b.setMessage(flagLabel(key, next));
			save();
		}).bounds(x, y, 220, 20).build());
	}

	private void save() {
		config.save();
	}

	private Component dimensionLabel(int index) {
		String suffix = index == 0 ? "overworld" : index == 1 ? "nether" : "end";
		return Component.translatable("customworld.terrain.dimension." + suffix);
	}

	private Component toggleLabel(String key, boolean value) {
		return Component.translatable("customworld.terrain." + key + (value ? ".on" : ".off"));
	}

	private Component flagLabel(String key, boolean value) {
		return Component.translatable("customworld.terrain." + key + (value ? ".on" : ".off"));
	}

	private Component valueText(String key) {
		if (key.equals("seaLevel")) {
			return dimension.seaLevelSet()
					? Component.literal(String.valueOf(dimension.seaLevel))
					: Component.translatable("customworld.terrain.vanilla");
		}
		if (key.equals("noiseH")) {
			return dimension.noiseHorizontal > 0
					? Component.literal(String.valueOf(dimension.noiseHorizontal))
					: Component.translatable("customworld.terrain.vanilla");
		}
		return dimension.noiseVertical > 0
				? Component.literal(String.valueOf(dimension.noiseVertical))
				: Component.translatable("customworld.terrain.vanilla");
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);

		graphics.text(this.font, Component.translatable("customworld.terrain.heading"), 20, 16, 0xFFFFFF);

		// 与 init() 里的控件位置严格同源：顶栏 46 + 接管开关 40 + 行距 26
		int row = FIRST_ROW;
		graphics.text(this.font, Component.translatable("customworld.terrain.seaLevel"), 20, row + 6, 0xA0A0A0);
		graphics.text(this.font, Component.translatable("customworld.terrain.noiseH"), 20, row + ROW_GAP + 6, 0xA0A0A0);
		graphics.text(this.font, Component.translatable("customworld.terrain.noiseV"), 20, row + ROW_GAP * 2 + 6, 0xA0A0A0);

		graphics.text(this.font, valueText("seaLevel"), VALUE_X, row + 6, 0xFFFFFF);
		graphics.text(this.font, valueText("noiseH"), VALUE_X, row + ROW_GAP + 6, 0xFFFFFF);
		graphics.text(this.font, valueText("noiseV"), VALUE_X, row + ROW_GAP * 2 + 6, 0xFFFFFF);

		graphics.text(this.font, Component.translatable("customworld.terrain.hint"), 20, this.height - 56, 0xA0A0A0);
	}

	@Override
	public void onClose() {
		save();
		this.minecraft.gui.setScreen(parent);
	}
}
