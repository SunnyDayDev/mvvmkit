package me.sunnydaydev.mvvmkit.observable

import me.sunnydaydev.mvvmkit.util.notSupportedOperation

/**
 * Created by sunny on 19.08.2018.
 * mail: mail@sunnydaydev.me
 */

interface ImmutableMVVMList<T>: MVVMList<T> {

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun add(element: T): Boolean = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun add(index: Int, element: T) = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun addAll(index: Int, elements: Collection<T>): Boolean = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun addAll(elements: Collection<T>): Boolean = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun clear() = notSupportedOperation()

    override fun iterator(): MVVMIterator<T>

    override fun listIterator(): MVVMListIterator<T>

    override fun listIterator(index: Int): MVVMListIterator<T>

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun remove(element: T): Boolean = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun removeAll(elements: Collection<T>): Boolean = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun removeAt(index: Int): T = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun retainAll(elements: Collection<T>): Boolean = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun set(index: Int, element: T): T = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun move(fromIndex: Int, toIndex: Int) = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun swap(fromIndex: Int, toIndex: Int) = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun setAll(items: Collection<T>) = notSupportedOperation()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun setAll(items: Collection<T>, startIndex: Int, count: Int) = notSupportedOperation()

}