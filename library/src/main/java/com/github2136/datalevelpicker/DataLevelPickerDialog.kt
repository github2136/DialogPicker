package com.github2136.datalevelpicker

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.CheckedTextView
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.github2136.Util

/**
 * Created by YB on 2022/12/5
 * 级联菜单选择
 * @param data 多级数据
 */
class DataLevelPickerDialog<T : IDataLevel> constructor(data: MutableList<T>, onConfirm: (data: MutableList<T>) -> Unit) : DialogFragment(), View.OnClickListener {
    private val className by lazy { javaClass.simpleName }
    private var dataLevel: MutableList<IDataLevel> = mutableListOf()
    private var selectData = mutableListOf<IDataLevel>() //选中的集合
    private var level = 0 //当前操作等级
    private var onConfirm: ((data: MutableList<T>) -> Unit)? = null
    private lateinit var hsvTitle: HorizontalScrollView //顶部选中滚动控件
    private lateinit var llTitle: LinearLayout //顶部选择
    private lateinit var rvList: RecyclerView //显示当前等级数据
    private lateinit var btnConfirm: TextView //确认按钮
    private lateinit var btnCancel: TextView //取消按钮
    private lateinit var adapter: DataLevelPickerAdapter

    init {
        dataLevel.addAll(data)
        this.onConfirm = onConfirm
    }

    fun show(data: MutableList<T>?, manager: FragmentManager) {
        if (!this.isAdded) {
            selectData.clear()
            data?.let {
                level = if (data.isEmpty()) 0 else data.lastIndex
                var list = dataLevel
                for (d in data) {
                    val sd = list.first { it.getId() == d.getId() }
                    selectData.add(sd)
                    sd.getChild()?.apply { list = this }
                }
            }
            show(manager, className)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.apply {
            setGravity(Gravity.BOTTOM)
            decorView.setPadding(0)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, Util.dp2px(350f))
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }
        val view = inflater.inflate(R.layout.dialog_level_picker, container)
        hsvTitle = view.findViewById(R.id.hsvTitle)
        llTitle = view.findViewById(R.id.llTitle)
        rvList = view.findViewById(R.id.rvList)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        if (selectData.isEmpty()) {
            btnConfirm.isEnabled = false
        }
        rvList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        llTitle.post {
            selectData.forEachIndexed { i, item ->
                addTitle(inflater, item, i)
            }
            setTitleCheck(llTitle, level)
            hsvTitle.postDelayed({ hsvTitle.fullScroll(HorizontalScrollView.FOCUS_RIGHT) }, 100)
        }
        if (selectData.isEmpty()) {
            adapter = DataLevelPickerAdapter(dataLevel)
            rvList.scrollToPosition(0)
        } else {
            var list = dataLevel
            for ((i, item) in selectData.withIndex()) {
                if (level == i) {
                    adapter = DataLevelPickerAdapter(list).apply { selectId = item.getId() }
                    rvList.scrollToPosition(list.indexOfFirst { it.getId() == item.getId() })
                } else {
                    item.getChild()?.apply { list = this }
                }
            }
        }
        adapter.setOnItemClickListener { position ->
            val item = adapter.getItem(position)!!
            btnConfirm.isEnabled = true
            if (level >= selectData.size) {
                //添加title
                selectData.add(item)
                addTitle(inflater, item, level)
                setTitleCheck(llTitle, level)
                hsvTitle.postDelayed({ hsvTitle.fullScroll(HorizontalScrollView.FOCUS_RIGHT) }, 100)
            } else {
                //替换
                (llTitle.getChildAt(level) as CheckedTextView).text = item.getText()
                selectData[level] = item
                val childCount = llTitle.childCount
                llTitle.removeViews(level + 1, childCount - (level + 1))
                selectData = selectData.subList(0, level + 1)
                setTitleCheck(llTitle, level)
                hsvTitle.postDelayed({ hsvTitle.fullScroll(HorizontalScrollView.FOCUS_RIGHT) }, 100)
            }
            item.getChild()?.apply {
                //展示下一级
                level++
                adapter.selectId = ""
                adapter.setData(this)
                rvList.scrollToPosition(0)
            } ?: let {
                //切换选中的最后一级
                adapter.selectId = item.getId()
                adapter.notifyDataSetChanged()
            }
        }
        rvList.adapter = adapter
        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ctvTop -> {
                val ctv = v as CheckedTextView
                val clickLevel = ctv.tag as Int
                level = clickLevel
                if (!v.isChecked) {
                    setTitleCheck(llTitle, clickLevel)
                    var list = dataLevel
                    for ((i, item) in selectData.withIndex()) {
                        if (clickLevel == i) {
                            adapter.selectId = item.getId()
                            adapter.setData(list)
                            rvList.scrollToPosition(list.indexOfFirst { it.getId() == item.getId() })
                        } else {
                            item.getChild()?.apply { list = this }
                        }
                    }
                }
            }
            R.id.btnConfirm -> {
                onConfirm?.invoke(selectData.subList(0, llTitle.children.last { (it as CheckedTextView).isChecked }.tag.toString().toInt() + 1).toMutableList() as MutableList<T>)
                dismiss()
            }
            R.id.btnCancel -> {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        selectData.clear()
        level = 0
    }
    /**
     * 添加标题
     */
    private fun addTitle(inflater: LayoutInflater, item: IDataLevel, level: Int) {
        val title = inflater.inflate(R.layout.item_dlp_top, llTitle, false) as CheckedTextView
        title.isChecked = false
        title.setOnClickListener(this)
        title.text = item.getText()
        title.tag = level
        llTitle.addView(title)
    }

    /**
     * 设置指定标题显示选中
     */
    private fun setTitleCheck(llTitle: LinearLayout, level: Int) {
        llTitle.children.forEachIndexed { index, view ->
            if (view is CheckedTextView) {
                view.isChecked = view.tag == level
            }
        }
    }
}