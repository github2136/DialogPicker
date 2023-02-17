package com.github2136.datetime

import android.content.DialogInterface
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
import com.google.android.material.button.MaterialButton
import java.util.*

/**
 * Created by yb on 2023/2/13
 * 时间范围选择
 * @param title 显示标题
 * @param startLimit 开始范围
 * @param endLimit 结束范围
 */
class TimeRangPickerDialog(
    var title: String = "请选择时间范围", startLimit: String? = null, endLimit: String? = null, onConfirm: (start: String, end: String) -> Unit
) : DialogFragment(), View.OnClickListener, TimePicker.OnTimeChangedListener {
    private val className by lazy { javaClass.simpleName }
    private val startCalendar: Calendar = Calendar.getInstance()
    private val endCalendar: Calendar = Calendar.getInstance()
    private val startLimitCalendar: Calendar = Calendar.getInstance()
    private val endLimitCalendar: Calendar = Calendar.getInstance()
    private var onConfirm: ((start: String, end: String) -> Unit)? = null

    private lateinit var tvTitle: TextView
    private lateinit var btnConfirm: TextView
    private lateinit var btnCancel: TextView
    private lateinit var btnStartTime: MaterialButton
    private lateinit var btnEndTime: MaterialButton
    private lateinit var tpTime: TimePicker

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
        val view = inflater.inflate(R.layout.dialog_time_rang_picker, container)

        tvTitle = view.findViewById(R.id.tvTitle)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnStartTime = view.findViewById(R.id.btnStartTime)
        btnEndTime = view.findViewById(R.id.btnEndTime)
        tpTime = view.findViewById(R.id.tpTime)
        //禁止编辑
        tpTime.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
        tpTime.setIs24HourView(true)
        tpTime.hour = startCalendar.get(Calendar.HOUR_OF_DAY)
        tpTime.minute = startCalendar.get(Calendar.MINUTE)
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnStartTime.setOnClickListener(this)
        btnEndTime.setOnClickListener(this)
        tvTitle.text = title
        btnStartTime.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_HM)
        btnEndTime.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_HM)
        btnStartTime.isChecked = true
        btnEndTime.isChecked = false
        tpTime.setOnTimeChangedListener(this)
        return view
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (btnStartTime.isChecked) {
            startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            startCalendar.set(Calendar.MINUTE, minute)
            if (startCalendar.time.before(startLimitCalendar.time)) {
                view.hour = startLimitCalendar.get(Calendar.HOUR_OF_DAY)
                view.minute = startLimitCalendar.get(Calendar.MINUTE)
            }
            if (startCalendar.time.after(endCalendar.time)) {
                view.hour = endCalendar.get(Calendar.HOUR_OF_DAY)
                view.minute = endCalendar.get(Calendar.MINUTE)
            }
            startCalendar.set(Calendar.HOUR_OF_DAY, view.hour)
            startCalendar.set(Calendar.MINUTE, view.minute)
            btnStartTime.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_HM)
        } else if (btnEndTime.isChecked) {
            endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            endCalendar.set(Calendar.MINUTE, minute)

            if (endCalendar.time.after(endLimitCalendar.time)) {
                view.hour = endLimitCalendar.get(Calendar.HOUR_OF_DAY)
                view.minute = endLimitCalendar.get(Calendar.MINUTE)
            }
            if (endCalendar.time.before(startCalendar.time)) {
                view.hour = startCalendar.get(Calendar.HOUR_OF_DAY)
                view.minute = startCalendar.get(Calendar.MINUTE)
            }
            endCalendar.set(Calendar.HOUR_OF_DAY, view.hour)
            endCalendar.set(Calendar.MINUTE, view.minute)
            btnEndTime.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_HM)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnStartTime -> {
                if (btnStartTime.isChecked) {
                    btnEndTime.isChecked = false
                    tpTime.hour = startCalendar.get(Calendar.HOUR_OF_DAY)
                    tpTime.minute = startCalendar.get(Calendar.MINUTE)
                } else {
                    btnStartTime.isChecked = true
                }
            }
            R.id.btnEndTime -> {
                if (btnEndTime.isChecked) {
                    btnStartTime.isChecked = false
                    tpTime.hour = endCalendar.get(Calendar.HOUR_OF_DAY)
                    tpTime.minute = endCalendar.get(Calendar.MINUTE)
                } else {
                    btnEndTime.isChecked = true
                }
            }
            R.id.btnConfirm -> {
                onConfirm?.invoke(Util.date2str(startCalendar.time, Util.DATE_PATTERN_HM), Util.date2str(endCalendar.time, Util.DATE_PATTERN_HM))
                dismiss()
            }
            R.id.btnCancel -> {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        btnStartTime.isChecked = true
        btnEndTime.isChecked = false
        super.onDismiss(dialog)
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

    fun show(start: String?, end: String?, manager: FragmentManager) {
        if (!this.isAdded) {
            val startTemp = if (start != null) {
                Util.str2date(start, Util.DATE_PATTERN_HM) ?: Date()
            } else {
                Date()
            }
            if (startTemp.before(startLimitCalendar.time) || startTemp.after(endLimitCalendar.time)) {
                startCalendar.time = startLimitCalendar.time
            } else {
                startCalendar.time = startTemp
            }

            val endTemp = if (end != null) {
                Util.str2date(end, Util.DATE_PATTERN_HM) ?: Date()
            } else {
                Date()
            }
            if (endTemp.before(startLimitCalendar.time) || endTemp.after(endLimitCalendar.time)) {
                endCalendar.time = startLimitCalendar.time
            } else {
                endCalendar.time = endTemp
            }
            show(manager, className)
        }
    }
}