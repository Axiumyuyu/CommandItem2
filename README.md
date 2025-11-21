# **CommandItem2 Plugin**

## **English**

*I am not a native English Speaker, sorry for possible mistakes, please refer to Chinese version using translator or check source code.*

### **Overview**

**CommandItem2** is a powerful and highly customizable Minecraft plugin for Purpur servers that allows administrators to create unique items with special abilities. These items can execute commands, have cooldowns, require specific permissions, and more, with all their properties stored directly on the item itself using Persistent Data Containers (PDC). This ensures that items retain their unique characteristics even when traded or stored.

### **Features**

* **Custom Item Creation**: Define items with custom names (using MiniMessage), lore, enchantments, and materials.  
* **Command Execution**: Assign a list of commands to be executed from the console when the item is used.  
* **Persistent Data**: All special properties are saved to the item's NBT data, making them permanent.  
* **Cooldown System**: Set a per-player, per-item cooldown to prevent spam.  
* **Permission Control**: Require players to have a specific permission node (commanditem.use.\<id\>) to use an item.  
* **Configurable Behavior**:  
  * **Strict Mode**: Choose whether to always pull item data from the config (for consistency) or from the item's data first (for legacy/modified items).  
  * **Consumable Items**: Decide if an item should be consumed upon use.  
* **Live Reload**: Reload the configuration and all item definitions without restarting the server.  
* **PlaceholderAPI Support**: Display item cooldowns and other information via placeholders.  
* **Dynamic Permissions**: Permissions are automatically registered and unregistered on reload based on the items in your config.

### **Dependencies**

