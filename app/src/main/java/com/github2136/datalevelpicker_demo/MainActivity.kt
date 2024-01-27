package com.github2136.datalevelpicker_demo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github2136.datalevelpicker.DataLevelPickerDialog
import com.github2136.datetime.DatePickerDialog
import com.github2136.datetime.DateRangePickerDialog
import com.github2136.datetime.DateTimePickerDialog
import com.github2136.datetime.DateTimeRangePickerDialog
import com.github2136.datetime.TimePickerDialog
import com.github2136.datetime.TimeRangePickerDialog
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    var selectData: MutableList<AreaCode> = mutableListOf()
    val db by lazy { DB(this) }
    val dataLevelPickerDialog by lazy {
        DataLevelPickerDialog(db.getAreaCodeList("")) { data ->
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

    val dateRangePickerDialog by lazy {
        DateRangePickerDialog { start, end ->
            tv.text = "$start $end"
        }
    }
    val timePickerDialog by lazy {
        TimePickerDialog {
            tv.text = it
        }
    }
    val timeRangePickerDialog by lazy {
        TimeRangePickerDialog { start, end ->
            tv.text = "$start $end"
        }
    }
    val dateTimePickerDialog by lazy {
        DateTimePickerDialog {
            tv.text = it
        }
    }
    val dateTimeRangePickerDialog by lazy {
        DateTimeRangePickerDialog { start, end ->
            tv.text = "$start $end"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv = findViewById(R.id.tv)

        val dbFile = getDatabasePath(DB.NAME)
        if (!dbFile.exists()) {
            val inputStream = resources.openRawResource(R.raw.areacode)
            val bis = BufferedInputStream(inputStream)
            bis.use {
                val outputStream = FileOutputStream(dbFile)
                val bos = BufferedOutputStream(outputStream)
                bos.use {
                    val bytes = ByteArray(8 * 1024)
                    var len = bis.read(bytes)
                    while (len > 0) {
                        bos.write(bytes)
                        len = bis.read(bytes)
                    }
                    bos.flush()
                }
            }
            Toast.makeText(this, "文件复制完成", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn1 -> {
                dataLevelPickerDialog.show(null, supportFragmentManager)
            }

            R.id.btn2 -> {
                if (selectData.isEmpty()) {
                    // val d = mutableListOf<AreaCode>()
                    // d.add(City("A9", "", null))
                    // d.add(City("B99", "", null))
                    // d.add(City("C999", "", null))
                    // selectData.addAll(d)
                }
                dataLevelPickerDialog.show(selectData, supportFragmentManager)
            }

            R.id.btn3 -> {
                datePickerDialog.setLimit(null, null)
                datePickerDialog.show(null, supportFragmentManager)
            }

            R.id.btn4 -> {
                datePickerDialog.setLimit("2022-12-01", "2023-12-01")
                datePickerDialog.show("2023-12-01", supportFragmentManager)
            }

            R.id.btn5 -> {
                dateRangePickerDialog.setLimit(null, null)
                dateRangePickerDialog.show(null, null, supportFragmentManager)
            }

            R.id.btn6 -> {
                dateRangePickerDialog.setLimit("2023-01-01", "2023-02-01")
                dateRangePickerDialog.show("2023-01-02", "2023-01-05", supportFragmentManager)
            }

            R.id.btn7 -> {
                timePickerDialog.setLimit(null, null)
                timePickerDialog.show(null, supportFragmentManager)
            }

            R.id.btn8 -> {
                timePickerDialog.setLimit("07:00", "21:00")
                timePickerDialog.show("12:00", supportFragmentManager)
            }

            R.id.btn9 -> {
                timeRangePickerDialog.setLimit(null, null)
                timeRangePickerDialog.show(null, null, supportFragmentManager)
            }

            R.id.btn10 -> {
                timeRangePickerDialog.setLimit("07:00", "21:00")
                timeRangePickerDialog.show(null, null, supportFragmentManager)
            }

            R.id.btn11 -> {
                dateTimePickerDialog.setLimit(null, null)
                dateTimePickerDialog.show(null, supportFragmentManager)
            }

            R.id.btn12 -> {
                dateTimePickerDialog.setLimit("2023-02-01 12:00", "2023-04-01 12:00")
                dateTimePickerDialog.show("2023-02-01 00:00", supportFragmentManager)
            }

            R.id.btn13 -> {
                dateTimeRangePickerDialog.setLimit(null, null)
                dateTimeRangePickerDialog.show("2023-01-01 12:00", "2023-01-01 13:00", supportFragmentManager)
            }

            R.id.btn14 -> {
                dateTimeRangePickerDialog.setLimit("2023-02-01 12:00", "2023-04-01 12:00")
                dateTimeRangePickerDialog.show("2023-02-01 00:00", "2023-02-01 13:00", supportFragmentManager)
            }
        }
    }
}