package com.github2136.datetime

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github2136.Util
import com.github2136.datalevelpicker.R
import java.util.*

/**
 * Created by yb on 2023/2/13
 * 时间单选
 * @param title 显示标题
 * @param startLimit 开始范围
 * @param endLimit 结束范围
 */
class TimePickerDialog(
    var title: String = "请选择时间", startLimit: String? = null, endLimit: String? = null, onConfirm: (date: String) -> Unit
) : DialogFragment(), View.OnClickListener, TimePicker.OnTimeChangedListener {
    private val className by lazy { javaClass.simpleName }
    private var dateCalender: Calendar
    private var startLimitCalendar: Calendar? = null
    private var endLimitCalendar: Calendar? = null
    private var onConfirm: ((data: String) -> Unit)? = null

    private lateinit var tvTitle: TextView
    private lateinit var tpDate: TimePicker
    private lateinit var btnConfirm: TextView
    private lateinit var btnCancel: TextView

    init {
        dateCalender = Calendar.getInstance()
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
        val view = inflater.inflate(R.layout.dialog_time_picker, container)
        tvTitle = view.findViewById(R.id.tvTitle)
        tpDate = view.findViewById(R.id.tpDate)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)
        tpDate.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
        tpDate.setIs24HourView(true)
        tpDate.hour = dateCalender.get(Calendar.HOUR_OF_DAY)
        tpDate.minute = dateCalender.get(Calendar.MINUTE)
        tpDate.setOnTimeChangedListener(this)
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        tvTitle.text = title
        return view
    }

    fun show(date: String?, manager: FragmentManager) {
        if (!this.isAdded) {
            if (date != null) {
                date.apply {
                    Util.str2date(this, Util.DATE_PATTERN_HM)?.apply {
                        dateCalender.time = this
                    }
                }
            } else {
                dateCalender = Calendar.getInstance()
            }
            super.show(manager, className)
        }
    }

    fun setLimit(startLimit: String?, endLimit: String?) {
        if (startLimit != null) {
            startLimit.apply {
                Util.str2date(this, Util.DATE_PATTERN_HM)?.apply {
                    startLimitCalendar?.also { it.time = this } ?: run { startLimitCalendar = Calendar.getInstance().also { it.time = this } }
                }
            }
        } else {
            startLimitCalendar = null
        }
        if (endLimit != null) {
            endLimit.apply {
                Util.str2date(this, Util.DATE_PATTERN_HM)?.apply {
                    endLimitCalendar?.also { it.time = this } ?: run { endLimitCalendar = Calendar.getInstance().also { it.time = this } }
                }
            }
        } else {
            endLimitCalendar = null
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnConfirm -> {
                onConfirm?.invoke(String.format("%02d:%02d", tpDate.hour, tpDate.minute))
                dismiss()
            }
            R.id.btnCancel -> {
                dismiss()
            }
        }
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        dateCalender.set(Calendar.HOUR_OF_DAY, hourOfDay)
        dateCalender.set(Calendar.MINUTE, minute)
        startLimitCalendar?.apply {
            if (dateCalender.time.before(time)) {
                view.hour = get(Calendar.HOUR_OF_DAY)
                view.minute = get(Calendar.MINUTE)
            }
        }
        endLimitCalendar?.apply {
            if (dateCalender.time.after(time)) {
                view.hour = get(Calendar.HOUR_OF_DAY)
                view.minute = get(Calendar.MINUTE)
            }
        }
    }
}