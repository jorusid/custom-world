package com.customworld;

import com.customworld.client.ClientSideGuard;
import net.fabricmc.loader.api.FabricLoader;

/**
 * 「这个场景该不该由本模组接管世界生成」的统一判断，所有 mixin 都先问它。
 * <p>
 * 规则：
 * <ul>
 *     <li><b>专用服务器</b>：世界生成本来就由本机决定，始终接管；</li>
 *     <li><b>客户端 + 单人游戏</b>：世界生成跑在本机进程内的集成服务器，接管；</li>
 *     <li><b>客户端 + 多人世界</b>：地形由对方服务端下发，本机不接管，自动停用，
 *     避免本地看着有效、别人看着原版，以及不同步被踢。</li>
 * </ul>
 * 判断是<b>每次调用时实时求值</b>，不靠一次性的事件钩子，所以单人/多人来回切换也不会残留状态。
 */
public final class WorldGenGuard {
	private static volatile boolean lastState = true;

	private WorldGenGuard() {
	}

	/** 当前场景是否允许本模组作用。 */
	public static boolean active() {
		boolean current = compute();
		if (current != lastState) {
			lastState = current;
			if (current) {
				CustomWorldClient.LOGGER.info("[CustomWorld] 场景允许接管世界生成，已启用");
			} else {
				CustomWorldClient.LOGGER.info("[CustomWorld] 检测到多人世界，已自动停用世界生成改动");
			}
		}
		return current;
	}

	private static boolean compute() {
		if (FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.SERVER) {
			return true;
		}
		return ClientSideGuard.singleplayerWorld();
	}
}
