package com.github2136.datetime

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
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
 * 日期时间范围选择
 * @param title 显示标题
 * @param startLimit 开始范围
 * @param endLimit 结束范围
 */
class DateTimeRangPickerDialog constructor(
    var title: String = "请选择日期时间范围", startLimit: String? = null, endLimit: String? = null, onConfirm: (start: String, end: String) -> Unit
) : DialogFragment(), View.OnClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    private val className by lazy { javaClass.simpleName }
    private val startCalendar: Calendar = Calendar.getInstance()
    private val endCalendar: Calendar = Calendar.getInstance()
    private val startLimitCalendar: Calendar = Calendar.getInstance()
    private val endLimitCalendar: Calendar = Calendar.getInstance()
    private var onConfirm: ((start: String, end: String) -> Unit)? = null

    private lateinit var tvTitle: TextView
    private lateinit var btnConfirm: TextView
    private lateinit var btnCancel: TextView
    private lateinit var btnStartDate: MaterialButton
    private lateinit var btnStartTime: MaterialButton
    private lateinit var btnEndDate: MaterialButton
    private lateinit var btnEndTime: MaterialButton
    private lateinit var dpDate: DatePicker
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
        val view = inflater.inflate(R.layout.dialog_date_time_rang_picker, container)

        tvTitle = view.findViewById(R.id.tvTitle)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnStartDate = view.findViewById(R.id.btnStartDate)
        btnStartTime = view.findViewById(R.id.btnStartTime)
        btnEndDate = view.findViewById(R.id.btnEndDate)
        btnEndTime = view.findViewById(R.id.btnEndTime)
        dpDate = view.findViewById(R.id.dpDate)
        tpTime = view.findViewById(R.id.tpTime)
        //禁止编辑
        dpDate.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS
        tpTime.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
        tpTime.setIs24HourView(true)
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnStartDate.setOnClickListener(this)
        btnStartTime.setOnClickListener(this)
        btnEndDate.setOnClickListener(this)
        btnEndTime.setOnClickListener(this)

        tvTitle.text = title
        btnStartDate.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_YMD)
        btnStartTime.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_HM)
        btnEndDate.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_YMD)
        btnEndTime.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_HM)
        btnStartDate.isChecked = true
        btnStartTime.isChecked = false
        btnEndDate.isChecked = false
        btnEndTime.isChecked = false
        setDpDate()
        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnStartDate -> {
                if (btnStartDate.isChecked) {
                    btnStartTime.isChecked = false
                    btnEndDate.isChecked = false
                    btnEndTime.isChecked = false
                    setDpDate()
                    dpDate.visibility = View.VISIBLE
                    tpTime.visibility = View.GONE
                } else {
                    btnStartDate.isChecked = true
                }
            }
            R.id.btnEndDate -> {
                if (btnEndDate.isChecked) {
                    btnStartDate.isChecked = false
                    btnStartTime.isChecked = false
                    btnEndTime.isChecked = false
                    setDpDate()
                    dpDate.visibility = View.VISIBLE
                    tpTime.visibility = View.GONE
                } else {
                    btnEndDate.isChecked = true
                }
            }
            R.id.btnStartTime -> {
                if (btnStartTime.isChecked) {
                    btnStartDate.isChecked = false
                    btnEndDate.isChecked = false
                    btnEndTime.isChecked = false
                    tpTime.setOnTimeChangedListener(null)
                    tpTime.hour = startCalendar.get(Calendar.HOUR_OF_DAY)
                    tpTime.minute = startCalendar.get(Calendar.MINUTE)
                    tpTime.setOnTimeChangedListener(this)
                    dpDate.visibility = View.GONE
                    tpTime.visibility = View.VISIBLE
                } else {
                    btnStartTime.isChecked = true
                }
            }
            R.id.btnEndTime -> {
                if (btnEndTime.isChecked) {
                    btnStartDate.isChecked = false
                    btnStartTime.isChecked = false
                    btnEndDate.isChecked = false
                    tpTime.setOnTimeChangedListener(null)
                    tpTime.hour = endCalendar.get(Calendar.HOUR_OF_DAY)
                    tpTime.minute = endCalendar.get(Calendar.MINUTE)
                    tpTime.setOnTimeChangedListener(this)
                    dpDate.visibility = View.GONE
                    tpTime.visibility = View.VISIBLE
                } else {
                    btnEndTime.isChecked = true
                }
            }
            R.id.btnConfirm -> {
                onConfirm?.invoke(Util.date2str(startCalendar.time, Util.DATE_PATTERN_YMDHM), Util.date2str(endCalendar.time, Util.DATE_PATTERN_YMDHM))
                dismiss()
            }
            R.id.btnCancel -> {
                dismiss()
            }
        }
    }

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (btnStartDate.isChecked) {
            startCalendar.set(year, monthOfYear, dayOfMonth)
            if (startCalendar.time.before(startLimitCalendar.time)) {
                startCalendar.time = startLimitCalendar.time
                btnStartTime.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_HM)
            }
            if (startCalendar.time.after(endCalendar.time)) {
                startCalendar.time = endCalendar.time
                btnStartTime.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_HM)
            }
            btnStartDate.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_YMD)
        } else if (btnEndDate.isChecked) {
            endCalendar.set(year, monthOfYear, dayOfMonth)
            if (endCalendar.time.after(endLimitCalendar.time)) {
                endCalendar.time = endLimitCalendar.time
                btnEndTime.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_HM)
            }
            if (endCalendar.time.before(startCalendar.time)) {
                endCalendar.time = startCalendar.time
                btnEndTime.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_HM)
            }
            btnEndDate.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_YMD)
        }
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

    override fun onDismiss(dialog: DialogInterface) {
        btnStartDate.isChecked = true
        btnStartTime.isChecked = false
        btnEndDate.isChecked = false
        btnEndTime.isChecked = false
        super.onDismiss(dialog)
    }

    /**
     * 设置控件时间及限制范围
     */
    private fun setDpDate() {
        if (btnStartDate.isChecked) {
            dpDate.init(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH), this)
            dpDate.minDate = startLimitCalendar.timeInMillis
            dpDate.maxDate = endCalendar.timeInMillis
        } else if (btnEndDate.isChecked) {
            dpDate.init(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH), this)
            dpDate.minDate = startCalendar.timeInMillis
            dpDate.maxDate = endLimitCalendar.timeInMillis
        }
    }

    fun setLimit(startLimit: String?, endLimit: String?) {
        var startLimit = startLimit
        var endLimit = endLimit
        if (startLimit != null && endLimit != null) {
            val s = Util.str2date(startLimit, Util.DATE_PATTERN_YMDHM)
            val e = Util.str2date(endLimit, Util.DATE_PATTERN_YMDHM)
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
                Util.str2date(this, Util.DATE_PATTERN_YMDHM)?.apply {
                    startLimitCalendar.time = this
                }
            }
        } else {
            startLimitCalendar.timeInMillis = 0
        }
        if (endLimit != null) {
            endLimit.apply {
                Util.str2date(this, Util.DATE_PATTERN_YMDHM)?.apply {
                    endLimitCalendar.time = this
                }
            }
        } else {
            endLimitCalendar.timeInMillis = Long.MAX_VALUE
        }
    }

    fun show(start: String?, end: String?, manager: FragmentManager) {
        if (!this.isAdded) {
            var start = start
            var end = end
            if (start != null && end != null) {
                val s = Util.str2date(start, Util.DATE_PATTERN_YMDHM)
                val e = Util.str2date(end, Util.DATE_PATTERN_YMDHM)
                if (s != null && e != null) {
                    if (s.time > e.time) {
                        //开始时间大于结束时间
                        start = null
                        end = null
                    }
                }
            }
            val startTemp = if (start != null) {
                Util.str2date(start, Util.DATE_PATTERN_YMDHM) ?: Date()
            } else {
                Date()
            }
            if (startTemp.before(startLimitCalendar.time) || startTemp.after(endLimitCalendar.time)) {
                startCalendar.time = startLimitCalendar.time
            } else {
                startCalendar.time = startTemp
            }

            val endTemp = if (end != null) {
                Util.str2date(end, Util.DATE_PATTERN_YMDHM) ?: Date()
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