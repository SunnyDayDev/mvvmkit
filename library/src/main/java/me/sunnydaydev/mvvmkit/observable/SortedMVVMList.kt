package me.sunnydaydev.mvvmkit.observable

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import java.util.function.UnaryOperator

/**
 * Created by sunny on 10.06.2018.
 * mail: mail@sunnydaydev.me
 */

class SortedMVVMList<T> private constructor(
        private val executeSorting: SortedMVVMList<T>.() -> Unit
): MVVMArrayList<T>() {

    private var synchronizedSortingOperation = false

    @Synchronized
    override fun add(element: T): Boolean = silentWithSorting(
            action = { super.add(element) },
            notify = { notifyChanged() }
    )

    @Synchronized
    override fun addAll(elements: Collection<T>): Boolean = silentWithSorting(
            action = { super.addAll(elements) },
            notify = { notifyChanged() }
    )

    @TargetApi(Build.VERSION_CODES.N)
    @Synchronized
    override fun replaceAll(operator: UnaryOperator<T>) = silentWithSorting(
            action = { super.replaceAll(operator) },
            notify = { notifyChanged() }
    )

    @Synchronized
    override fun setAll(items: Collection<T>)  = silentWithSorting(
            action = { super.setAll(items) },
            notify = { notifyChanged() }
    )

    @Synchronized
    override fun addAll(index: Int, elements: Collection<T>): Boolean = silentWithSorting(
        action = { super.addAll(index, elements) },
        notify = { elements.forEach { item -> notifyInserted(indexOf(item), 1) } }
    )

    @Synchronized
    override fun add(index: Int, element: T) = silentWithSorting(
            action = { super.add(index, element) },
            notify = { notifyInserted(indexOf(element), 1) }
    )

    @Synchronized
    override fun setAll(items: Collection<T>, startIndex: Int, count: Int) = silentWithSorting(
            action = { super.setAll(items, startIndex, count) },
            notify = { notifyChanged() }
    )

    @Synchronized
    override fun retainAll(elements: Collection<T>): Boolean = silentWithSorting(
            action = { super.retainAll(elements) },
            notify = { notifyChanged() }
    )

    @Synchronized
    override fun set(index: Int, element: T): T = silentWithSorting(
            action = { super.set(index, element) },
            notify = {
                val newIndex = indexOf(element)
                notifyMoved(index, newIndex, 1)
                notifyChanged(index, 1)
            }
    )

    @SuppressLint("LogNotTimber")
    override fun move(fromIndex: Int, toIndex: Int) {
        Log.e("SortedMVVMList", "Method move ignored because list is sorted.")
    }

    @SuppressLint("LogNotTimber")
    override fun swap(fromIndex: Int, toIndex: Int) {
        Log.e("SortedMVVMList", "Method swap ignored because list is sorted.")
    }

    @Synchronized
    private fun <R> silentWithSorting(action: () -> R, notify: (R) -> Unit): R = silent {
        val result = silent(action)
        if (!synchronizedSortingOperation) {
            synchronizedSortingOperation = true
            executeSorting()
            synchronizedSortingOperation = false
            notify(result)
        }
        result
    }

    companion object {

        fun <T> create(comparator: Comparator<T>) = SortedMVVMList<T> { sortWith(comparator) }

        fun <T> create(compare: (T, T) -> Int) = create(Comparator(compare))

        fun <T, K: Comparable<K>> create(
                descending: Boolean = false,
                keySelector: (T) -> K
        ): SortedMVVMList<T> =
                if (descending) SortedMVVMList { sortByDescending(keySelector) }
                else SortedMVVMList { sortBy(keySelector) }

    }

}