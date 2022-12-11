package com.github2136.datalevelpicker

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by YB on 2022/12/6
 */
class DataLevelPickerAdapter(protected var list: MutableList<IDataLevel>? = null) : RecyclerView.Adapter<VH>() {
    protected lateinit var mLayoutInflater: LayoutInflater

    var selectId = "" //选中的项
    private var selectColor: Int = 0 //选中颜色
    private var unSelectColor: Int = 0 //未选中颜色

    protected var itemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        selectColor = parent.context.resources.getColor(R.color.colorDlpPrimary, null)
        unSelectColor = parent.context.resources.getColor(R.color.colorDlpText, null)

        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val v = mLayoutInflater.inflate(R.layout.item_dlp, parent, false)
        return VH(v, itemClickListener)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        list?.get(position)?.let { item ->
            holder.getView<CheckedTextView>(R.id.ctvItem)?.apply {
                text = item.getText()
                if (selectId == item.getId()) {
                    setTextColor(selectColor)
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dlp_check, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    setTextColor(unSelectColor)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    fun setOnItemClickListener(itemClickListener: (position: Int) -> Unit) {
        this.itemClickListener = itemClickListener
    }

    fun getItem(position: Int): IDataLevel? {
        return list?.get(position)
    }

    fun setData(list: MutableList<IDataLevel>) {
        this.list = list
        notifyDataSetChanged()
    }
}

class VH(itemView: View, val itemClickListener: ((Int) -> Unit)? = null) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private var views: SparseArray<View> = SparseArray()

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (itemClickListener != null) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.invoke(position)
            }
        }
    }

    fun <T : View> getView(@IdRes id: Int): T? {
        var v: View? = views.get(id)
        if (v != null) {
            return v as T
        }
        v = itemView.findViewById(id)
        if (v == null) {
            return null
        }
        views.put(id, v)
        return v as T
    }
}