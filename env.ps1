$env:JAVA_HOME = 'D:\Users\ikunx\AndroidBuildEnv\jdk-17'
$env:GRADLE_HOME = 'D:\Users\ikunx\AndroidBuildEnv\gradle\gradle-9.4.1'
$env:ANDROID_HOME = 'D:\Users\ikunx\AndroidBuildEnv\android-sdk'
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:Path = "$env:JAVA_HOME\bin;$env:GRADLE_HOME\bin;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\platform-tools;$env:Path"

java -version
gradle -v
adb version
