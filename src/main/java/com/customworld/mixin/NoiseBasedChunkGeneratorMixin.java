package com.customworld.mixin;

import com.customworld.worldgen.WorldGenApplier;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 世界生成设置进场的唯一关口：把 {@link NoiseGeneratorSettings} 换成按配置调整过的版本。
 * 新建世界、加载已有单人存档都会走这个构造器，所以改动对所有单人世界统一生效。
 * <p>
 * 为什么用 {@code @ModifyVariable} 而不是 {@code @ModifyArg}：
 * 该构造器的第一条指令是 {@code aload_0}（非方法调用），
 * {@code @ModifyArg} 必须挂在一个方法调用上，在 HEAD 处会报
 * {@code "@ModifyArg annotation on is targetting a non-method insn"}。
 * 改参数则不受这个限制，直接改局部变量槽即可。
 * <p>
 * 槽位：0 = this，1 = BiomeSource，2 = Holder&lt;NoiseGeneratorSettings&gt;。
 */
@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
	/**
	 * 注入点在<b>构造器的 super() 之前</b>，此时实例还没建立，Mixin 强制要求处理器是
	 * {@code static}；写成实例方法会抛
	 * {@code "handler before super() invocation must be static"}。
	 */
	@ModifyVariable(
			method = "<init>",
			at = @At("HEAD"),
			argsOnly = true,
			index = 2)
	private static Holder<NoiseGeneratorSettings> customworld_adjustSettings(
			Holder<NoiseGeneratorSettings> settings) {
		return WorldGenApplier.adjust(settings);
	}
}
