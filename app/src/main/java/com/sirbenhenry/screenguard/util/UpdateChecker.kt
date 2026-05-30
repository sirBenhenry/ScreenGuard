package com.sirbenhenry.screenguard.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class ReleaseInfo(val version: String, val apkUrl: String, val releaseNotes: String)

object UpdateChecker {
    private const val RELEASES_API = "https://api.github.com/repos/SirBenHenry/ScreenGuard/releases/latest"
    private const val TAG = "UpdateChecker"

    suspend fun checkForUpdate(context: Context): ReleaseInfo? = withContext(Dispatchers.IO) {
        try {
            val conn = URL(RELEASES_API).openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
            conn.setRequestProperty("User-Agent", "ScreenGuard/${getInstalledVersion(context)}")
            val response = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            val tagName = json.getString("tag_name").trimStart('v')
            val body = json.optString("body", "")

            val assets = json.getJSONArray("assets")
            var apkUrl: String? = null
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                if (asset.getString("name").endsWith(".apk")) {
                    apkUrl = asset.getString("browser_download_url")
                    break
                }
            }

            if (apkUrl == null) return@withContext null
            val installed = getInstalledVersion(context)
            if (isNewer(tagName, installed)) ReleaseInfo(tagName, apkUrl, body)
            else null
        } catch (e: Exception) {
            Log.w(TAG, "Update check failed: ${e.message}")
            null
        }
    }

    suspend fun downloadAndInstall(context: Context, apkUrl: String, onProgress: (Int) -> Unit) = withContext(Dispatchers.IO) {
        try {
            val conn = URL(apkUrl).openConnection() as HttpURLConnection
            conn.connect()
            val total = conn.contentLength
            val file = File(context.cacheDir, "screenguard_update.apk")
            val stream = conn.inputStream
            val out = file.outputStream()
            var downloaded = 0
            val buf = ByteArray(8192)
            var n: Int
            while (stream.read(buf).also { n = it } != -1) {
                out.write(buf, 0, n)
                downloaded += n
                if (total > 0) onProgress((downloaded * 100 / total))
            }
            out.close()
            stream.close()

            // Trigger install
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Download failed: ${e.message}")
        }
    }

    private fun getInstalledVersion(context: Context): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.0.0"

    private fun isNewer(remote: String, installed: String): Boolean {
        fun parts(v: String) = v.split(".").mapNotNull { it.toIntOrNull() }
        val r = parts(remote)
        val i = parts(installed)
        val len = maxOf(r.size, i.size)
        for (idx in 0 until len) {
            val rv = r.getOrElse(idx) { 0 }
            val iv = i.getOrElse(idx) { 0 }
            if (rv > iv) return true
            if (rv < iv) return false
        }
        return false
    }
}
