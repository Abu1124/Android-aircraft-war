# 实验二验证记录

日期：2026-11-23。课件：实验2.pdf，共28页，重点核对第4–5、19–24、26–27页。

## 实际环境

- IDE：本机 Rabbit，Android Studio 2026.2.2（不是课件指定的 Panda）。
- 工程：`C:\Users\18525\AndroidStudioProjects\AircraftWar2026`，路径无中文。
- Java 源码，minSdk 30；compileSdk 37；targetSdk 35。
- JBR 25.0.4；Gradle 9.2.1；AGP 9.0.0；Build Tools 36.0.0。
- 模拟器：Medium Phone API 37.0，1080×2400。
- 仅验证了现有 API37 模拟器，未在 API30 设备或真机实测。

## 实验要求与结果

| 课件要求 | 实现和验证 |
|---|---|
| 新建 Java / min API30 工程 | 配置已核对，应用和测试 APK 均构建成功 |
| 英文项目路径、AircraftWar2026 名称 | 已满足 |
| Activity + SurfaceView + 子线程 | BaseGame 实现 Callback、Runnable，MainActivity 加载 EasyGame |
| BitmapFactory + res/drawable | 图片统一按资源ID加载，inScaled=false，Surface 创建时缓存 |
| 滚动背景与英雄机、敌机、子弹可见 | 已在模拟器查看，截图 evidence/game.png |
| 手指拖动 | 已使用模拟器 swipe 操作；自动检查坐标边界 |
| 命中后消失并计分 | 自动检查击毁后敌机和子弹删除且恰加10分 |
| 出界与碰撞 | 自动检查敌机、子弹出界；无子弹时英雄撞机 |
| 线程退出 | pause 后工作线程已停止，恢复后重启，再次暂停后停止；Logcat 有 start/stop 记录 |
| 四步迁移 | 01/02/03 源码阶段快照分别构建通过；01空壳和03静态场景安装启动并截图；04最终玩法构建安装运行通过 |
| AI 日志 | AI_USAGE_LOG.md 如实记录协助范围，学生个人理解/修改待补 |
| Panda IDE | 未满足此版本名称：实际使用用户已有 Rabbit；未擅自卸载/更换 IDE |
| Git 与远程仓库 | 2026-11-25 用户明确授权首次提交和推送，并指定 GitHub 的 Abu1124/Android-aircraft-war；本次为现有成果的一次初始提交，不代表四阶段提交要求已满足 |

阶段快照是在本次迁移中保存、随后补做构建验证的文件版本，不是四条既有 Git 提交。截图和日志不能代替课件要求的 Git 历史。

## 自动检查结果

`evidence/instrumentation.txt` 中11项全部 PASS：

1. Surface 启动绘制线程。
2. pause 等待线程结束。
3. 子弹命中删除敌机和子弹，加10分。
4. 一颗子弹不会同时命中两个重叠敌机。
5. 敌机出界删除且不计分。
6. 子弹出界删除。
7. 无英雄子弹时仍能检测英雄撞机。
8. 新一局重置英雄生命及分数。
9. 触摸位置限制在可见场景内。
10. resume 重新启动绘制线程。
11. 再次 pause 停止线程。

`lintDebug --rerun-tasks` 已通过（0 errors，仍有29条警告，包括旧目标API、竖屏锁定、未使用的预留素材和原逻辑的命名/写法等）。没有添加 baseline 隐藏错误。首次 Lint 发现 local.properties 路径分隔转义/CRLF 问题，改为转义盘符和LF后重跑通过。

## 证据

- `build-verification.log`：应用及测试 APK 构建成功。
- `evidence/lint-build.log`：最终 Lint 成功。
- `evidence/instrumentation.txt`：11项设备检查。
- `evidence/logcat.txt`：绘制线程与游戏状态日志。
- `evidence/game.png`：运行中的背景、飞机、双方子弹及分数。
- `evidence/step1-shell.png`、`evidence/step3-static.png`：中间阶段画面。
- 相邻 `AircraftWar2026-checkpoints` 目录：阶段源码 ZIP 与各阶段构建日志。

## 尚需本人完成的提交事项

阅读并理解关键迁移代码，补充 AI 日志中的个人修改/解释；确认是否需按教师要求另用 Panda，以及是否仍需提交 Gitee。不得把以上事项或未运行的真机/API30测试写成已完成。原始 evidence 目录和日志保留在本机，未纳入源代码仓库。
