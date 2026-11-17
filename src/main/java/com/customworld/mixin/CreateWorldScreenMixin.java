package com.customworld.mixin;

import com.customworld.CustomWorldClient;
import com.customworld.ui.TerrainSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

/**
 * 在「创建世界」界面下方加一个「地形设置」入口。
 * <p>
 * 用界面左下角固定位置，不挤原版按钮；原版改动布局也不会把它顶掉。
 */
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
	@Inject(method = "init", at = @At("TAIL"))
	private void customworld$terrainButton(CallbackInfo ci) {
		Screen self = (Screen) (Object) this;
		Button entry = Button.builder(Component.translatable("customworld.terrain.entry"), b -> {
			Minecraft mc = Minecraft.getInstance();
			mc.gui.setScreen(new TerrainSettingsScreen(mc.gui.screen()));
		}).bounds(8, self.height - 26, 110, 20).build();

		try {
			Method adder = Screen.class.getDeclaredMethod("addRenderableWidget", GuiEventListener.class);
			adder.setAccessible(true);
			adder.invoke(self, entry);
		} catch (Throwable t) {
			CustomWorldClient.LOGGER.warn("[CustomWorld] 地形设置按钮注册失败", t);
		}
	}
}
