package com.customworld.config;

import com.customworld.CustomWorldClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 世界生成参数配置：三个维度各一份，互不影响。
 * <p>
 * 数值约定：{@code -9999} / {@code -1} 表示「跟随原版」，只有玩家真正填过的项才会改动世界生成，
 * 这样默认状态下模组完全不改变原版地形。
 */
public final class WorldGenConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final int VANILLA_INT = -9999;
	private static WorldGenConfig instance;

	public Dimension overworld = new Dimension();
	public Dimension nether = new Dimension();
	public Dimension end = new Dimension();

	public static WorldGenConfig get() {
		if (instance == null) {
			instance = load();
		}
		return instance;
	}

	public static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve(CustomWorldClient.MOD_ID + ".json");
	}

	public static WorldGenConfig load() {
		Path file = file();
		if (!Files.exists(file)) {
			WorldGenConfig fresh = new WorldGenConfig();
			fresh.save();
			return fresh;
		}
		try {
			String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
			WorldGenConfig loaded = GSON.fromJson(text, WorldGenConfig.class);
			if (loaded == null) {
				throw new IOException("配置解析为空");
			}
			loaded.fillDefaults();
			return loaded;
		} catch (Throwable t) {
			CustomWorldClient.LOGGER.warn("[CustomWorld] 配置读取失败，改用默认值", t);
			quarantine(file);
			return new WorldGenConfig();
		}
	}

	private void fillDefaults() {
		if (overworld == null) overworld = new Dimension();
		if (nether == null) nether = new Dimension();
		if (end == null) end = new Dimension();
	}

	private static void quarantine(Path path) {
		try {
			Path target = path.resolveSibling(path.getFileName() + ".corrupt-" + System.currentTimeMillis());
			Files.move(path, target, StandardCopyOption.REPLACE_EXISTING);
			CustomWorldClient.LOGGER.warn("[CustomWorld] 损坏的配置已保留为 {}", target.getFileName());
		} catch (IOException ignored) {
		}
	}

	public void save() {
		Path file = file();
		Path temp = file.resolveSibling(file.getFileName() + ".tmp");
		try {
			Files.createDirectories(file.getParent());
			byte[] bytes = GSON.toJson(this).getBytes(StandardCharsets.UTF_8);
			Files.write(temp, bytes);
			try {
				Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (IOException atomicFailed) {
				Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException t) {
			CustomWorldClient.LOGGER.warn("[CustomWorld] 配置写入失败", t);
			try {
				Files.deleteIfExists(temp);
			} catch (IOException ignored) {
			}
		}
	}

	/** 按噪声设置所属的维度取对应参数组。 */
	public Dimension forSettingsKey(ResourceKey<NoiseGeneratorSettings> key) {
		String path = key.identifier().getPath();
		if (path.contains("nether")) {
			return nether;
		}
		if (path.contains("end")) {
			return end;
		}
		if (path.contains("overworld") || path.contains("large_biomes") || path.contains("amplified")) {
			return overworld;
		}
		return null;
	}

	/** 单个维度的参数组。 */
	public static final class Dimension {
		/** 是否接管该维度。 */
		public boolean enabled = false;

		/** 海平面高度；-9999 跟随原版。 */
		public int seaLevel = VANILLA_INT;

		/** 噪声水平网格尺寸（越大越粗糙）；-1 跟随原版。 */
		public int noiseHorizontal = -1;

		/** 噪声垂直网格尺寸；-1 跟随原版。 */
		public int noiseVertical = -1;

		/** 是否禁用怪物生成；false 跟随原版。 */
		public boolean disableMobGeneration = false;

		/** 是否关闭含水层（洞穴里不会自然灌水/岩浆）；false 跟随原版。 */
		public boolean disableAquifers = false;

		/** 是否关闭矿脉；false 跟随原版。 */
		public boolean disableOreVeins = false;

		/** 是否使用旧版随机源；false 跟随原版。 */
		public boolean useLegacyRandom = false;

		public boolean seaLevelSet() {
			return seaLevel != VANILLA_INT;
		}
	}
}
