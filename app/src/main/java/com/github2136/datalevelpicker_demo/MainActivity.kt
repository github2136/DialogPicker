package com.github2136.datalevelpicker_demo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github2136.datalevelpicker.DataLevelPickerDialog
import com.github2136.datetime.DatePickerDialog
import com.github2136.datetime.DateRangPickerDialog
import com.github2136.datetime.TimePickerDialog

class MainActivity : AppCompatActivity() {
    var selectData: MutableList<City> = mutableListOf()
    val dataLevelPickerDialog by lazy {
        val l5 = mutableListOf<City>()
        repeat(2) { n ->
            val l4 = mutableListOf<City>()
            repeat(4) { l ->
                val l3 = mutableListOf<City>()
                repeat(6) { k ->
                    val l2 = mutableListOf<City>()
                    repeat(8) { j ->
                        val l1 = mutableListOf<City>()
                        repeat(10) { i ->
                            l1.add(City("E$n$l$k$j$i", "E$n$l$k$j$i", null))
                        }
                        l2.add(City("D$n$l$k$j", "D$n$l$k$j", l1))
                    }
                    l3.add(City("C$n$l$k", "C$n$l$k", l2))
                }
                l4.add(City("B$n$l", "B$n$l", l3))
            }
            l5.add(City("A$n", "A$n", l4))
        }

        DataLevelPickerDialog(l5) { data ->
            this.selectData = data
            tv.text = data.joinToString { it.getText() }
        }
    }
    lateinit var tv: TextView
    val datePickerDialog by lazy {
        DatePickerDialog {
            tv.text = it
        }
    }

    val dateRangPickerDialog by lazy {
        DateRangPickerDialog { start, end ->
            tv.text = "$start $end"
        }
    }
    val timePickerDialog by lazy {
        TimePickerDialog {
            tv.text = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv = findViewById(R.id.tv)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn1 -> {
                dataLevelPickerDialog.show(null, supportFragmentManager)
            }
            R.id.btn2 -> {
                if (selectData.isEmpty()) {
                    val d = mutableListOf<City>()
                    d.add(City("A0", "", null))
                    d.add(City("B01", "", null))
                    d.add(City("C012", "", null))
                    selectData.addAll(d)
                }
                dataLevelPickerDialog.show(selectData, supportFragmentManager)
            }
            R.id.btn3 -> {
                datePickerDialog.setLimit(null, null)
                datePickerDialog.show(null, supportFragmentManager)
            }
            R.id.btn4 -> {
                datePickerDialog.setLimit("2022-12-01", "2023-02-01")
                datePickerDialog.show("2023-01-01", supportFragmentManager)
            }
            R.id.btn5 -> {
                dateRangPickerDialog.setLimit(null, null)
                dateRangPickerDialog.show(null, null, supportFragmentManager)
            }
            R.id.btn6 -> {
                dateRangPickerDialog.setLimit("2023-01-01", "2023-02-01")
                dateRangPickerDialog.show("2023-01-02", "2023-01-05", supportFragmentManager)
            }
            R.id.btn7 -> {
                timePickerDialog.setLimit(null, null)
                timePickerDialog.show(null, supportFragmentManager)
            }
            R.id.btn8 -> {
                timePickerDialog.setLimit("07:00", "21:00")
                timePickerDialog.show("12:00", supportFragmentManager)
            }
        }
    }
}