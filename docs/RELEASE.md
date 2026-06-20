# Release Workflow

Use Git tags and GitHub Releases so source code and APK files stay tied to the same version.

## 1. Update App Version

Edit `gradle.properties`:

```properties
APP_VERSION_CODE=3
APP_VERSION_NAME=1.2.0
```

Rules:

- `APP_VERSION_CODE` must be a higher integer than the previous release.
- `APP_VERSION_NAME` should match the release tag without the leading `v`.

## 2. Verify Locally

```powershell
.\gradlew.bat test assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 3. Commit and Tag

```powershell
git add .
git commit -m "Release v1.2.0"
git tag v1.2.0
git push
git push origin v1.2.0
```

## 4. GitHub Release

Pushing a `v*` tag triggers the GitHub Actions workflow in `.github/workflows/android-release.yml`.

The workflow:

- runs unit tests,
- builds the debug APK,
- renames it to include the version,
- attaches it to a GitHub Release.

For a manually built APK, use the same file naming pattern:

```text
Seedream-302-v1.2.0-debug.apk
```
