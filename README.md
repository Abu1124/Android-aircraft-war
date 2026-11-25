# AircraftWar2026 — 实验二

Java；minSdk 30；Activity + SurfaceView + 独立游戏线程。默认进入简单模式。

本项目由两位小组成员共同完成，使用 GitHub 协作管理源代码。当前仓库保存实验二的 Android 迁移基础版本，后续计划新增登录和在线排行榜功能。

仓库：https://github.com/Abu1124/Android-aircraft-war

## 两人协作

另一位成员可通过 `git clone https://github.com/Abu1124/Android-aircraft-war.git` 获取项目，在 Android Studio 中打开仓库根目录。首次同步时使用自己的 Android SDK 路径和 Gradle JDK；`local.properties`、`.idea`、构建目录和安装包不共享。建议分别建立功能分支，通过 Pull Request 合并，开发前先拉取最新代码。

## 在现有 Rabbit 中运行

1. Android Studio → Open，选择克隆后的仓库根目录，不要选择 app 子目录。本机现有工程位于 `C:\Users\18525\AndroidStudioProjects\AircraftWar2026`。
2. Gradle JDK 选择 Android Studio 自带的 JBR（本机 JDK 25），不要选择系统中其他不兼容的 Java 版本。
3. SDK Manager 中安装编译所需的 Android API 37 平台和 Build Tools 36.0.0。等待 Gradle 同步，选择 `app` 和 API 30 或以上的模拟器/真机，点击 Run。本机已验证 `Medium Phone API 37.0`。
4. 手指或鼠标在模拟器内拖动英雄机，自动射击。左上显示分数和生命值。游戏结束后点击游戏区域重新开始。
5. 返回桌面或退出 Activity 后，Logcat 的 `AircraftWar` 标签应出现 `render thread stopped`。

本机已有 SDK 37.0 和 Build Tools 36.0.0，因此 compileSdk 使用 37，minSdk 仍严格为 30；targetSdk 为 35。编译 API 与最低支持 API 是不同概念。本实验未使用高于 API 30 的游戏接口。

课件第26页指定 Panda，本机实际是 Rabbit（Android Studio 2026.2.2）。本工程使用现有环境，不将其记录为 Panda；如教师对 IDE 版本有单独验收要求，需要说明该差异。

Gradle Wrapper 固定为 9.2.1（带 SHA-256 校验），AGP 固定为 9.0.0。实际构建结果见验证记录。初次依赖下载需要可用网络，仓库已配置依赖镜像；如同步失败，可在 Android Studio 的 HTTP Proxy 设置中使用自己电脑的实际代理，代理凭据不要提交到仓库。

## 目录与迁移盘点

- `application/MainActivity.java`：应用入口，管理前后台生命周期。
- `application/BaseGame.java`：替代原 Swing Game；Surface 回调、固定40ms逻辑更新、Canvas 绘制、输入队列及碰撞计分。
- `application/EasyGame.java`：默认简单模式，替代难度选择页面。
- `application/ImageManager.java`：surfaceCreated 时 BitmapFactory.decodeResource 加载并缓存；不持有 Activity。
- `application/GameConfig.java`：512×768 是原游戏的逻辑坐标空间；真实 Surface 尺寸用于等比例缩放和居中，触摸做逆变换。不是写死手机物理像素。
- `aircraft/basic/bullet/items/observer/shooting`：来自上学期项目，保留实体继承、射击策略、敌机工厂及观察者结构，替换桌面图片与窗口依赖。
- `res/drawable`：原图片素材，重命名大写字符，统一资源加载。
- `res/layout/activity_main.xml`：空壳阶段的普通布局，最终由游戏 SurfaceView 替代。

直接或基本复用：观察者接口、射击算法、生命值、工厂结构、道具效果和计分规则。

需替换：Swing/AWT、BufferedImage/ImageIO、鼠标控制、Timer 绘制、桌面窗口尺寸引用。原模型依赖图片尺寸，因此不是整包原样复制；已换为 Bitmap 和统一逻辑场景配置。

后续实验：难度选择页面、音效、排行榜/存储、登录与在线排行榜。本实验不接入网络或桌面 DAO。

## 关键实现与修正

- 绘制线程只计算和 Canvas 绘制，不跳页面、不弹对话框。
- Canvas 在 finally 中 unlockCanvasAndPost；onPause 与 surfaceDestroyed 均停止并等待工作线程结束。
- 主线程仅发布触摸坐标，实体和集合仅由游戏线程更新；暂停后的测试才允许检查内部状态。
- 修复原英雄撞机检测嵌在子弹循环内的问题；无子弹时也判撞机。
- 每颗子弹至多命中一个敌机，避免重复计分。
- 英雄机按图片半宽高限制在画布内，敌机水平生成位置留出半宽。
- 火力/冻结持续时间按游戏帧计时，随游戏暂停，无额外休眠线程修改对象。
- 新一局重置英雄机单例、分数、集合和计时状态。
- 修正原 ImageManager 中道具图片与效果的对应关系。

## 构建与测试

Windows PowerShell：

```powershell
$env:JAVA_HOME = 'D:\Android development\jbr'
.\gradlew.bat assembleDebug assembleDebugAndroidTest lintDebug
```

APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。项目 Java 源码不依赖第三方库。

`Lab2Instrumentation` 在真实 Android 运行时检查命中计分、单颗子弹只命中一次、敌机与子弹出界、无子弹时撞机、重开状态、触摸边界和线程暂停恢复。它会临时暂停游戏后布置确定性场景，不修改正式玩法参数。

## 迁移过程与版本记录

相邻目录 `AircraftWar2026-checkpoints` 保存四阶段源码快照（不含 IDE、build 或本机配置）。这些是阶段文件快照，不是 Git 历史。不得将其冒充已经提交的日志，也不回填虚假的提交时间。

本次首次提交将现有实验二成果作为一个初始版本保存，不将本地阶段快照伪造成四条已发生的提交。用户选择 GitHub 作为两人协作仓库；课件原文要求的 Gitee 和四阶段过程提交并未因此自动满足。

AI 使用过程见 `AI_USAGE_LOG.md`。验证结果见 `VALIDATION.md`，其中原始截图和运行日志保存在本机，不纳入源代码提交。
