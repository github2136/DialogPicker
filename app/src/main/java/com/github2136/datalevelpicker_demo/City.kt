package com.github2136.datalevelpicker_demo

import com.github2136.datalevelpicker.IDataLevel

/**
 * Created by YB on 2022/12/6
 */
data class City(var areaCode: String, var name: String, var citys: MutableList<City>?) : IDataLevel {
    override fun getId() = areaCode

    override fun getText() = name

    override fun getChild() = citys as MutableList<IDataLevel>?

}