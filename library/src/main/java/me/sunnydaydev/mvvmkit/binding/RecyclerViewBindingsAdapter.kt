package me.sunnydaydev.mvvmkit.binding

import androidx.core.view.get
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
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

    // region Commands

    @JvmStatic
    @BindingAdapter("scrollToPosition")
    fun bindScrollToPosition(view: RecyclerView, position: Command<Int>) {
        position.handle(view::scrollToPosition)
    }

    // endregion

    // region Listeners



    // endregion

    // region Items

    @JvmStatic
    @BindingAdapter(
            value = [
                "recyclerView_items",
                "recyclerView_itemsLayoutMap",
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
                adapterInfo.bindingMap.get() == bindingMap &&
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

    @JvmStatic
    @BindingAdapter(
            value = [
                "recyclerView_onItemMoved",
                "recyclerView_onItemActionStateChanged",
                "recyclerView_canItemDropOver"
            ],
            requireAll = false
    )
    fun bindItemMovedListener(
            view: RecyclerView,
            movedCallback: OnTouchItemMovedCallback?,
            actionStateCallback: OnTouchItemActionStateCallback?,
            dropOverCallback: OnTouchItemCanDropOverCallback?
    ) {

        ListenerUtil.getListener<ItemTouchHelper>(
                view, R.id.binding_recyclerview_touch_item_helper
        )?.attachToRecyclerView(null)
        ListenerUtil.trackListener(view, null, R.id.binding_recyclerview_touch_item_helper)

        if (movedCallback == null && actionStateCallback == null && dropOverCallback == null) {
            return
        }

        val itemTouchCallback = object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return movedCallback?.onItemMoved(viewHolder.adapterPosition, target.adapterPosition) ?: true
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {

            }

            override fun canDropOver(recyclerView: RecyclerView, current: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return dropOverCallback?.canDropOver(current.adapterPosition, target.adapterPosition) ?: true
            }

            override fun isLongPressDragEnabled(): Boolean = true

            override fun isItemViewSwipeEnabled(): Boolean = false

            override fun onSelectedChanged(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                viewHolder ?: return

                actionStateCallback?.onItemSelectionChanged(
                        viewHolder.adapterPosition,
                        ItemTouchActionState.parse(actionState)
                )

            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                actionStateCallback?.onItemSelectionChanged(
                        viewHolder.adapterPosition,
                        ItemTouchActionState.IDLE
                )

            }

        }

        val helper = ItemTouchHelper(itemTouchCallback)

        ListenerUtil.trackListener(view, helper, R.id.binding_recyclerview_touch_item_helper)

        helper.attachToRecyclerView(view)

    }

    // endregion

    // region ItemDecoration

    @JvmStatic
    @BindingAdapter("itemDecoration")
    fun bindItemDecoration(view: RecyclerView, decoration: RecyclerView.ItemDecoration) {
        // TODO: remove previous
        view.addItemDecoration(decoration)
    }

    @JvmStatic
    @BindingAdapter("itemDecorations")
    fun bindItemDecorations(view: RecyclerView, decoration: List<RecyclerView.ItemDecoration>) {
        // TODO: remove previous
        decoration.forEach(view::addItemDecoration)
    }

    // endregion

    @JvmStatic
    @BindingAdapter(
            value = [
                "recyclerView_onVisibleItemsPositionsChanged",
                "recyclerView_onFirstVisibleItemsPositionsChanged",
                "recyclerView_onLastVisibleItemPositionsChanged"
            ],
            requireAll = false
    )
    fun bindLastVisiblePosition(view: RecyclerView,
                                callback: AdapterPositionListener.Callback?,
                                first: AdapterPositionListener.SingleCallback?,
                                last: AdapterPositionListener.SingleCallback?) {

        val current: AdapterPositionListener? =
                ListenerUtil.getListener(view, R.id.binding_recyclerview_binding_visible_position)

        if (current?.isSame(callback, first, last) == true) return

        current?.also(view::removeOnScrollListener)

        if (callback == null) return

        val newListener = AdapterPositionListener(callback, first, last)

        view.addOnScrollListener(newListener)

        ListenerUtil.trackListener(view, newListener, R.id.binding_recyclerview_binding_visible_position)

    }

    private val RecyclerView.firstVisibleItemPosition: Int get() {
        if (childCount == 0) return -1
        val firstView = this[0]
        return getChildAdapterPosition(firstView)
    }

    private val RecyclerView.lastVisibleItemPosition: Int get() {
        if (childCount == 0) return -1
        val lastView = this[childCount - 1]
        return getChildAdapterPosition(lastView)
    }

    class AdapterPositionListener(
            private val callback: Callback?,
            private val firstCallback: AdapterPositionListener.SingleCallback?,
            private val lastCallback: AdapterPositionListener.SingleCallback?
    ): RecyclerView.OnScrollListener() {

        private var handledFirst: Int? = null
        private var handledLast: Int? = null

        fun isSame(callback: Callback?,
                   first: AdapterPositionListener.SingleCallback?,
                   last: AdapterPositionListener.SingleCallback?): Boolean =
                callback == this.callback && first == firstCallback && last == lastCallback

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            val first = recyclerView.firstVisibleItemPosition
            val last = recyclerView.lastVisibleItemPosition

            val firstChanged = handledFirst != first
            val lastChanged = handledLast != last

            if (firstCallback != null && firstChanged) {
                recyclerView.post {
                    firstCallback.onItemVisiblePositionChanged(first)
                }
            }

            if (lastCallback != null && lastChanged) {
                recyclerView.post {
                    lastCallback.onItemVisiblePositionChanged(last)
                }
            }

            if (callback != null && (firstChanged || lastChanged)) {
                recyclerView.post {
                    callback.onItemsVisiblePositionChanged(first, last)
                }
            }

            if (firstChanged) handledFirst = first
            if (lastChanged) handledLast = last

        }

        interface Callback {

            fun onItemsVisiblePositionChanged(first: Int, last: Int)

        }

        interface SingleCallback {

            fun onItemVisiblePositionChanged(position: Int)

        }

    }

    // region Classes

    interface OnTouchItemMovedCallback {

        fun onItemMoved(fromIndex: Int, toIndex: Int): Boolean

    }

    interface OnTouchItemActionStateCallback {

        fun onItemSelectionChanged(position: Int, type: ItemTouchActionState)

    }

    interface OnTouchItemCanDropOverCallback {

        fun canDropOver(current: Int, target: Int): Boolean

    }

    enum class ItemTouchActionState {

        DRAG, SWIPE, IDLE;

        internal companion object {

            fun parse(action: Int): ItemTouchActionState = when(action) {
                ItemTouchHelper.ACTION_STATE_DRAG -> DRAG
                ItemTouchHelper.ACTION_STATE_SWIPE -> SWIPE
                else -> IDLE
            }

        }

    }

    private data class AdapterInfo<T>(
            val adapter: WeakReference<LastAdapter>,
            val items: WeakReference<List<T>>,
            val bindingMap: WeakReference<BindingMap>
    ) {
        constructor(adapter: LastAdapter, items: List<T>, map: BindingMap):
                this(WeakReference(adapter) ,WeakReference(items), WeakReference(map))
    }

    class BindingMap (private val id: Int, map: Map<KClass<out Any>, Item> = emptyMap()) {

        internal val map = map.toMutableMap()

        inline fun <reified T: Any> map(variable: Int, layout: Int): BindingMap {
            map(T::class, variable = variable, layout = layout)
            return this
        }

        fun <T: Any> map(clazz: KClass<T>, variable: Int, layout: Int): BindingMap {
            map[clazz] = Item(variable = variable, layout = layout)
            return this
        }

        data class Item(
                val variable: Int,
                val layout: Int
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            other as? BindingMap ?: return false
            return id == other.id
        }

        override fun hashCode(): Int = id.hashCode()

    }

    // endregion

}