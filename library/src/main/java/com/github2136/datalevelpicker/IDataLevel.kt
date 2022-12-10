package com.github2136.datalevelpicker

/**
 * Created by YB on 2022/12/5
 * 级联菜单
 */
interface IDataLevel {
    fun getId(): String //唯一id
    fun getText(): String //显示的文字
    fun getChild(): MutableList<IDataLevel>? //下级选项
}