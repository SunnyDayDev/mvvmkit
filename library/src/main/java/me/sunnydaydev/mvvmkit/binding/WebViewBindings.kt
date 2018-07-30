package me.sunnydaydev.mvvmkit.binding

import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BindingAdapter
import me.sunnydaydev.mvvmkit.observable.Command

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 30.07.2018.
 * mail: mail@sunnydaydev.me
 */
 
object WebViewBindings {

    @JvmStatic
    @BindingAdapter(
            value = [
                "loadUrlCommand",
                "additionalHeaders",
                "clearBeforeLoad"
            ],
            requireAll = false
    )
    fun bindUrl(view: WebView,
                command: Command<String>?,
                headers: Map<String, String>?,
                clearBeforeLoad: Boolean?) {

        command ?: return

        command.handle {

            if (clearBeforeLoad == true) {

                if (Build.VERSION.SDK_INT < 18) {
                    @Suppress("DEPRECATION")
                    view.clearView()
                } else {
                    view.loadUrl("about:blank")
                }

                view.clearCache(false)

            }

            if (headers == null) view.loadUrl(it)
            else view.loadUrl(it, headers)

            if (clearBeforeLoad == true) {
                view.clearHistory()
            }

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

}