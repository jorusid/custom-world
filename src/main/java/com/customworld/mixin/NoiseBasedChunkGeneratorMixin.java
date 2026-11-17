package com.customworld.mixin;

import com.customworld.worldgen.WorldGenApplier;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 世界生成设置进场的唯一关口：把 {@link NoiseGeneratorSettings} 换成按配置调整过的版本。
 * 新建世界、加载已有单人存档都会走这个构造器，所以改动对所有单人世界统一生效。
 */
@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
	/**
	 * 注意：目标是<b>非静态构造器</b>，因此处理器必须是实例方法（写成 static 会在运行时注入失败，
	 * 而 mixins.json 是 {@code required: true}，那会把游戏直接拦在启动阶段）。
	 */
	@ModifyArg(
			method = "<init>",
			at = @At("HEAD"),
			index = 1)
	private Holder<NoiseGeneratorSettings> customworld_adjustSettings(
			Holder<NoiseGeneratorSettings> settings) {
		return WorldGenApplier.adjust(settings);
	}
}
