# Release Workflow

Use Git tags and GitHub Releases so source code and APK files stay tied to the same version.

## 1. Update App Version

Edit `gradle.properties`:

```properties
APP_VERSION_CODE=7
APP_VERSION_NAME=1.1.5
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
git commit -m "Release v1.1.5"
git tag v1.1.5
git push
git push origin v1.1.5
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
Seedream-302-v1.1.5-debug.apk
```

Local APK backups may be kept under `releases/apk/`, but APK files should not be committed to the source repository. Publish distributable APKs through GitHub Releases.

## Troubleshooting

If a release APK does not appear on GitHub:

1. Confirm the tag was pushed:

```powershell
git push origin v1.1.5
```

2. If the tag already exists on GitHub but was created before the workflow file existed, recreate it:

```powershell
git push origin :refs/tags/v1.1.5
git tag -d v1.1.5
git tag v1.1.5
git push origin v1.1.5
```

3. To upload a locally built APK manually:

```powershell
gh release upload v1.1.5 app\build\outputs\apk\debug\app-debug.apk --repo OneGemmj/302AISeedreamAPP --clobber
```
