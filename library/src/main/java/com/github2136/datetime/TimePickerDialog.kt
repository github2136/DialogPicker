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
    private val timeCalender: Calendar = Calendar.getInstance()
    private val startLimitCalendar: Calendar = Calendar.getInstance()
    private val endLimitCalendar: Calendar = Calendar.getInstance()
    private var onConfirm: ((data: String) -> Unit)? = null

    private lateinit var tvTitle: TextView
    private lateinit var tpTime: TimePicker
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
        val view = inflater.inflate(R.layout.dialog_time_picker, container)
        tvTitle = view.findViewById(R.id.tvTitle)
        tpTime = view.findViewById(R.id.tpTime)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)
        tpTime.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
        tpTime.setIs24HourView(true)
        tpTime.hour = timeCalender.get(Calendar.HOUR_OF_DAY)
        tpTime.minute = timeCalender.get(Calendar.MINUTE)
        tpTime.setOnTimeChangedListener(this)
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        tvTitle.text = title
        return view
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        timeCalender.set(Calendar.HOUR_OF_DAY, hourOfDay)
        timeCalender.set(Calendar.MINUTE, minute)
        if (timeCalender.time.before(startLimitCalendar.time)) {
            view.hour = startLimitCalendar.get(Calendar.HOUR_OF_DAY)
            view.minute = startLimitCalendar.get(Calendar.MINUTE)
        }
        if (timeCalender.time.after(endLimitCalendar.time)) {
            view.hour = endLimitCalendar.get(Calendar.HOUR_OF_DAY)
            view.minute = endLimitCalendar.get(Calendar.MINUTE)
        }
        timeCalender.set(Calendar.HOUR_OF_DAY, view.hour)
        timeCalender.set(Calendar.MINUTE, view.minute)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnConfirm -> {
                onConfirm?.invoke(Util.date2str(timeCalender.time, Util.DATE_PATTERN_HM))
                dismiss()
            }
            R.id.btnCancel -> {
                dismiss()
            }
        }
    }

    fun setLimit(startLimit: String?, endLimit: String?) {
        var startLimit = startLimit
        var endLimit = endLimit
        if (startLimit != null && endLimit != null) {
            val s = Util.str2date(startLimit, Util.DATE_PATTERN_HM)
            val e = Util.str2date(endLimit, Util.DATE_PATTERN_HM)
            if (s != null && e != null) {
                if (s.time > e.time) {
                    //开始时间大于结束时间
                    startLimit = null
                    endLimit = null
                }
            }
        }
        if (startLimit != null) {
            startLimit.apply {
                Util.str2date(this, Util.DATE_PATTERN_HM)?.apply {
                    startLimitCalendar.time = this
                }
            }
        } else {
            startLimitCalendar.timeInMillis = 0
        }
        if (endLimit != null) {
            endLimit.apply {
                Util.str2date(this, Util.DATE_PATTERN_HM)?.apply {
                    endLimitCalendar.time = this
                }
            }
        } else {
            endLimitCalendar.timeInMillis = Long.MAX_VALUE
        }
    }

    fun show(date: String?, manager: FragmentManager) {
        if (!this.isAdded) {
            val temp = if (date != null) {
                Util.str2date(date, Util.DATE_PATTERN_HM) ?: Date()
            } else {
                Date()
            }
            if (temp.before(startLimitCalendar.time) || temp.after(endLimitCalendar.time)) {
                timeCalender.time = startLimitCalendar.time
            } else {
                timeCalender.time = temp
            }
            super.show(manager, className)
        }
    }
}