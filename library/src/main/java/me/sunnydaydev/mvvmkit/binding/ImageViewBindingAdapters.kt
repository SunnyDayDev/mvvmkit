package me.sunnydaydev.mvvmkit.binding

import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import androidx.databinding.BindingConversion
import android.graphics.Bitmap
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.RequestOptions
import me.sunnydaydev.mvvmkit.util.findActivity
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
    @BindingAdapter(
            value = [
                "imageUri",
                "imageUriCenterCrop",
                "imageUriCenterInside",
                "imageUriCircleCrop",
                "imageUriFitCenter"
            ],
            requireAll = false
    )
    fun bindImageUri(imageView: ImageView,
                     uri: Uri?,
                     centerCrop: Boolean?,
                     centerInside: Boolean?,
                     circleCrop: Boolean?,
                     fitCenter: Boolean?) =
            bindImageUri(imageView, uri, centerCrop, centerInside,
                    circleCrop, fitCenter, null)

    @JvmStatic
    @BindingAdapter(
            value = [
                "imageUri",
                "imageUriCenterCrop",
                "imageUriCenterInside",
                "imageUriCircleCrop",
                "imageUriFitCenter",
                "imageUriTransformation"
            ],
            requireAll = true
    )
    fun bindImageUri(imageView: ImageView,
                     uri: Uri?,
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

        bindImageUri(imageView, uri, options)

    }

    @JvmStatic
    @BindingAdapter(value = ["imageUri", "imageUriOptions"])
    fun bindImageUri(imageView: ImageView, url: Uri?, options: RequestOptions?) {

        val glide = Glide.with(imageView)
        glide.clear(imageView)

        url ?: return

        glide.load(url)
                .apply { if (options != null) apply(options) }
                .into(imageView)

    }

    private fun RequestOptions.applyIf(
            check: Boolean?,
            action: RequestOptions.() -> RequestOptions
    ): RequestOptions = if (check == true) action(this) else this

    @JvmStatic
    @BindingAdapter("srcCompat")
    fun bindSrcCompat(view: ImageView, id: Int) {
        if (id == -1) {
            view.setImageDrawable(null)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setImageDrawable(ContextCompat.getDrawable(view.context, id))
            } else {
                try {
                    view.setImageDrawable(ContextCompat.getDrawable(view.context, id))
                } catch (e: Throwable) {
                    try {
                        val vector = VectorDrawableCompat.create(
                                view.resources, id, view.findActivity()?.theme)
                        view.setImageDrawable(vector)
                    } catch (ignored: Throwable) {
                        throw e
                    }
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("src")
    fun bindSrc(view: ImageView, id: Int) {
        if (id == -1) {
            view.setImageDrawable(null)
        } else {
            view.setImageDrawable(ContextCompat.getDrawable(view.context, id))
        }
    }

}