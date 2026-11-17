本模组的版本变更记录，按版本倒序。安装包见 [Releases](../../releases)。

## 0.1.1

- **修复启动期 Mixin 注入失败**：`@ModifyArg` 的处理器改回 `static`
  - 报错原文：`@ModifyArg handler before super() invocation must be static`
  - 注入点在构造器的 `super()` 之前，此时实例尚未建立，Mixin 不允许实例方法处理器；
    而 `customworld.mixins.json` 是 `required: true`，注入失败会直接拦在启动阶段
- 版本号推进，功能与 0.1.0 一致

## 0.1.0

- 首个版本：在「创建世界」界面左下角加入「地形设置」入口
- 主世界 / 下界 / 末地三维度各自独立的参数组，互不影响
- 可调：海平面、噪声水平网格、噪声垂直网格、禁用怪物生成、关闭含水层、关闭矿脉
- 进入多人世界自动停用（世界生成由对方服务端决定，本地改无效）
- 配置写入 `config/customworld.json`，采用原子替换 + 损坏隔离
- 三语支持：简体中文 / 繁體中文 / English
