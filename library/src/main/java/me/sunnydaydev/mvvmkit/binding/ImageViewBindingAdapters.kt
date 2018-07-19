package me.sunnydaydev.mvvmkit.binding

import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import androidx.databinding.BindingConversion
import android.graphics.Bitmap
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.RequestOptions
import java.net.URL


/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

object ImageViewBindingAdapters {

    @JvmStatic
    @BindingConversion
    fun convertStringToUri(string: String?): Uri? = string?.let { Uri.parse(it) }

    @JvmStatic
    @BindingConversion
    fun convertURLToUri(string: URL?): Uri? = string?.let { Uri.parse(it.toString()) }

    @JvmStatic
    @BindingAdapter("imageDrawable")
    fun bindImageDrawable(view: ImageView, drawable: Drawable?) {
        view.setImageDrawable(drawable)
    }

    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "imageUrlOptions"])
    fun bindImageUrl(imageView: ImageView, url: Uri?, options: RequestOptions?) {

        val glide = Glide.with(imageView)
        glide.clear(imageView)

        url ?: return

        glide.load(url)
                .apply { if (options != null) apply(options) }
                .into(imageView)

    }

    @JvmStatic
    @BindingAdapter(
            value = [
                "imageUrl",
                "imageUrlCenterCrop",
                "imageUrlCenterInside",
                "imageUrlCircleCrop",
                "imageUrlFitCenter",
                "imageUrlTransformation"
            ],
            requireAll = false
    )
    fun bindImageUrl(imageView: ImageView, url: Uri?,
                     centerCrop: Boolean?,
                     centerInside: Boolean?,
                     circleCrop: Boolean?,
                     fitCenter: Boolean?,
                     transformation: Transformation<Bitmap>?) {

        val options = RequestOptions()
                .applyIf(centerCrop) { centerCrop() }
                .applyIf(centerInside) { centerInside() }
                .applyIf(circleCrop) { circleCrop() }
                .applyIf(fitCenter) { fitCenter() }
                .applyIf(transformation != null) { transform(transformation!!) }

        bindImageUrl(imageView, url, options)

    }

    private fun RequestOptions.applyIf(
            check: Boolean?,
            action: RequestOptions.() -> RequestOptions
    ): RequestOptions = if (check == true) action(this) else this

}