* **Server**: [Paper](https://papermc.io) or its fork like [Purpur](https://purpurmc.org/) for Minecraft 1.21.3 or later.  
* **Required Plugin**: [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (for placeholder support).

### **Installation**

1. Download the latest version of the plugin from the releases page.  
2. Make sure you have Purpur 1.21+ and PlaceholderAPI installed on your server.  
3. Place the CommandItem2-vX.X.X.jar file into your server's /plugins directory.  
4. Start or restart your server. A CommandItem2 folder containing the config.yml file will be generated.  
5. Customize the config.yml to define your own special items.  
6. Use the /si reload command in-game or from the console to apply your changes.

### **Configuration (config.yml)**

Strict mode can be used for compatibility with older versions or special items modified by administrators. **If this mode is disabled, ensure that players do not have permission to modify the item's NBT data; otherwise, serious consequences may occur.**

### **Commands & Permissions**

| Command                   | Description                                | Permission        |
|:--------------------------|:-------------------------------------------|:------------------|
| /ci reload                | Reloads the plugin's configuration.        | commanditem.admin |
| /ci get \<id\> \[amount\] | Gives the player a specified special item. | commanditem.admin |
| /ci                       | Shows the help message.                    | commanditem.admin |

**Player Permissions:**

* commanditem.use.\<item\_id\>: Allows a player to use the special item with the corresponding ID. For example, commanditem.use.fire\_sword.  
* commanditem.use.\*: Allows a player to use all special items.

### **Placeholders (PlaceholderAPI)**

Use these placeholders in any other plugin that supports PlaceholderAPI.

| Placeholder                              | Description                                         | Example Output |
|:-----------------------------------------|:----------------------------------------------------|:---------------|
| %commanditem\_cooldown\_\<item\_id\>%    | Shows the formatted remaining cooldown for an item. | 1m 15s / Ready |
| %commanditem\_cooldown\_s\_\<item\_id\>% | Shows the remaining cooldown in seconds.            | 75 / 0         |
| %commanditem\_name\_\<item\_id\>%        | Shows the MiniMessage-formatted name of the item.   | Infernal Blade |

### **Building from Source**

If you want to compile the plugin yourself, follow these steps:

1. **Clone the repository:**  
   git clone \<repository\_url\>  
   cd CommandItem2

2. **Ensure** you **have JDK 17 or newer installed.**  
3. **Build the JAR using Gradle:**  
   * On Windows:  
     gradlew build

   * On Linux/macOS:  
     ./gradlew build

4. The compiled JAR file will be located in build/libs/.

## **中文版本**

### **项目简介**

**CommandItem2** 是一款功能强大、高度可定制的 Minecraft 插件，专为 Paper 服务端设计。它允许服主创建拥有特殊能力的独特物品。这些物品可以执行指令、拥有冷却时间、需要特定权限等。所有特殊属性都通过持久化数据容器（PDC）直接存储在物品本身，确保物品在交易或存放后仍能保持其独特性。

### **功能特色**

* **自定义物品创建**: 定义具有自定义名称（支持 MiniMessage 格式）、Lore、附魔和材质的物品。  
* **指令执行**: 为物品指定一组指令，在使用时由后台执行。  
* **数据持久化**: 所有特殊属性都保存在物品的 NBT 数据中，使其永久生效。  
* **冷却系统**: 为每个玩家和每种物品设置独立的冷却时间，防止滥用。  
* **权限控制**: 可要求玩家拥有特定的权限节点 (commanditem.use.\<id\>) 才能使用物品。  
* **可配置行为**:  
  * **严格模式**: 可选择始终从配置文件读取物品数据（保证一致性），或优先从物品自身读取（兼容旧版或被修改过的物品）。  
  * **消耗性物品**: 可设置物品在使用后是否被消耗。  
* **实时重载**: 无需重启服务器即可重载配置和所有物品定义。  
* **PlaceholderAPI 支持**: 通过变量显示物品的冷却时间和其他信息。  
* **动态权限**: 插件会根据配置文件中的物品，在重载时自动注册和注销对应的使用权限。

### **前置依赖**

* **服务端**:[Paper](https://papermc.io) 或其分支例如 [Purpur](https://purpurmc.org/) 的 Minecraft 1.21.3 或更高版本。  
* **必需插件**: [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (用于变量支持)。

### **安装方法**

1. 从发布页面下载最新版本的插件。  
2. 确保你的服务器已安装 Purpur 1.21+ 和 PlaceholderAPI。  
3. 将下载的 CommandItem2-vX.X.X.jar 文件放入服务器的 /plugins 文件夹中。  
4. 启动或重启服务器。此时会自动生成一个 CommandItem2 文件夹，其中包含 config.yml 配置文件。  
5. 编辑 config.yml 文件，定义你自己的特殊物品。  
6. 在游戏内或后台使用 /ci reload 指令来应用你的更改。

### **插件配置 (config.yml)**

strict 模式可以用于兼容旧版本或者由管理修改过的特殊物品，**如果关闭该模式，请确保玩家没有权限修改物品NBT数据，否则可能造成严重后果**

### **指令 & 权限**

| 指令                  | 描述             | 权限                |
|:--------------------|:---------------|:------------------|
| `/si reload`        | 重载插件的配置文件。     | commanditem.admin |
| `/si get <id> [数量]` | 给予玩家一个指定的特殊物品。 | commanditem.admin |
| `/si`               | 显示帮助信息。        | commanditem.admin |

**玩家权限:**

* `commanditem.use.<item_id>`: 允许玩家使用对应ID的特殊物品。例如 commanditem.use.fire\_sword。  
* `commanditem.use.*`: 允许玩家使用所有特殊物品。

### **变量 (PlaceholderAPI)**

你可以在任何支持 PlaceholderAPI 的插件中使用这些变量。

| 变量                                   | 描述                          | 输出示例        |
|:-------------------------------------|:----------------------------|:------------|
| `%commanditem_cooldown_<item_id>%`   | 显示一个物品格式化后的剩余冷却时间。          | 1m 15s / 可用 |
| `%commanditem_cooldown_s_<item_id>%` | 显示剩余冷却时间的秒数。                | 75 / 0      |
| `%commanditem_name_<item_id>%`       | 显示物品经过 MiniMessage 格式化后的名称。 | 炼狱之刃        |

### **从源码编译**

如果你想自己编译这个插件，请遵循以下步骤：

1. **克隆仓库:**  
2. ```
    git clone <repository_url>  
    cd CommandItem2
   ```
3. **确保你已安装 JDK 17 或更高版本。**  
4. **使用 Gradle 构建 JAR 文件:**  
   * Windows 系统:  
     `gradlew build`

   * Linux/macOS 系统:  
     `./gradlew build`

5. 编译好的 JAR 文件将会位于 build/libs/ 目录下。