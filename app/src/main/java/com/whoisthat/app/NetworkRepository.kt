package com.whoisthat.app

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

/**
 * Calls the same backend used by com.yemen.numberphoneplus.
 * All requests are performed off the UI thread and delivered on the main thread.
 */
object NetworkRepository {

    private const val NAME_BASE = "https://a.namecheapmdeo.site"
    private const val NAME_PATH = "/api/arkamalyemenphone/2025/5/getname/"

    private const val NAME_AUTH =
        "v2V94ZFQw3q5IUlcZVN7YUInMoxWGiAhTZkbyMJSWEQWWeDXnWxR8L8e4OaAeirsUOKUVTyLjNHsFfTWBADE"
    private const val NUMBER_AUTH =
        "ForQw3q5IUlcZVN7YUInMoxWGiAhTZkbyMJSWEQWWeDXnWxR8L8e4OaAeirsUOKUVTyLjNHsFfTWPHONE"

    private const val NUMBER_HOST = "e.namecheapmdeo.site"
    // Same request as the capture: TLS connects to the captured Host, and the
    // captured Host header is sent explicitly. Using the raw IP here breaks
    // Android's certificate hostname verification.
    private const val REAL_NUMBER_URL =
        "https://e.namecheapmdeo.site/api/arkamalyemenphone/2025/5/P/9/" +
            "getnumberfefrefrt5tyrddyrtgrttygtgtgt/1?&&id2=null"

    // Body captured from the working reference app. Do not regenerate or encrypt it:
    // the fixed user blob, credentials and trailing spaces are required by the server.
    private const val NUMBER_BODY_TEMPLATE =
        "{\"certification\":null," +
            "\"user\":\"LQBfldoC6UJV9EfEOnyvc8y4ktiHqL3f/o+xZ3YlKRG/4vM64A9rs8UQZn5wHT" +
            "a5Ui4wYZlF4an1EC6sL0f5Yev93NLbO57sQeib8R2SuMtxg3tKQHjbYvMEUrRBai/u+8Ejrenwg" +
            "4B1+rhal1K3xP2P52EbG+UAAoZmobo4cQjx1/S1PCkYPmD1eeybei1nFnJgAostsDq7EESfvzq8" +
            "Tkj3pTc3hH11PVbfbjCvE3ExRwnAnH0EZzQIwsCP5yIPa04ICscT4yqG9B9zDiy3kg==\"," +
            "\"Auth\":\"WQdc93e7094-327b-4ff3-bf2e-c52f29a8277f      \"," +
            "\"phone\":0," +
            "\"User_Agent\":\"Dalvik/2.1.0 (Linux; U; Android 7.1.2; " +
            "SM-N975F Build/َN2G48H)\"," +
            "\"numZ\":5," +
            "\"versionApp\":13," +
            "\"cookie\":\"csrftoken=74wEeG0TBlOdRqUGLDuVvJt3jPEzZBPY; " +
            "_clck=1go9in%7C2%7Cfxb%7C0%7C2011; " +
            "_clsk=ik0jf9%7C1751614102753%7C1%7C1%7Cq.clarity.ms%2Fcollect" +
            "                          \"," +
            "\"username\":\"734840710\"," +
            "\"password\":\"734840710َ\"," +
            "\"domain\":\"https://dash.alzaem.net/\"}"

    private val executor = Executors.newFixedThreadPool(4)
    private val mainHandler = Handler(Looper.getMainLooper())

    data class Outcome(
        val results: List<ContactEntry> = emptyList(),
        val error: String? = null
    )

    fun searchByName(query: String, onResult: (Outcome) -> Unit) {
        val url = NAME_BASE + NAME_PATH + URLEncoder.encode(query.trim(), "UTF-8")
        val body = JSONObject().put("txtsearch", query.trim()).toString()
        execute(url, body, NAME_AUTH, "94", onResult)
    }

    fun searchByNumber(query: String, onResult: (Outcome) -> Unit) {
        val digits = query.trim().replace(Regex("[^0-9]"), "")
        if (digits.isEmpty()) {
            onResult(Outcome(error = "الرجاء إدخال رقم صحيح"))
            return
        }
        val phone = try {
            digits.toLong()
        } catch (e: NumberFormatException) {
            onResult(Outcome(error = "الرجاء إدخال رقم صحيح"))
            return
        }
        val body = NUMBER_BODY_TEMPLATE.replace("\"phone\":0", "\"phone\":$phone")
        execute(REAL_NUMBER_URL, body, NUMBER_AUTH, "94", onResult, NUMBER_HOST)
    }

