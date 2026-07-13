# Modifications to Moonlight Android

RevoMirror-Android is a derivative work based on
[Moonlight Android](https://github.com/moonlight-stream/moonlight-android)
(licensed under GPL-3.0).

This document records the modifications made to the original Moonlight Android
source code, in compliance with Section 5 of the GNU General Public License
v3.0, which requires modified versions to carry prominent notices stating that
the files were changed and the date of any change.

> **Note:** Files newly added for RevoMirror-Android, such as custom activities,
> dialogs, adapters, utility classes, PDF assets, icons, and layouts, are new
> code or resources added by Revopoint Software. This document focuses on
> changes made to files derived from the original Moonlight Android project, and
> summarizes the new additions where they affect the modified original files.

---

## Base Version

- **Upstream project:** Moonlight Android
- **Based on version / commit:** `Commits on Jul 28, 2024` 
- **Fork date:** 2025-08-11

---

## Summary of Modifications

| Date | Modified File(s) | Description of Change | Reason / Intent |
|------|------------------|-----------------------|-----------------|
| 2025-09-19 | `build.gradle`, `app/build.gradle` | 升级 Android Gradle Plugin、compile/target SDK，调整 minSdk，并新增 RecyclerView/AppCompat/PDFView 依赖 | 适配 RevoMirror Android 发布、打包和新 UI/PDF 展示能力 |
| 2025-09-19 | `app/src/main/AndroidManifest.xml` | 新增 `App` Application、`SplashActivity`、`SettingActivity`、`TutorialActivity`，调整主入口、主题、固定横屏和窗口配置 | 将 Moonlight 入口改为 RevoMirror 投屏应用流程 |
| 2025-09-19 | `PcViewActivity.java`, `AppViewActivity.java`, `AddComputerActivity.java`, `ShortcutActivity.java`, `StreamSettingsActivity.java`, `HelpActivity.java` | 重做设备列表、手动添加、配对、应用列表、快捷方式和设置入口相关界面逻辑 | 将原游戏串流客户端交互改为 RevoMirror 设备发现、配对和投屏入口 |
| 2025-09-19 | `GameActivity.java`, `ui/StreamView.java`, `binding/input/touch/AbsoluteTouchContext.java` | 调整串流页面 UI，新增加载弹窗、返回入口、触控事件转换、双指缩放和拖拽 | 支持移动端投屏画面查看和触控操作 |
| 2025-09-19 | `preferences/PreferenceConfiguration.java`, `res/xml/preferences.xml` | 调整默认串流参数和设置项读取逻辑 | 提供更符合 RevoMirror 投屏场景的默认配置 |
| 2025-09-19 | `grid/AppGridAdapter.java`, `grid/PcGridAdapter.java`, `grid/GenericGridAdapter.java`, `grid/assets/*AssetLoader.java` | 调整 PC/应用网格展示、资源加载和图标处理 | 配合新的设备首页和 RevoMirror 视觉样式 |
| 2025-09-19 | `utils/Dialog.java`, `utils/ServerHelper.java`, `utils/UiHelper.java`, `utils/LimeLog.java`, `utils/TvChannelHelper.java` | 调整弹窗、服务地址、UI 辅助、日志和 TV channel 相关逻辑 | 支撑自定义 UI、连接流程和发布需求 |
| 2025-09-19 | `res/layout/*`, `res/values/*`, `res/drawable*/*`, `res/mipmap*/*` | 重做启动页、设备页、设置页、串流页、弹窗、颜色、样式、字符串和图标资源；移除部分原 Moonlight 多语言和 launcher foreground 资源 | 替换为 RevoMirror 品牌和固定横屏投屏界面 |
| 2025-09-19 | `app/src/main/assets/gplv3/*`, `app/src/main/assets/lisence/*`, `app/src/main/assets/privacy/*` | 新增 GPLv3、用户协议、隐私政策 PDF 资源 | 在应用内展示开源许可和法务文档 |

---

## Detailed Notes

### 1. Build and Package Configuration

- **改动内容：** `build.gradle` 将 Android Gradle Plugin 从 7.3.0 升级到 8.6.0；`app/build.gradle` 将 compile/target SDK 升级到 35，minSdk 调整为 24，版本号改为 `1.0.0` / `100`，并新增 RecyclerView、AppCompat、PDFView 依赖。
- **改动原因：** 满足 RevoMirror Android 的目标平台、发布包名、打包命名和新增 UI/PDF 功能需求。
- **影响范围：** 影响构建产物、安装包身份、系统兼容范围和依赖集合。

### 2. Application Entry and Activity Registration

- **改动内容：** `AndroidManifest.xml` 将应用入口从原 Moonlight 主界面调整为 `SplashActivity`，注册 `App` Application、`SettingActivity` 和 `TutorialActivity`，并为主要界面设置横屏、主题和窗口行为。
- **改动原因：** 增加 RevoMirror 启动页、统一语言初始化、设置页和内置 PDF 法务文档展示。
- **影响范围：** 影响应用启动流程、Activity 路由、窗口显示和系统 UI 行为。

### 3. Device Discovery, Pairing, and Main UI

- **改动内容：** `PcViewActivity.java` 由原 Moonlight PC 网格页改为 RevoMirror 投屏设备首页，新增无网络提示、设置入口、手动添加入口、配对弹窗、底部菜单和 RecyclerView 设备列表；相关改动涉及 `PcGridAdapter.java`、`GenericGridAdapter.java`、`ComputerAdapter.java`、`PairPcDialog.java`、`BottomMenuDialog.java` 和设备页布局资源。
- **改动原因：** 将原游戏串流客户端的 PC 管理流程调整为 RevoMirror 设备发现、配对和投屏入口。
- **影响范围：** 影响设备发现结果展示、配对操作、离线状态显示和主页面交互。

### 4. Streaming and Touch Interaction

- **改动内容：** `GameActivity.java` 替换部分原始加载提示和串流页面 UI，增加返回按钮和 `TouchControlLayout`；`StreamView.java` 增加双指缩放、双指拖拽、触控坐标转换和触控处理开关；`AbsoluteTouchContext.java` 配合转换后的坐标处理触控输入。
- **改动原因：** 适配手机/平板观看和操控投屏画面的使用场景。
- **影响范围：** 影响串流页面显示、触控事件传递、缩放拖拽和加载状态展示。

### 5. Settings, Language, and Legal Documents

- **改动内容：** 新增 `SettingActivity.java`、`TutorialActivity.java`、`LanguageUtils.java`、`SharedPreferenceUtil.java` 等代码，修改 `PreferenceConfiguration.java` 和 `preferences.xml`，支持应用内语言切换、关于页、用户协议、隐私政策和 GPLv3 文档展示。
- **改动原因：** 提供 RevoMirror 的语言设置、版本信息和 GPL/法务入口。
- **影响范围：** 影响设置页、语言资源加载、默认偏好设置和法务文档展示。

### 6. UI Resources and Branding

- **改动内容：** 修改 `activity_pc_view.xml`、`activity_game.xml`、`pc_grid_item.xml`、`values/strings.xml`、`values/colors.xml`、`values/styles.xml` 等原资源，新增启动页、设置页、弹窗、设备项、图标和背景资源，并替换 launcher 图标。
- **改动原因：** 将原 Moonlight 视觉界面替换为 RevoMirror 品牌和横屏投屏界面。
- **影响范围：** 影响主要页面布局、文案、多语言、图标、主题和视觉样式。

### 7. Utility and Support Code

- **改动内容：** 修改 `Dialog.java`、`UiHelper.java`、`ServerHelper.java`、`LimeLog.java` 等辅助类，并新增 `BaseActivity.java`、`LogUtil.java`、`ScreenUtil.java`、`Utils.java`、`ViewUtil.java`、`EventHub.java` 等工具类。
- **改动原因：** 支撑沉浸式 UI、刘海屏适配、统一弹窗、日志输出、语言刷新和通用界面处理。
- **影响范围：** 影响应用内弹窗、日志、屏幕适配、状态栏/导航栏隐藏和 UI 事件通知。

---

## RevoMirror-Android Files and Resources

The following major additions are new code or resources added for
RevoMirror-Android rather than direct modifications of existing Moonlight files:

- `App.java`, `SplashActivity.java`, `SettingActivity.java`, `TutorialActivity.java`
- `ui/activity/BaseActivity.java`
- `ui/adapter/ComputerAdapter.java`, `ui/adapter/SettingItemAdapter.java`
- `ui/dialog/*Dialog.java`
- `utils/LanguageUtils.java`, `utils/LogUtil.java`, `utils/ScreenUtil.java`, `utils/SharedPreferenceUtil.java`, `utils/Utils.java`, `utils/ViewUtil.java`, `utils/EventHub.java`
- `view/GridSpacingItemDecoration.java`, `view/RoundFrameLayout.java`, `view/TouchControlLayout.java`
- RevoMirror layouts, drawables, launcher icons, and PDF assets under `app/src/main/res` and `app/src/main/assets`

---

## Copyright of Modifications

All modifications described above are:

Copyright (C) 2025 Revopoint Software
(Xi'an Chishine Optoelectronics Technology Co., Ltd.)

These modifications are licensed under the GNU General Public License v3.0,
consistent with the license of the original Moonlight Android project.
