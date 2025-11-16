<div align="center">

# 🚀 AkiAsync
> Leaves / Luminol 系列服务端独占的异步优化插件，皆在为了不改变核心特性下提高 Leaves / Luminol 的性能提升
> 注意本Fork注重修复与数据包/插件的兼容性

**🍃 Leaves / Luminol 服务器异步优化插件**

[![License](https://img.shields.io/badge/License-GPL--3.0-blue.svg)](LICENSE)
[![Leaves](https://img.shields.io/badge/Leaves-1.21.8+-green.svg)](https://github.com/LeavesMC/Leaves)
[![Luminol](https://img.shields.io/badge/Luminol-1.21.8+-purple.svg)](https://github.com/LuminolMC/Luminol)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![Version](https://img.shields.io/badge/version-3.2.2--Community-orange.svg)](https://github.com/XingZiNina/Aki-Async)

**🎉 Community Edition（社区版）**

[快速开始](#-快速开始) • [命令列表](#-命令列表) • [配置说明](#️-配置说明) • [常见问题](#-常见问题)

</div>

---

## ✨ 核心特性

### 📊 **性能表现**
- ✅ **TPS优化稳步提升** - 减轻对主线程的压力
- ✅ **多线程均衡** - CPU占用分散到多个核心/线程
- ✅ **降低延迟** - 同tick内完成，实体行为优化有效
- ✅ **零破坏** - 完全兼容Paper/Spigot配置

### 🎯 **支持生物（65种）**
- **专用优化**：村民、猪灵、劫掠者、唤魔者、烈焰人、守卫者、女巫
- **通用优化**：骷髅、僵尸、苦力怕、蜘蛛、末影人、铁傀儡、雪傀儡、猪、牛、羊、鸡等55种

### 🔧 **优化模块（36个）**
- ⚡ **异步AI优化**（11个）- 村民、猪灵、劫掠者、唤魔者、烈焰人、守卫者、女巫、通用模板
- 🏭 **方块实体优化**（6个）- 漏斗、熔炉、箱子、酿造台、信标
- 💣 **爆炸优化**（1个）- TNT 分三阶段异步进行（未经大量测试 实验性功能）
- 👨‍🌾 **村民繁殖优化**（2个）- 异步繁殖检查 + 年龄降频
- 🎯 **并行优化**（3个）- 实体追踪、实体Tick、生物生成
- 💡 **光照优化**（2个）- Starlight算法、天空光缓存
- 💥 **性能优化**（3个）- 推挤、查找缓存、碰撞检测
- 🗄️ **内存优化**（3个）- 对象池、谓词缓存、列表预分配，减轻GC回收压力
- 🔧 **其他优化**（5个）- 区块异步加载、Locate异步定位、AI降频、寻路预算、漏斗智能跳过
> 注意有些功能为实验性功能，请根据实际需要进行开启

---

## 📋 系统要求

| 组件 | 要求 |
|------|------|
| **服务端** | [Leaves](https://github.com/LeavesMC/Leaves) 或 [Luminol](https://github.com/LuminolMC/Luminol) 以及其分支 |
| **服务端版本** | 1.21.8+ |
| **JDK** | Java 21＋ |
| **内存** | 推荐6GB+ |
| **CPU** | 推荐4核心+(虽多线程优化，但依旧以单核性能为主)|

---

## 🚀 快速开始

### 1. 下载插件

从 [Releases](https://github.com/virgil698/Aki-Async/releases) 下载最新版本

### 2. 安装插件

```bash
# 将JAR文件放入plugins文件夹
cp Aki-Async-x.x.0.jar /path/to/server/plugins/
```

### 3. 启用Mixin支持

**修改启动脚本，添加以下参数：**

```bash
java -Dleavesclip.enable.mixin=true -Xms4G -Xmx4G -jar server.jar
```

⚠️ **重要**：必须添加 `-Dleavesclip.enable.mixin=true` 参数，否则插件无法工作

### 4. 启动服务器

首次启动会自动生成配置文件：`plugins/AkiAsync/config.yml`

---

## 📋 命令列表

<details>
<summary><b>💡 /aki-reload - 热重载配置</b></summary>

**功能**：无需重启服务器即可重新加载配置并应用更改(实验性功能 存在不稳定性)

**权限**：`akiasync.reload`（默认仅OP可用）

**使用方法**：
```bash
/aki-reload
```

**效果**：
- ✅ 重新加载 `config.yml` 配置
- ✅ 平滑重启与初始化所有线程池（不丢失任务）
- ✅ 清除所有内部缓存
- ✅ 重置性能统计计数器
- ✅ 更新所有模块配置引用

**示例**：
```yaml
# 修改 config.yml
entity-tick-parallel:
  threads: 8  # 从 4 改为 8

# 执行命令
/aki-reload
# 输出：§a[AkiAsync] Configuration hot-reloaded, thread pools smoothly restarted.
```

</details>

<details>
<summary><b>🔍 /aki-debug <true|false> - 调试模式</b></summary>

**功能**：动态开启/关闭调试日志输出

**权限**：`akiasync.debug`（默认仅OP可用）

**使用方法**：
```bash
/aki-debug true   # 开启调试模式
/aki-debug false  # 关闭调试模式
```

**别名**：支持 `on/off`、`enable/disable`

**效果**：
- ✅ 实时切换调试日志开关
- ✅ 自动保存到配置文件
- ✅ 触发配置重载以应用更改
- ✅ 控制性能指标输出（每60秒）

**使用场景**：
- 🔍 性能调试时开启，查看详细指标
- 🧹 日常使用时关闭，避免控制台刷屏
- 📊 需要监控线程池状态时开启
- 🔥 推荐搭配Spark插件使用

**示例**：
```bash
# 开启调试模式
/aki-debug true
# 输出：§a[AkiAsync] Debug logging enabled successfully!
# 输出：§a[AkiAsync] Configuration reloaded to apply debug changes.

# 关闭调试模式  
/aki-debug false
# 输出：§a[AkiAsync] Debug logging disabled successfully!
```

</details>

<details>
<summary><b>📋 /aki-version - 版本信息</b></summary>

**功能**：显示插件详细信息和系统环境

**权限**：`akiasync.version`（默认仅OP可用）

**使用方法**：
```bash
/aki-version   # 显示版本信息
```

**显示内容**：
- ✅ 插件名称、版本号、作者
- ✅ 服务器类型和版本（Leaves/Paper/Spigot）
- ✅ Minecraft 版本
- ✅ Java 版本
- ✅ 操作系统信息
- ✅ 当前启用的优化功能状态

**使用场景**：
- 🐛 反馈问题时提供版本信息
- 📊 检查当前启用的优化功能
- 🔍 确认服务器环境配置
- 📝 生成技术支持报告

**示例**：
```bash
# 查看版本信息
/aki-version

# 输出示例：
# [AkiAsync] ========================================
# [AkiAsync] Plugin: AkiAsync
# [AkiAsync] Version: 11.45.14-SNAPSHOT
# [AkiAsync] Authors: Virgil
# [AkiAsync] 
# [AkiAsync] Server: Leaves 1.21.8
# [AkiAsync] Minecraft: 1.21.8
# [AkiAsync] Java: 21.0.5
# [AkiAsync] OS: Windows Server 2019 10.0
# [AkiAsync] 
# [AkiAsync] Active Optimizations:
# [AkiAsync]   Entity Tracker: ON
# [AkiAsync]   Mob Spawning: ON
# [AkiAsync]   Entity Tick Parallel: ON
# [AkiAsync]   Async Lighting: ON
# [AkiAsync]   Chunk Tick Async: ON
# [AkiAsync]   Brain Throttle: ON
# [AkiAsync] ========================================
```

**提示**：
- 💡 反馈 Bug 时建议先执行此命令，并附上输出截图
- 💡 可用于快速检查配置是否生效

</details>

## 🔧 常见问题

<details>
<summary><b>Q: 插件无法加载？</b></summary>

检查启动参数是否包含 `-Dleavesclip.enable.mixin=true`

</details>

<details>
<summary><b>Q: MSPT没有明显改善？</b></summary>

1. 确认实体数量 > 50（少于50不会启用并行优化）
2. 逐步开启异步AI优化（从村民开始）
3. 使用Spark查看详细性能

</details>

<details>
<summary><b>Q: 如何验证优化效果？</b></summary>

使用 [Spark](https://spark.lucko.me/) 性能分析工具：
```
/spark profiler start --timeout 5m
# 等待5分钟
/spark profiler stop
```

观察MSPT变化和CompletableFuture占比

如果出现一些性能问题请提交Issue并提交您的Spark报告链接或文件以供排查问题

</details>

<details>
<summary><b>Q: 插件兼容性？</b></summary>

- ✅ 与大部分插件兼容
- 📧 遇到冲突请提交Issue

</details>

<details>
<summary><b>Q: 推荐的开启顺序？</b></summary>

建议逐步开启，观察效果：
1. 先开启 `entity-tick-parallel`（并行实体Tick）
2. 再开启 `villager-optimization`（村民优化）
3. 最后开启 `universal-ai-optimization`（通用优化）

每次开启后运行一段时间，确认无问题再开启下一个。

</details>

---

## 🛠️ 开发构建

```bash
# 克隆项目
git clone https://github.com/virgil698/Aki-Async.git
cd Aki-Async

# 构建
./gradlew clean build

# 产物位于
build/libs/Aki-Async-x.x.x-SNAPSHOT.jar
```

---

## 📄 开源协议

本项目采用 [GPL-3.0 License](LICENSE) 开源协议

### 🆓 AkiAsync Community Edition（社区版）

这是**坚持开源精神的社区版本**：
- ✅ 完全免费使用
- ✅ 允许商业用途
- ✅ 源代码开放
- ✅ 24个优化模块完整功能
- ✅ 65种生物全覆盖
- ✅ 长期维护更新

根据GPL-3.0协议：
- ✅ 可自由使用、修改、分发
- ✅ 可用于商业服务器
- ⚠️ 修改后的版本必须同样开源（GPL-3.0）
- ⚠️ 必须保留原作者版权声明

---

## 🤝 参考与致谢

本项目参考了以下优秀开源项目：

| 项目 | 贡献 |
|------|------|
| [Starlight](https://github.com/PaperMC/Starlight) / [ScalableLux](https://github.com/RelativityMC/ScalableLux) | 光照传播算法 |
| [Lithium](https://github.com/CaffeineMC/lithium) | 性能优化思路 |
| [ServerCore](https://github.com/Wesley1808/ServerCore) | 热点优化方案 |
| [FerriteCore](https://github.com/malte0811/FerriteCore) | 内存优化策略 |
| [Async](https://github.com/AxalotLDev/Async) | 异步化边界设计 |
| [Horizon](https://github.com/LeavesMC/Leaves) | 数据包优化（暂未实现） |
| [Leaves](https://github.com/LeavesMC/Leaves) | Mixin支持 |

---

## ⚠️ 免责声明

- 本插件通过Mixin修改服务端核心代码，**请先在测试服务器验证**
- 不保证与所有插件100%兼容
- 如遇问题请提交 [Issue](https://github.com/virgil698/Aki-Async/issues)

---

<div align="center">

### 📮 联系方式

💬 **Issues**: [GitHub Issues](https://github.com/virgil698/Aki-Async/issues)  
📧 **Email**: virgil698@sky233.top  
💬 **Discord**: xiaokong23357(virgil698)

---

**Edition**: Community（社区版） | **Version**: 2.0.0 | **License**: GPL-3.0 | **Author**: [Virgil](https://github.com/virgil698)

**如果觉得有用，请给个 ⭐ Star 支持开发！**

Made with ❤️ for Minecraft Server Optimization

</div>
