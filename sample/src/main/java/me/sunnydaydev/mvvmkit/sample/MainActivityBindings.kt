package me.sunnydaydev.mvvmkit.sample

import me.sunnydaydev.mvvmkit.binding.RecyclerViewBindingsAdapter
import me.sunnydaydev.mvvmkit.sample.vm.BlackViewModel
import me.sunnydaydev.mvvmkit.sample.vm.BlueViewModel
import me.sunnydaydev.mvvmkit.sample.vm.GreenViewModel
import me.sunnydaydev.mvvmkit.sample.vm.OrangeViewModel

/**
 * Created by sunny on 08.06.2018.
 * mail: mail@sunnydaydev.me
 */

object MainActivityBindings {

    @JvmStatic
    val colorsMap = RecyclerViewBindingsAdapter.BindingMap(1)
            .map<BlackViewModel>(BR.vm, R.layout.black_list_item_layout)
            .map<OrangeViewModel>(BR.vm, R.layout.orange_list_item_layout)
            .map<GreenViewModel>(BR.vm, R.layout.green_list_item_layout)
            .map<BlueViewModel>(BR.vm, R.layout.blue_list_item_layout)

}