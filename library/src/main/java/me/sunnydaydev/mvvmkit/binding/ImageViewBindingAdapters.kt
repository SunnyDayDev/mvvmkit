package me.sunnydaydev.mvvmkit.binding

import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

object ImageViewBindingAdapters {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun bindImageUrl(imageView: ImageView, url: String?) {
        bindImageUrl(imageView, url?.let { Uri.parse(it) })
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun bindImageUrl(imageView: ImageView, url: Uri?) {

        val glide = Glide.with(imageView)
        glide.clear(imageView)

        url ?: return

        glide.load(url).into(imageView)

    }

}