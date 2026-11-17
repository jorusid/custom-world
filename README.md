# Custom World · 自定义世界生成参数

《我的世界》**26.2** / **Fabric** 的世界生成参数调整模组：主世界、下界、末地各一套参数，
互不干扰，数值可以拉出原版范围。

> 纯参数面板路线：不改游戏规则，只把原版世界生成里那几个关键旋钮开放给人调。

## 功能

在**创建世界界面左下角**点「地形设置」，打开面板，按维度分别调整：

| 参数 | 含义 | 原版字段 |
|---|---|---|
| 海平面 | 水位与洞穴液面高度（不改变山体高度） | `NoiseGeneratorSettings.seaLevel` |
| 噪声水平网格 | 地形采样格子的水平尺寸，越大越粗糙 | `NoiseSettings.noiseSizeHorizontal` |
| 噪声垂直网格 | 地形采样格子的垂直尺寸 | `NoiseSettings.noiseSizeVertical` |
| 禁用怪物生成 | 该维度不再自然生成怪物 | `disableMobGeneration` |
| 关闭含水层 | 洞穴里不再自然灌水/岩浆 | `aquifersEnabled` |
| 关闭矿脉 | 不生成大型矿脉 | `oreVeinsEnabled` |

- 每项默认显示「**原版**」，表示该项不干预；只有改过的项才会生效
- 配置保存在 `config/customworld.json`，三份语言（简体中文 / 繁體中文 / English）

## 作用范围

| 场景 | 是否生效 |
|---|---|
| 单人游戏（本机集成服务器） | ✅ |
| 专用服务器 | ✅ |
| 连别人的服务器（多人世界） | ❌ 自动停用 |

世界生成本来由服务端决定，客户端在多人世界里改只会造成“自己看着变了、别人还是原版”。
所以模组会实时判断场景，进多人世界就自动不接管。

## 安装

1. Fabric Loader（26.2，需要 Java 25）
2. 把 `customworld-<版本>.jar` 放进 `.minecraft/mods/`

## 实现要点

- **单一注入点**：`NoiseBasedChunkGenerator` 只有一个构造器，所有实例都必然经过它，
  用 `@ModifyArg` 换掉传入的 `NoiseGeneratorSettings`，新建世界与加载老存档统一生效
- **改动可序列化**：`NoiseGeneratorSettings.CODEC` 用的是允许内联的 `RegistryFileCodec`
  （`allowInline = true`），直接 Holder 能正常写进存档，不会保存失败
- **配置抗损坏**：`.tmp` 原子替换；解析失败会留一份 `.corrupt-<时间戳>` 取证

## 已知边界

- 海平面只影响水位/含水层液面，`seaLevel` 不是地形高度旋钮
- 噪声网格原版只用 1/2/4，非整倍数（如 3、5）建议先小范围试
- 参数生效后会被写进 `level.dat`，关掉模组不会自动恢复原版地形

## 版本历史

| 版本 | 说明 |
|---|---|
| 0.1.0 | 首个版本：地形设置面板、三维度独立参数、多人世界自动停用 |
