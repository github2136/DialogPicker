package com.github2136.datalevelpicker

/**
 * Created by YB on 2022/12/5
 * 级联菜单
 */
interface IDataLevel {
    /**
     * 唯一id
     */
    fun getId(): String
    /**
     * 显示的文字
     */
    fun getText(): String
    /**
     * 下级选项
     */
    fun getChild(): MutableList<IDataLevel>?
}