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
 * 日期时间单选
 * @param title 显示标题
 * @param startLimit 开始范围
 * @param endLimit 结束范围
 */
class DateTimePickerDialog constructor(
    var title: String = "请选择日期时间", startLimit: String? = null, endLimit: String? = null, onConfirm: (date: String) -> Unit
) : DialogFragment(), View.OnClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    private val className by lazy { javaClass.simpleName }
    private val dateTimeCalendar: Calendar = Calendar.getInstance()
    private val startLimitCalendar: Calendar = Calendar.getInstance()
    private val endLimitCalendar: Calendar = Calendar.getInstance()
    private var onConfirm: ((date: String) -> Unit)? = null

    private lateinit var tvTitle: TextView
    private lateinit var btnConfirm: TextView
    private lateinit var btnCancel: TextView
    private lateinit var btnDate: MaterialButton
    private lateinit var btnTime: MaterialButton
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
        val view = inflater.inflate(R.layout.dialog_date_time_picker, container)

        tvTitle = view.findViewById(R.id.tvTitle)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnDate = view.findViewById(R.id.btnDate)
        btnTime = view.findViewById(R.id.btnTime)
        dpDate = view.findViewById(R.id.dpDate)
        tpTime = view.findViewById(R.id.tpTime)
        //禁止编辑
        dpDate.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS
        tpTime.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
        tpTime.setIs24HourView(true)
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnDate.setOnClickListener(this)
        btnTime.setOnClickListener(this)
        dpDate.init(dateTimeCalendar.get(Calendar.YEAR), dateTimeCalendar.get(Calendar.MONTH), dateTimeCalendar.get(Calendar.DAY_OF_MONTH), this)
        tvTitle.text = title
        btnDate.text = Util.date2str(dateTimeCalendar.time, Util.DATE_PATTERN_YMD)
        btnTime.text = Util.date2str(dateTimeCalendar.time, Util.DATE_PATTERN_HM)
        btnDate.isChecked = true
        btnTime.isChecked = false
        dpDate.minDate = startLimitCalendar.timeInMillis
        dpDate.maxDate = endLimitCalendar.timeInMillis
        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnDate -> {
                if (btnDate.isChecked) {
                    btnTime.isChecked = false
                    dpDate.visibility = View.VISIBLE
                    tpTime.visibility = View.GONE
                } else {
                    btnDate.isChecked = true
                }
            }
            R.id.btnTime -> {
                if (btnTime.isChecked) {
                    btnDate.isChecked = false
                    tpTime.setOnTimeChangedListener(null)
                    tpTime.hour = dateTimeCalendar.get(Calendar.HOUR_OF_DAY)
                    tpTime.minute = dateTimeCalendar.get(Calendar.MINUTE)
                    tpTime.setOnTimeChangedListener(this)
                    dpDate.visibility = View.GONE
                    tpTime.visibility = View.VISIBLE
                } else {
                    btnTime.isChecked = true
                }
            }
            R.id.btnConfirm -> {
                onConfirm?.invoke(Util.date2str(dateTimeCalendar.time, Util.DATE_PATTERN_YMDHM))
                dismiss()
            }
            R.id.btnCancel -> {
                dismiss()
            }
        }
    }

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (btnDate.isChecked) {
            dateTimeCalendar.set(year, monthOfYear, dayOfMonth)
            if (dateTimeCalendar.time.before(startLimitCalendar.time)) {
                dateTimeCalendar.time = startLimitCalendar.time
                btnTime.text = Util.date2str(dateTimeCalendar.time, Util.DATE_PATTERN_HM)
            }
            if (dateTimeCalendar.time.after(endLimitCalendar.time)) {
                dateTimeCalendar.time = endLimitCalendar.time
                btnTime.text = Util.date2str(dateTimeCalendar.time, Util.DATE_PATTERN_HM)
            }
            btnDate.text = Util.date2str(dateTimeCalendar.time, Util.DATE_PATTERN_YMD)
        }
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (btnTime.isChecked) {
            dateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            dateTimeCalendar.set(Calendar.MINUTE, minute)
            if (dateTimeCalendar.time.before(startLimitCalendar.time)) {
                view.hour = startLimitCalendar.get(Calendar.HOUR_OF_DAY)
                view.minute = startLimitCalendar.get(Calendar.MINUTE)
            }
            if (dateTimeCalendar.time.after(endLimitCalendar.time)) {
                view.hour = endLimitCalendar.get(Calendar.HOUR_OF_DAY)
                view.minute = endLimitCalendar.get(Calendar.MINUTE)
            }
            dateTimeCalendar.set(Calendar.HOUR_OF_DAY, view.hour)
            dateTimeCalendar.set(Calendar.MINUTE, view.minute)
            btnTime.text = Util.date2str(dateTimeCalendar.time, Util.DATE_PATTERN_HM)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        btnDate.isChecked = true
        btnTime.isChecked = false
        super.onDismiss(dialog)
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

    fun show(date: String?, manager: FragmentManager) {
        if (!this.isAdded) {
            val temp = if (date != null) {
                Util.str2date(date, Util.DATE_PATTERN_YMDHM) ?: Date()
            } else {
                Date()
            }
            if (temp.before(startLimitCalendar.time) || temp.after(endLimitCalendar.time)) {
                dateTimeCalendar.time = startLimitCalendar.time
            } else {
                dateTimeCalendar.time = temp
            }
            super.show(manager, className)
        }
    }
}