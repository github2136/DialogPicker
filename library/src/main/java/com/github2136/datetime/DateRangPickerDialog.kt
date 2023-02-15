package com.github2136.datetime

import android.content.DialogInterface
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
import com.google.android.material.button.MaterialButton
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Created by yb on 2023/2/13
 * 日期范围
 * @param title 显示标题
 * @param startLimit 开始范围
 * @param endLimit 结束范围
 */
class DateRangPickerDialog constructor(
    var title: String = "请选择时间范围", startLimit: String? = null, endLimit: String? = null, onConfirm: (start: String, end: String) -> Unit
) : DialogFragment(), View.OnClickListener, DatePicker.OnDateChangedListener {
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
    private lateinit var dpDate: DatePicker

    init {
        startCalendar = Calendar.getInstance()
        endCalendar = Calendar.getInstance()
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
        val view = inflater.inflate(R.layout.dialog_date_rang_picker, container)

        tvTitle = view.findViewById(R.id.tvTitle)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnStartDate = view.findViewById(R.id.btnStartDate)
        btnEndDate = view.findViewById(R.id.btnEndDate)
        dpDate = view.findViewById(R.id.dpDate)
        //禁止编辑
        dpDate.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnStartDate.setOnClickListener(this)
        btnEndDate.setOnClickListener(this)

        tvTitle.text = title
        btnStartDate.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_YMD)
        btnEndDate.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_YMD)
        btnStartDate.isChecked = true
        btnEndDate.isChecked = false
        setDpDate()
        return view
    }

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (btnStartDate.isChecked) {
            startCalendar.set(year, monthOfYear, dayOfMonth)
            btnStartDate.text = Util.date2str(startCalendar.time, Util.DATE_PATTERN_YMD)
        } else {
            endCalendar.set(year, monthOfYear, dayOfMonth)
            btnEndDate.text = Util.date2str(endCalendar.time, Util.DATE_PATTERN_YMD)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnStartDate -> {
                if (btnStartDate.isChecked) {
                    btnEndDate.isChecked = false
                    setDpDate()
                } else {
                    btnStartDate.isChecked = true
                }
            }
            R.id.btnEndDate -> {
                if (btnEndDate.isChecked) {
                    btnStartDate.isChecked = false
                    setDpDate()
                } else {
                    btnEndDate.isChecked = true
                }
            }
            R.id.btnConfirm -> {
                onConfirm?.invoke(Util.date2str(startCalendar.time, Util.DATE_PATTERN_YMD), Util.date2str(endCalendar.time, Util.DATE_PATTERN_YMD))
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
                Util.str2date(this, Util.DATE_PATTERN_YMD)?.apply {
                    startLimitCalendar?.also { it.time = this } ?: run { startLimitCalendar = Calendar.getInstance().also { it.time = this } }
                }
            }
        } else {
            startLimitCalendar = null
        }
        if (endLimit != null) {
            endLimit.apply {
                Util.str2date(this, Util.DATE_PATTERN_YMD)?.apply {
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
                    time = Util.str2date(start, Util.DATE_PATTERN_YMD)
                }
            } else {
                startCalendar = Calendar.getInstance()
            }
            if (end != null) {
                endCalendar.apply {
                    time = Util.str2date(end, Util.DATE_PATTERN_YMD)
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
    /**
     * 设置控件时间及限制范围
     */
    private fun setDpDate() {
        if (btnStartDate.isChecked) {
            dpDate.init(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH), this)
            dpDate.minDate = startLimitCalendar?.timeInMillis ?: 0
            endLimitCalendar?.apply {
                dpDate.maxDate = min(this.timeInMillis, endCalendar.timeInMillis)
            } ?: run {
                dpDate.maxDate = endCalendar.timeInMillis
            }
        } else {
            dpDate.init(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH), this)
            startLimitCalendar?.apply {
                dpDate.minDate = max(this.timeInMillis, endCalendar.timeInMillis)
            } ?: run {
                dpDate.minDate = startCalendar.timeInMillis
            }
            dpDate.maxDate = endLimitCalendar?.timeInMillis ?: Long.MAX_VALUE
        }
    }
}