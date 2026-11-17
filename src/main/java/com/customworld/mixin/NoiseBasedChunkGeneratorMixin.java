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
	 * 注意：注入点在<b>构造器的 super() 之前</b>，此时实例还没建立，Mixin 强制要求处理器是
	 * {@code static}；写成实例方法会在运行时抛
	 * {@code InvalidInjectionException: @ModifyArg handler before super() invocation must be static}，
	 * 而 mixins.json 是 {@code required: true}，那会把游戏直接拦在启动阶段。
	 */
	@ModifyArg(
			method = "<init>",
			at = @At("HEAD"),
			index = 1)
	private static Holder<NoiseGeneratorSettings> customworld_adjustSettings(
			Holder<NoiseGeneratorSettings> settings) {
		return WorldGenApplier.adjust(settings);
	}
}
