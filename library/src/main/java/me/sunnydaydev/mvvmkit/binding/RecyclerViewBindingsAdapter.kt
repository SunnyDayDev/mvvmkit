package me.sunnydaydev.mvvmkit.binding

import android.databinding.BindingAdapter
import android.databinding.adapters.ListenerUtil
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import me.sunnydaydev.mvvmkit.R
import me.sunnydaydev.mvvmkit.observable.Command
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

object RecyclerViewBindingsAdapter {

    @JvmStatic
    @BindingAdapter("scrollToPosition")
    fun bindScrollToPosition(view: RecyclerView, position: Command<Int>) {
        position.handle(view::scrollToPosition)
    }

    @JvmStatic
    @BindingAdapter(
            value = [
                "recyclerView_items",
                "recyclerView_itemLayoutMap",
                "recyclerView_itemsStableId"
            ],
            requireAll = false
    )
    fun <T: Any> bindRecyclerViewItems(
            view: RecyclerView,
            items: List<T>?,
            bindingMap: BindingMap?,
            stableId: Boolean?
    ) {

        if (items == null || bindingMap == null) {
            view.adapter = null
            ListenerUtil.trackListener(view, null, R.id.binding_recyclerview_items_adapter_info)
            return
        }

        var adapterInfo: AdapterInfo<T>? =
                ListenerUtil.getListener(view, R.id.binding_recyclerview_items_adapter_info)

        // Check not changed
        if (adapterInfo != null &&
                adapterInfo.items.get() === items &&
                adapterInfo.bindingMap.get() === bindingMap &&
                adapterInfo.adapter.get()?.let { it === view.adapter } == true) {
            return
        }

        val adapter = LastAdapter(items, stableId == true)
                .apply {
                    bindingMap.map.forEach {
                        map(it.key.java, it.value.layout, it.value.variable)
                    }
                }
                .into(view)

        adapterInfo = AdapterInfo(adapter, items, bindingMap)

        ListenerUtil.trackListener(view, adapterInfo, R.id.binding_recyclerview_items_adapter_info)

    }

    private data class AdapterInfo<T>(
            val adapter: WeakReference<LastAdapter>,
            val items: WeakReference<List<T>>,
            val bindingMap: WeakReference<BindingMap>
    ) {
        constructor(adapter: LastAdapter, items: List<T>, map: BindingMap):
                this(WeakReference(adapter) ,WeakReference(items), WeakReference(map))
    }

    class BindingMap (map: Map<KClass<out Any>, Item> = emptyMap()) {

        internal val map = map.toMutableMap()

        inline fun <reified T: Any> map(variable: Int, layout: Int): BindingMap {
            map(T::class, layout, variable)
            return this
        }

        fun <T: Any> map(clazz: KClass<T>, variable: Int, layout: Int): BindingMap {
            map[clazz] = Item(variable, layout)
            return this
        }

        data class Item(
                val variable: Int,
                val layout: Int
        )

    }

}