package me.sunnydaydev.mvvmkit.binding

import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import me.sunnydaydev.mvvmkit.R
import me.sunnydaydev.mvvmkit.observable.Command

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 30.07.2018.
 * mail: mail@sunnydaydev.me
 */
 
object WebViewBindings {

    private const val WEBVIEW_URL = "me.sunnydaydev.mvvmkit.WebViewBindingAdapters.WebViewUrl"
    private const val WEBVIEW_DATA = "me.sunnydaydev.mvvmkit.WebViewBindingAdapters.WebViewData"

    @JvmStatic
    @BindingAdapter(
            value = [
                "urlCommand",
                "dataCommand",
                "clearOnChange"
            ],
            requireAll = false
    )
    fun bindWebViewContentCommand(view: WebView,
                                  urlCommand: Command<WebViewUrl>?,
                                  dataCommand: Command<WebViewData>?,
                                  clear: Boolean?) {

        urlCommand?.handle {
            bindWebViewContent(view, null, it, clear)
        }

        dataCommand?.handle {
            bindWebViewContent(view, it, null, clear)
        }

    }

    @JvmStatic
    @BindingAdapter("webViewClient")
    fun bindWebViewClient(view: WebView, client: WebViewClient?) {
        view.webViewClient = client
    }

    @JvmStatic
    @BindingAdapter("webChromeClient")
    fun bindWebChromeClient(view: WebView, client: WebChromeClient?) {
        view.webChromeClient = client
    }

    @JvmStatic
    @BindingAdapter(value = ["data", "url", "clearOnChange"], requireAll = false)
    fun bindWebViewContent(view: WebView, data: WebViewData?, url: WebViewUrl?, clear: Boolean?) {

        if (url == null) {
            ListenerUtil.trackListener(view, null, R.id.binding_webview_url)
        }

        if (data == null) {
            ListenerUtil.trackListener(view, null, R.id.binding_webview_data)
        }

        fun withClearingCheck(action: WebView.() -> Unit) {

            val needClear = clear == true

            if (needClear) {

                view.clearViewCompat()
                view.clearCache(false)
                view.clearHistory()

            }

            action(view)

            if (needClear) {
                view.clearHistory()
            }

        }

        when {

            url != null -> {

                val previous: WebViewUrl? = ListenerUtil
                        .getListener(view, R.id.binding_webview_url) as? WebViewUrl

                if (previous == url) {
                    return
                }

                withClearingCheck {
                    view.loadUrl(url.url, url.headers)
                }

                ListenerUtil.trackListener(view, url, R.id.binding_webview_url)

            }

            data != null -> {

                val previous: WebViewData? = ListenerUtil
                        .getListener(view, R.id.binding_webview_data) as? WebViewData

                if (previous == data) {
                    return
                }

                withClearingCheck {

                    if (data.baseUrl == null && data.historyUrl == null) {

                        view.loadData(data.html, data.mimeType, data.encoding)

                    } else {

                        view.loadDataWithBaseURL(
                                data.baseUrl, data.html,
                                data.mimeType, data.encoding, data.historyUrl
                        )

                    }

                }

                ListenerUtil.trackListener(view, data, R.id.binding_webview_data)

            }

            else -> {

                view.clearViewCompat()
                view.clearCache(false)
                view.clearHistory()

            }

        }

    }

    fun saveState(webView: WebView, outState: Bundle) {

        (webView.getTag(R.id.binding_webview_data) as? WebViewData)?.let {
            outState.putParcelable(WEBVIEW_DATA, it)
        }

        (webView.getTag(R.id.binding_webview_url) as? WebViewUrl )?.let {
            outState.putParcelable(WEBVIEW_URL, it)
        }

    }

    fun restoreState(webView: WebView, inState: Bundle) {

        (inState.getParcelable(WEBVIEW_DATA) as? WebViewData) ?.let {
            webView.setTag(R.id.binding_webview_data, it)
        }

        (inState.getParcelable(WEBVIEW_URL) as? WebViewUrl) ?.let {
            webView.setTag(R.id.binding_webview_url, it)
        }

    }

    private fun WebView.clearViewCompat() =
            if (Build.VERSION.SDK_INT < 18) {
                @Suppress("DEPRECATION")
                clearView()
            } else {
                loadUrl("about:blank")
            }

}

data class WebViewData(val html: String,
                       val baseUrl: String? = null,
                       val historyUrl: String? = null,
                       val encoding: String = "utf-8",
                       val mimeType: String = "text/html") : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(html)
        parcel.writeString(baseUrl)
        parcel.writeString(historyUrl)
        parcel.writeString(encoding)
        parcel.writeString(mimeType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WebViewData> {

        override fun createFromParcel(parcel: Parcel): WebViewData {
            return WebViewData(
                    parcel.readString()!!,
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readString()!!,
                    parcel.readString()!!
            )
        }

        override fun newArray(size: Int): Array<WebViewData?> {
            return arrayOfNulls(size)
        }

    }

}

data class WebViewUrl(val url: String, val headers: Map<String, String>? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readStringMap()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeStringMap(headers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WebViewUrl> {

        override fun createFromParcel(parcel: Parcel): WebViewUrl {
            return WebViewUrl(parcel)
        }

        override fun newArray(size: Int): Array<WebViewUrl?> {
            return arrayOfNulls(size)
        }

        private fun Parcel.readStringMap(): Map<String, String>? {
            if (readByte() == 0.toByte()) return null
            return (1 .. readInt())
                    .associate { readString() to readString() }
        }

        private fun Parcel.writeStringMap(headers: Map<String, String>?) {
            writeByte(if (headers == null) 0 else 1)
            headers ?: return
            writeInt(headers.size)
            headers.forEach {
                writeString(it.key)
                writeString(it.value)
            }
        }

    }

}