package com.github2136.datalevelpicker_demo

import com.github2136.datalevelpicker.IDataLevel

/**
 * Created by 44569 on 2023/12/28
 */
data class AreaCode(
    var ParentCode: String,
    var AreaCode: String,
    var AreaName: String,
    var Level: Int,
    var Type: String,
    var Sort: Int
) : IDataLevel {
    override fun getId() = AreaCode

    override fun getText() = AreaName
    var next: MutableList<AreaCode>? = null
    override fun getChild(): MutableList<AreaCode>? {
        return next
    }
}
