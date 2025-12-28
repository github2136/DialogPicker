package com.github2136.datalevelpicker

/**
 * Created by YB on 2022/12/5
 * 级联菜单
 */
abstract class IDataLevel {
    /**
     * 唯一id
     */
    abstract fun getId(): String
    /**
     * 显示的文字
     */
    abstract fun getText(): String
    /**
     * 下级选项
     */
    abstract fun getChild(): MutableList<out IDataLevel>?

    /**
     * 延迟数据
     */
    open fun setChild(data: MutableList<IDataLevel>?) {}
    open var success: Boolean = false
}