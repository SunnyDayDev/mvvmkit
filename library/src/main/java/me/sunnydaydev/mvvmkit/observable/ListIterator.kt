package me.sunnydaydev.mvvmkit.observable

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 18.08.2018.
 * mail: mail@sunnydaydev.me
 */

class MVVMIterator<T>(private val delegate: Delegate<T>): MutableIterator<T> {
    
    interface Delegate<T> {
        val size: Int
        operator fun get(index: Int): T
        fun remove(index: Int)
    } 

    private var index = -1

    override fun hasNext(): Boolean = delegate.size != 0 && index < delegate.size - 1

    override fun next(): T = delegate[++index]

    @Deprecated(message = "Merged delegate immutable", level = DeprecationLevel.HIDDEN)
    override fun remove() = delegate.remove(index)

}

class MVVMListIterator<T>(val list: Delegate<T>, startIndex: Int = -1): MutableListIterator<T> {

    interface Delegate<T>: MVVMIterator.Delegate<T> {
        operator fun set(index: Int, value: T)
        fun add(index: Int, element: T)
    }

    private var index = startIndex

    override fun hasNext(): Boolean = list.size != 0 && nextIndex() <= list.size - 1

    override fun nextIndex(): Int = index + 1

    override fun next(): T = list[++index]

    override fun hasPrevious(): Boolean = list.size != 0 && previousIndex() >= 0

    override fun previousIndex(): Int = index - 1

    override fun previous(): T = list[--index]

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun add(element: T) = list.add(index, element)

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun remove() = list.remove(index)

    @Deprecated(message = "Merged list immutable", level = DeprecationLevel.HIDDEN)
    override fun set(element: T) = list.set(index, element)

} 