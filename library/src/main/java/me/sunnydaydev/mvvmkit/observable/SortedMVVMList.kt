package me.sunnydaydev.mvvmkit.observable

import android.annotation.TargetApi
import android.os.Build
import me.sunnydaydev.mvvmkit.util.notSupportedOperation
import java.util.function.UnaryOperator

/**
 * Created by sunny on 10.06.2018.
 * mail: mail@sunnydaydev.me
 */

class SortedMVVMList<T> private constructor(
        private val executeSorting: SortedMVVMList<T>.() -> Unit
): MVVMArrayList<T>() {

    override fun add(element: T): Boolean {
        silent {
            super.add(element)
            executeSorting()
        }
        notifyInserted(indexOf(element), 1)
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {

        silent {
            super.addAll(elements)
            executeSorting()
        }

        elements.map { indexOf(it) }
                .sorted()
                .forEach { notifyInserted(it, 1) }

        return true
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun replaceAll(operator: UnaryOperator<T>) {

        silent {
            super.replaceAll(operator)
        }

        notifyChanged()

    }

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun setAll(items: Collection<T>) = notSupportedOperation()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun addAll(index: Int, elements: Collection<T>): Boolean = notSupportedOperation()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun add(index: Int, element: T) = notSupportedOperation()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun setAll(items: Collection<T>, startIndex: Int, count: Int) = notSupportedOperation()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun move(fromIndex: Int, toIndex: Int) = notSupportedOperation()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun retainAll(elements: Collection<T>): Boolean = notSupportedOperation()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun set(index: Int, element: T): T = notSupportedOperation()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun swap(fromIndex: Int, toIndex: Int) = notSupportedOperation()

    companion object {

        private const val NOT_SUPPORTED = "Operation not supported in sorted list."

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