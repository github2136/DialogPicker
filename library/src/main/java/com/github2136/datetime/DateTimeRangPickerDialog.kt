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
import kotlin.math.max
import kotlin.math.min

/**
 * Created by yb on 2023/2/13
 * 日期时间范围选择
 */
class DateTimeRangPickerDialog constructor(
    var title: String = "请选择时间范围", startLimit: String? = null, endLimit: String? = null, onConfirm: (start: String, end: String) -> Unit
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

    fun setLimit(startLimit: String?, endLimit: String?) {
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
            if (start != null) {
                startCalendar.apply {
                    time = Util.str2date(start, Util.DATE_PATTERN_YMDHM)
                }
            } else {
                startCalendar.time = Date()
            }
            if (end != null) {
                endCalendar.apply {
                    time = Util.str2date(end, Util.DATE_PATTERN_YMDHM)
                }
            } else {
                endCalendar.time = Date()
            }
            show(manager, className)
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
            // dpDate.init(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH), this)
            // dpDate.minDate = startLimitCalendar?.timeInMillis ?: 0
            // endLimitCalendar?.apply {
            //     dpDate.maxDate = min(this.timeInMillis, endCalendar.timeInMillis)
            // } ?: run {
            //     dpDate.maxDate = endCalendar.timeInMillis
            // }
        } else if (btnEndDate.isChecked) {
            // dpDate.init(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH), this)
            // startLimitCalendar?.apply {
            //     dpDate.minDate = max(this.timeInMillis, endCalendar.timeInMillis)
            // } ?: run {
            //     dpDate.minDate = startCalendar.timeInMillis
            // }
            // dpDate.maxDate = endLimitCalendar?.timeInMillis ?: Long.MAX_VALUE
        }
    }

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (btnStartDate.isChecked) {
            startCalendar.set(year, monthOfYear, dayOfMonth)
            btnStartDate.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_YMD)
        } else {
            endCalendar.set(year, monthOfYear, dayOfMonth)
            endCalendar
            btnEndDate.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_YMD)
        }
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (btnStartTime.isChecked) {
            startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            startCalendar.set(Calendar.MINUTE, minute)
            startLimitCalendar?.apply {
                if (startCalendar.time.before(time)) {
                    view.hour = get(Calendar.HOUR_OF_DAY)
                    view.minute = get(Calendar.MINUTE)
                }
            }
            val end = endLimitCalendar?.run {
                if (endCalendar.time.before(time)) {
                    endCalendar
                } else {
                    this
                }
            } ?: run { endCalendar }
            if (startCalendar.time.after(end.time)) {
                view.hour = end.get(Calendar.HOUR_OF_DAY)
                view.minute = end.get(Calendar.MINUTE)
            }
            startCalendar.set(Calendar.HOUR_OF_DAY, view.hour)
            startCalendar.set(Calendar.MINUTE, view.minute)
            btnStartTime.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_HM)
        } else {
            endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            endCalendar.set(Calendar.MINUTE, minute)
            endLimitCalendar?.apply {
                if (endCalendar.time.after(time)) {
                    view.hour = get(Calendar.HOUR_OF_DAY)
                    view.minute = get(Calendar.MINUTE)
                }
            }
            val start = startLimitCalendar?.run {
                if (startCalendar.time.after(time)) {
                    startCalendar
                } else {
                    this
                }
            } ?: run { startCalendar }
            if (endCalendar.time.before(start.time)) {
                view.hour = start.get(Calendar.HOUR_OF_DAY)
                view.minute = start.get(Calendar.MINUTE)
            }
            endCalendar.set(Calendar.HOUR_OF_DAY, view.hour)
            endCalendar.set(Calendar.MINUTE, view.minute)
            btnEndTime.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_HM)
        }
    }
}