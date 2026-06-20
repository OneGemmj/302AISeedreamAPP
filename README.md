# Seedream 302 Android

原生 Android 版 `302.AI Seedream 5.0 / 4.5` 图像生成与编辑工具，基于 `D:\Users\ikunx\Desktop\sd5_Version6.html` 的功能迁移。

## 功能

- API Key 粘贴、显示/隐藏、Android Keystore 加密保存、清空。
- 接口地址编辑和 3 次网络延迟测试。
- Seedream 5.0 / 4.5 模型选择。
- Prompt、多个本地参考图、多个 URL 参考图。
- 参考图缩略图、排序、删除、清空。
- `size`、`seed`、`response_format`、`watermark`、`stream`、`sequential_image_generation`、`max_images`、`tools.web_search` 参数构造。
- 前台服务执行请求，通知栏可停止，网络/5xx 错误最多 5 次指数退避重试。
- 普通 JSON 和 SSE 流式响应解析。
- 结果图片预览、保存到 `Pictures/Seedream`。
- Room 历史记录，本地缓存生成图片，支持搜索、复制 Prompt、保存、删除、清空。

## 构建环境

本机已安装一套便携构建环境到 `D:\Users\ikunx\AndroidBuildEnv`：

- JDK 17：`D:\Users\ikunx\AndroidBuildEnv\jdk-17`
- Gradle 9.4.1：`D:\Users\ikunx\AndroidBuildEnv\gradle\gradle-9.4.1`
- Android SDK：`D:\Users\ikunx\AndroidBuildEnv\android-sdk`
- 已安装 SDK 包：`platform-tools`、`platforms;android-37.0`、`build-tools;37.0.0`

`JAVA_HOME`、`GRADLE_HOME`、`ANDROID_HOME`、`ANDROID_SDK_ROOT` 和用户 `Path` 已写入用户环境变量。若当前终端还没刷新环境变量，可先运行：

```powershell
.\env.ps1
```

构建项目：

```powershell
.\gradlew test assembleDebug
```

Debug APK 输出位置：`app\build\outputs\apk\debug\app-debug.apk`。

如果 Android Studio 已安装，也可以直接打开本目录并等待 Gradle Sync；项目已包含 `local.properties` 指向本机 Android SDK。

## 主要源码

- `app/src/main/java/com/seedream/app/ui`：Compose 页面和 ViewModel。
- `app/src/main/java/com/seedream/app/service`：前台服务、通知、停止、重试、保活。
- `app/src/main/java/com/seedream/app/network`：POST 请求、SSE 解析、图片 URL 提取。
- `app/src/main/java/com/seedream/app/storage`：Room、图片缓存、相册保存、Key 加密存储。
- `app/src/test/java/com/seedream/app`：payload、URL 提取、SSE、重试策略单元测试。
