package com.seedream.app.storage

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageStorage(
    private val context: Context,
    private val client: OkHttpClient = OkHttpClient()
) {
    private val historyDir: File
        get() = File(context.filesDir, "history_images").also { it.mkdirs() }

    suspend fun cacheImage(src: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            val bytes = readImageBytes(src) ?: return@runCatching null
            val file = File(historyDir, "seedream_${System.currentTimeMillis()}_${bytes.size}.jpg")
            FileOutputStream(file).use { it.write(bytes) }
            file.absolutePath
        }.getOrNull()
    }

    suspend fun saveToPictures(src: String, displayName: String = "seedream_${System.currentTimeMillis()}.jpg"): Uri? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val bytes = readImageBytes(src) ?: return@runCatching null
                val resolver = context.contentResolver
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Seedream")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return@runCatching null
                resolver.openOutputStream(uri)?.use { it.write(bytes) }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                }
                uri
            }.getOrNull()
        }
    }

    fun deleteCachedFile(path: String?) {
        if (path.isNullOrBlank()) return
        val file = File(path)
        if (file.parentFile?.canonicalPath == historyDir.canonicalPath) {
            file.delete()
        }
    }

    private fun readImageBytes(src: String): ByteArray? {
        return when {
            src.startsWith("data:image", ignoreCase = true) -> {
                val payload = src.substringAfter(",", missingDelimiterValue = "")
                if (payload.isBlank()) null else Base64.decode(payload, Base64.DEFAULT)
            }
            src.startsWith("http://", true) || src.startsWith("https://", true) -> {
                val response = client.newCall(Request.Builder().url(src).build()).execute()
                response.use {
                    if (!it.isSuccessful) return null
                    it.body?.bytes()
                }
            }
            src.startsWith("file://", true) -> {
                File(Uri.parse(src).path ?: return null).takeIf { it.exists() }?.readBytes()
            }
            src.startsWith("content://", true) -> {
                context.contentResolver.openInputStream(Uri.parse(src))?.use { it.readBytes() }
            }
            File(src).exists() -> File(src).readBytes()
            else -> null
        }
    }
}
