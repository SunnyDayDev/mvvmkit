package me.sunnydaydev.mvvmkit.observable

/**
 * Created by sunny on 10.06.2018.
 * mail: mail@sunnydaydev.me
 */

class SortedMVVMList<T>(private val comparator: Comparator<T>): MVVMArrayList<T>() {

    override fun add(element: T): Boolean {
        silent {
            super.add(element)
            sortWith(comparator)
        }
        notifyInserted(indexOf(element), 1)
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {

        silent {
            super.addAll(elements)
            sortWith(comparator)
        }

        elements.map { indexOf(it) }
                .sorted()
                .forEach { notifyInserted(it, 1) }

        return true
    }

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun setAll(items: Collection<T>) = notSupported()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun addAll(index: Int, elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun add(index: Int, element: T) = notSupported()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun setAll(items: Collection<T>, startIndex: Int, count: Int) = notSupported()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun move(fromIndex: Int, toIndex: Int) = notSupported()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun retainAll(elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun set(index: Int, element: T): T = notSupported()

    @Deprecated(message = NOT_SUPPORTED, level = DeprecationLevel.HIDDEN)
    override fun swap(fromIndex: Int, toIndex: Int) = notSupported()

    private fun notSupported(): Nothing = throw IllegalAccessException(NOT_SUPPORTED)

    companion object {

        private const val NOT_SUPPORTED = "Operation not supported in sorted list."

    }

}