package com.example.bridge

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import com.example.gemini.GeminiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class WebAppInterface(
    private val context: Context,
    private val webView: WebView,
    private val coroutineScope: CoroutineScope
) {
    private val geminiClient = GeminiClient()
    private val prefs = context.getSharedPreferences("rst_erp_prefs", Context.MODE_PRIVATE)

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun playBeep() {
        try {
            val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
        } catch (_: Exception) {}
    }

    @JavascriptInterface
    fun playSuccessTone() {
        try {
            val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneG.startTone(ToneGenerator.TONE_PROP_ACK, 200)
        } catch (_: Exception) {}
    }

    @JavascriptInterface
    fun playErrorTone() {
        try {
            val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneG.startTone(ToneGenerator.TONE_PROP_NACK, 300)
        } catch (_: Exception) {}
    }

    @JavascriptInterface
    fun saveState(key: String, json: String) {
        prefs.edit().putString(key, json).apply()
    }

    @JavascriptInterface
    fun loadState(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    @JavascriptInterface
    fun shareText(title: String, text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_TITLE, title)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    @JavascriptInterface
    fun requestLowLatencyAi(prompt: String, callbackFunction: String) {
        coroutineScope.launch {
            val result = geminiClient.getLowLatencyResponse(prompt)
            invokeJsCallback(callbackFunction, result)
        }
    }

    @JavascriptInterface
    fun requestVideoAnalysis(prompt: String, videoBase64: String, mimeType: String, callbackFunction: String) {
        coroutineScope.launch {
            val result = geminiClient.analyzeVideoContent(prompt, videoBase64, mimeType)
            invokeJsCallback(callbackFunction, result)
        }
    }

    @JavascriptInterface
    fun requestHighThinkingAi(prompt: String, callbackFunction: String) {
        coroutineScope.launch {
            val result = geminiClient.getHighThinkingResponse(prompt)
            invokeJsCallback(callbackFunction, result)
        }
    }

    private fun invokeJsCallback(callbackFunction: String, responseText: String) {
        coroutineScope.launch(Dispatchers.Main) {
            val escaped = JSONObject.quote(responseText)
            webView.evaluateJavascript("window.$callbackFunction($escaped);", null)
        }
    }
}
