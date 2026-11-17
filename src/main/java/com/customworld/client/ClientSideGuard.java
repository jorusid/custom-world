package com.customworld.client;

import net.minecraft.client.Minecraft;

/**
 * 只在客户端环境被加载的检查器——单独放一个类，专用服务器上永远不会触发类加载。
 */
public final class ClientSideGuard {
	private ClientSideGuard() {
	}

	/** 当前是否应该由本机决定世界生成。 */
	public static boolean singleplayerWorld() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft == null) {
			return false;
		}
		if (minecraft.level == null) {
			// 主菜单、以及「创建世界」时的世界预生成阶段：此时还没有客户端世界，
			// 但地形确实由本机集成服务器生成，必须放行，否则世界创建时改不进去
			return true;
		}
		// 已经在世界里：只有本机托管地形（单人/局域网主机）才允许接管
		return minecraft.hasSingleplayerServer();
	}
}
