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
import kotlin.math.max

/**
 * Created by yb on 2023/2/13
 * 时间范围选择
 */
class TimeRangPickerDialog(
    var title: String = "请选择时间范围", startLimit: String? = null, endLimit: String? = null, onConfirm: (start: String, end: String) -> Unit
) : DialogFragment(), View.OnClickListener, TimePicker.OnTimeChangedListener {
    private val className by lazy { javaClass.simpleName }
    private lateinit var startCalendar: Calendar
    private lateinit var endCalendar: Calendar

    private var startLimitCalendar: Calendar? = null
    private var endLimitCalendar: Calendar? = null
    private var onConfirm: ((start: String, end: String) -> Unit)? = null

    private lateinit var tvTitle: TextView
    private lateinit var btnConfirm: TextView
    private lateinit var btnCancel: TextView
    private lateinit var btnStartDate: MaterialButton
    private lateinit var btnEndDate: MaterialButton
    private lateinit var tpDate: TimePicker

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
        btnStartDate = view.findViewById(R.id.btnStartDate)
        btnEndDate = view.findViewById(R.id.btnEndDate)
        tpDate = view.findViewById(R.id.tpDate)
        //禁止编辑
        tpDate.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnStartDate.setOnClickListener(this)
        btnEndDate.setOnClickListener(this)
        tvTitle.text = title
        btnStartDate.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_HM)
        btnEndDate.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_HM)
        btnStartDate.isChecked = true
        btnEndDate.isChecked = false
        tpDate.setOnTimeChangedListener(this)
        return view
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        if (btnStartDate.isChecked) {
            startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            startCalendar.set(Calendar.MINUTE, minute)
            startLimitCalendar?.apply {
                if (startCalendar.time.before(time)) {
                    view.hour = get(Calendar.HOUR_OF_DAY)
                    view.minute = get(Calendar.MINUTE)
                }
            }
            val endLimitDate =   endCalendar//.、、tim, endLimitCalendar.time)
            endLimitCalendar?.apply {
                if (startCalendar.time.after(time)) {
                    view.hour = get(Calendar.HOUR_OF_DAY)
                    view.minute = get(Calendar.MINUTE)
                }
            }

            // startCalendar.set(year, monthOfYear, dayOfMonth)
            // btnStartDate.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_HM)
        } else {
            // endCalendar.set(year, monthOfYear, dayOfMonth)
            // btnEndDate.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_HM)
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnStartDate -> {
                if (btnStartDate.isChecked) {
                    btnEndDate.isChecked = false
                    // setTpDate()
                } else {
                    btnStartDate.isChecked = true
                }
            }
            R.id.btnEndDate -> {
                if (btnEndDate.isChecked) {
                    btnStartDate.isChecked = false
                    // setTpDate()
                } else {
                    btnEndDate.isChecked = true
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

    fun show(start: String?, end: String?, manager: FragmentManager) {
        if (!this.isAdded) {
            if (start != null) {
                startCalendar.apply {
                    time = Util.str2date(start, Util.DATE_PATTERN_HM)
                }
            } else {
                startCalendar = Calendar.getInstance()
            }
            if (end != null) {
                endCalendar.apply {
                    time = Util.str2date(end, Util.DATE_PATTERN_HM)
                }
            } else {
                endCalendar = Calendar.getInstance()
            }
            show(manager, className)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        btnStartDate.isChecked = true
        btnEndDate.isChecked = false
        super.onDismiss(dialog)
    }
}