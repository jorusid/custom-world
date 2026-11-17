package com.customworld;

import com.customworld.config.WorldGenConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端入口：只负责加载配置、打日志。
 * 真正「能不能接管」的判断在 {@link WorldGenGuard} 里，按场景实时求值。
 */
public final class CustomWorldClient implements ClientModInitializer {
	public static final String MOD_ID = "customworld";
	public static final Logger LOGGER = LoggerFactory.getLogger("CustomWorld");

	@Override
	public void onInitializeClient() {
		WorldGenConfig config = WorldGenConfig.load();
		LOGGER.info("[CustomWorld] 客户端已加载：主世界={} 下界={} 末地={}",
				config.overworld.enabled, config.nether.enabled, config.end.enabled);
	}
}
