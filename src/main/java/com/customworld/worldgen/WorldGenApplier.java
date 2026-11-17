package com.customworld.worldgen;

import com.customworld.WorldGenGuard;
import com.customworld.config.WorldGenConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;

/**
 * 把配置文件里的数值套到世界生成设置上。
 * <p>
 * 只在 {@link WorldGenGuard#active()} 成立时动手；任何一项没被玩家改过就原样返回，
 * 保证「默认状态 = 原版地形」。
 */
public final class WorldGenApplier {
	private WorldGenApplier() {
	}

	public static Holder<NoiseGeneratorSettings> adjust(Holder<NoiseGeneratorSettings> original) {
		if (original == null || !WorldGenGuard.active()) {
			return original;
		}
		ResourceKey<NoiseGeneratorSettings> key = original.unwrapKey().orElse(null);
		if (key == null) {
			return original;
		}
		WorldGenConfig.Dimension dimension = WorldGenConfig.get().forSettingsKey(key);
		if (dimension == null || !dimension.enabled) {
			return original;
		}
		try {
			NoiseGeneratorSettings base = original.value();
			NoiseSettings noise = base.noiseSettings();
			boolean changed = false;

			if (dimension.noiseHorizontal > 0 && dimension.noiseHorizontal != noise.noiseSizeHorizontal()) {
				noise = new NoiseSettings(noise.minY(), noise.height(), dimension.noiseHorizontal,
						noise.noiseSizeVertical());
				changed = true;
			}
			if (dimension.noiseVertical > 0 && dimension.noiseVertical != noise.noiseSizeVertical()) {
				noise = new NoiseSettings(noise.minY(), noise.height(), noise.noiseSizeHorizontal(),
						dimension.noiseVertical);
				changed = true;
			}

			int seaLevel = dimension.seaLevelSet() ? dimension.seaLevel : base.seaLevel();
			changed |= seaLevel != base.seaLevel();

			boolean mobs = dimension.disableMobGeneration || base.disableMobGeneration();
			changed |= mobs != base.disableMobGeneration();

			boolean aquifers = base.aquifersEnabled() && !dimension.disableAquifers;
			changed |= aquifers != base.aquifersEnabled();

			boolean oreVeins = base.oreVeinsEnabled() && !dimension.disableOreVeins;
			changed |= oreVeins != base.oreVeinsEnabled();

			boolean legacy = base.useLegacyRandomSource() || dimension.useLegacyRandom;
			changed |= legacy != base.useLegacyRandomSource();

			if (!changed) {
				return original;
			}

			NoiseGeneratorSettings adjusted = new NoiseGeneratorSettings(noise, base.defaultBlock(), base.defaultFluid(),
					base.noiseRouter(), base.surfaceRule(), base.spawnTarget(), seaLevel, mobs, aquifers, oreVeins, legacy);
			return Holder.direct(adjusted);
		} catch (Throwable t) {
			return original;
		}
	}
}
