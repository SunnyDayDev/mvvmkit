package me.sunnydaydev.mvvmkit.observable

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 15.08.2018.
 * mail: mail@sunnydaydev.me
 */
 
class OptionalMVVMListItem<T> {

    internal val list: MVVMList<T> = MVVMArrayList()

    var item: T?

        @Synchronized
        get() =
            if (list.isEmpty()) null
            else list[0]

        @Synchronized
        set(value) {

            if (value === item) return

            if (value == null) {
                if (list.isNotEmpty()) list.removeAt(0)
            } else {
                if (list.isEmpty()) list.add(value)
                else list[0] = value
            }

        }

}