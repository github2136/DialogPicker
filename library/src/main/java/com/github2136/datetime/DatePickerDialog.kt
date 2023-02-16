package com.github2136.datetime

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github2136.Util
import com.github2136.datalevelpicker.R
import java.util.*

/**
 * Created by yb on 2023/2/13
 * 日期单选
 * @param title 显示标题
 * @param startLimit 开始范围
 * @param endLimit 结束范围
 */
class DatePickerDialog constructor(
    var title: String = "请选择日期", startLimit: String? = null, endLimit: String? = null, onConfirm: (date: String) -> Unit
) : DialogFragment(), View.OnClickListener, DatePicker.OnDateChangedListener {
    private val className by lazy { javaClass.simpleName }
    private val dateCalender: Calendar = Calendar.getInstance()
    private val startLimitCalendar: Calendar = Calendar.getInstance()
    private val endLimitCalendar: Calendar = Calendar.getInstance()
    private var onConfirm: ((data: String) -> Unit)? = null

    private lateinit var tvTitle: TextView
    private lateinit var dpDate: DatePicker
    private lateinit var btnConfirm: TextView
    private lateinit var btnCancel: TextView

    init {
        setLimit(startLimit, endLimit)
        this.onConfirm = onConfirm
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.apply {
            setGravity(Gravity.BOTTOM)
            decorView.setPadding(0)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, Util.dp2px(350f))
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }
        val view = inflater.inflate(R.layout.dialog_date_picker, container)
        tvTitle = view.findViewById(R.id.tvTitle)
        dpDate = view.findViewById(R.id.dpDate)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)
        dpDate.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS

        dpDate.init(dateCalender.get(Calendar.YEAR), dateCalender.get(Calendar.MONTH), dateCalender.get(Calendar.DAY_OF_MONTH), this)
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        tvTitle.text = title

        dpDate.minDate = startLimitCalendar.timeInMillis
        dpDate.maxDate = endLimitCalendar.timeInMillis
        return view
    }

    fun show(date: String?, manager: FragmentManager) {
        if (!this.isAdded) {
            if (date != null) {
                date.apply {
                    Util.str2date(this, Util.DATE_PATTERN_YMD)?.apply {
                        dateCalender.time = this
                    }
                }
            } else {
                dateCalender.time = Date()
            }
            super.show(manager, className)
        }
    }

    fun setLimit(startLimit: String?, endLimit: String?) {
        if (startLimit != null) {
            startLimit.apply {
                Util.str2date(this, Util.DATE_PATTERN_YMD)?.apply {
                    startLimitCalendar.time = this
                }
            }
        } else {
            startLimitCalendar.timeInMillis = 0
        }
        if (endLimit != null) {
            endLimit.apply {
                Util.str2date(this, Util.DATE_PATTERN_YMD)?.apply {
                    endLimitCalendar.time = this
                }
            }
        } else {
            endLimitCalendar.timeInMillis = Long.MAX_VALUE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnConfirm -> {
                onConfirm?.invoke(Util.date2str(dateCalender.time, Util.DATE_PATTERN_YMD))
                dismiss()
            }
            R.id.btnCancel -> {
                dismiss()
            }
        }
    }

    override fun onDateChanged(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        dateCalender.set(Calendar.YEAR, year)
        dateCalender.set(Calendar.MONTH, monthOfYear)
        dateCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }
}