    private fun execute(urlString: String, body: String, auth: String, version: String,
                        onResult: (Outcome) -> Unit, host: String? = null) {
        executor.execute {
            val outcome = try {
                val connection = URL(urlString).openConnection() as HttpURLConnection
                val bytes = body.toByteArray(Charsets.UTF_8)
                connection.requestMethod = "POST"
                connection.connectTimeout = 15000
                connection.readTimeout = 20000
                connection.doOutput = true
                connection.setFixedLengthStreamingMode(bytes.size)
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Content-Length", bytes.size.toString())
                connection.setRequestProperty("auth", auth)
                connection.setRequestProperty("version", version)
                connection.setRequestProperty("accept-encoding", "gzip")
                connection.setRequestProperty(
                    "operatingsystemversion", "RP1A.200720.012.A307FNXXU2CUJ2")
                connection.setRequestProperty("operatingsystem", "android")
                connection.setRequestProperty("User-Agent", "Dart/3.10 (dart:io)")
                if (host != null) connection.setRequestProperty("Host", host)

                connection.outputStream.use { it.write(bytes) }

                val code = connection.responseCode
                val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                val text = stream?.use { s ->
                    BufferedReader(InputStreamReader(s, Charsets.UTF_8)).use { it.readText() }
                }.orEmpty()
                connection.disconnect()

                when {
                    code == 401 -> Outcome(
                        error = "الخادم يرفض رمز التحقق (401) — بيانات الدخول في التطبيق المرجعي منتهية الصلاحية"
                    )
                    code == 400 -> Outcome(error = "الخادم رفض صيغة الطلب (400)")
                    code !in 200..299 -> Outcome(error = "خطأ من الخادم ($code)")
                    else -> {
                        val results = parseResults(text)
                        if (results.isEmpty() && looksEncrypted(text)) {
                            Outcome(error = "الخادم أعاد بيانات مشفرة؛ لا يمكن عرض الأسماء بدون فك تشفير")
                        } else {
                            Outcome(results = results)
                        }
                    }
                }
            } catch (e: Exception) {
                Outcome(error = "تعذر الاتصال بالخادم: ${e.message ?: "خطأ غير معروف"}")
            }
            mainHandler.post { onResult(outcome) }
        }
    }

    private fun looksEncrypted(text: String): Boolean {
        val t = text.trim()
        return t.length > 512 && t.all { it.isLetterOrDigit() || it in "+/=" }
    }

    private fun parseResults(text: String): List<ContactEntry> {
        if (text.isBlank()) return emptyList()
        val root = try {
            val trimmed = text.trim()
            if (trimmed.startsWith("[")) JSONArray(trimmed) else JSONObject(trimmed)
        } catch (e: Exception) {
            return emptyList()
        }
        val out = mutableListOf<ContactEntry>()
        when (root) {
            is JSONArray -> collect(root, out)
            is JSONObject -> {
                val nested = firstArray(root)
                if (nested != null) collect(nested, out) else collect(JSONArray().put(root), out)
            }
        }
        return out
    }

    private fun firstArray(obj: JSONObject): JSONArray? {
        for (key in listOf("data", "result", "results", "items", "list", "rows")) {
            val v = obj.opt(key)
            if (v is JSONArray) return v
            if (v is JSONObject) return firstArray(v)
        }
        val names = obj.names() ?: return null
        for (i in 0 until names.length()) {
            val v = obj.opt(names.getString(i))
            if (v is JSONArray) return v
        }
        return null
    }

    private fun collect(array: JSONArray, out: MutableList<ContactEntry>) {
        for (i in 0 until array.length()) {
            val v = array.opt(i)
            if (v is JSONObject) {
                out.add(
                    ContactEntry(
                        name = pick(v, "name", "fullname", "full_name", "owner", "customerName", "الاسم"),
                        number = pick(v, "phone", "number", "mobile", "tel", "phoneNumber", "الرقم"),
                        network = pick(v, "network", "company", "operator", "sim", "carrier", "الشبكة")
                    )
                )
            } else if (v is JSONArray) {
                collect(v, out)
            }
        }
    }

    private fun pick(obj: JSONObject, vararg keys: String): String {
        for (k in keys) {
            val v = obj.opt(k)
            if (v != null && v != JSONObject.NULL) return v.toString()
        }
        return ""
    }
}
