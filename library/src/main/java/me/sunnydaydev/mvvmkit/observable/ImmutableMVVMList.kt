package me.sunnydaydev.mvvmkit.observable

/**
 * Created by sunny on 19.08.2018.
 * mail: mail@sunnydaydev.me
 */

interface ImmutableMVVMList<T>: MVVMList<T> {

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun add(element: T): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun add(index: Int, element: T) = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun addAll(index: Int, elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun addAll(elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun clear() = notSupported()

    override fun iterator(): ImmutableIterator<T>

    override fun listIterator(): ImmutableListIterator<T>

    override fun listIterator(index: Int): ImmutableListIterator<T>

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun remove(element: T): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun removeAll(elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun removeAt(index: Int): T = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun retainAll(elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun set(index: Int, element: T): T = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun move(fromIndex: Int, toIndex: Int) = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun swap(fromIndex: Int, toIndex: Int) = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun setAll(items: Collection<T>) = notSupported()

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun setAll(items: Collection<T>, startIndex: Int, count: Int) = notSupported()

    class ImmutableIterator<T>(val list: List<T>): MutableIterator<T> {

        private var index = -1

        override fun hasNext(): Boolean = list.isNotEmpty() && index < list.size - 1

        override fun next(): T = list[++index]

        @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
        override fun remove() = notSupported()

    }

    class ImmutableListIterator<T>(val list: List<T>, startIndex: Int): MutableListIterator<T> {

        private var index = startIndex

        override fun hasNext(): Boolean = list.isNotEmpty() && nextIndex() <= list.lastIndex

        override fun nextIndex(): Int = index + 1

        override fun next(): T = list[++index]

        override fun hasPrevious(): Boolean = list.isNotEmpty() && previousIndex() >= 0

        override fun previousIndex(): Int = index - 1

        override fun previous(): T = list[--index]

        @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
        override fun add(element: T) = notSupported()

        @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
        override fun remove() = notSupported()

        @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
        override fun set(element: T) = notSupported()

    }

    companion object {

        private fun notSupported(): Nothing = error("Not supported")

    }

}