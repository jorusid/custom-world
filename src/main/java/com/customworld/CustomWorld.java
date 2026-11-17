package com.customworld;

import com.customworld.config.WorldGenConfig;
import net.fabricmc.api.ModInitializer;

/**
 * 双端入口（服务端侧）。
 * <p>
 * 专用服务器上世界生成本来就该由这台机器说了算，所以服务端始终允许接管；
 * 「进入多人世界就自动停用」这条只针对<b>客户端</b>，见 {@link WorldGenGuard}。
 */
public final class CustomWorld implements ModInitializer {
	@Override
	public void onInitialize() {
		WorldGenConfig config = WorldGenConfig.load();
		CustomWorldClient.LOGGER.info("[CustomWorld] 服务端已加载：主世界={} 下界={} 末地={}",
				config.overworld.enabled, config.nether.enabled, config.end.enabled);
	}
}
