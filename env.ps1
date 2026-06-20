$ErrorActionPreference = 'Stop'

if (-not $env:JAVA_HOME) {
    Write-Warning 'JAVA_HOME is not set. Install JDK 17 or set JAVA_HOME before building.'
}

if (-not $env:ANDROID_HOME -and $env:ANDROID_SDK_ROOT) {
    $env:ANDROID_HOME = $env:ANDROID_SDK_ROOT
}

if (-not $env:ANDROID_SDK_ROOT -and $env:ANDROID_HOME) {
    $env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
}

if ($env:JAVA_HOME) {
    $javaBin = Join-Path $env:JAVA_HOME 'bin'
    if ($env:Path -notlike "*$javaBin*") {
        $env:Path = "$javaBin;$env:Path"
    }
}

if ($env:ANDROID_HOME) {
    $androidTools = @(
        (Join-Path $env:ANDROID_HOME 'cmdline-tools\latest\bin'),
        (Join-Path $env:ANDROID_HOME 'platform-tools')
    )
    foreach ($toolPath in $androidTools) {
        if ($env:Path -notlike "*$toolPath*") {
            $env:Path = "$toolPath;$env:Path"
        }
    }
}

Write-Host 'Environment check:'
java -version
if (Get-Command gradle -ErrorAction SilentlyContinue) {
    gradle -v
} else {
    Write-Host 'Gradle command is not on PATH. The project can still build with .\gradlew.bat when network/cache is available.'
}
adb version
