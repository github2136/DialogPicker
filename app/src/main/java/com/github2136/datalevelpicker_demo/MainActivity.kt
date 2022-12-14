package com.github2136.datalevelpicker_demo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github2136.datalevelpicker.DataLevelPickerDialog
import com.github2136.datalevelpicker.IDataLevel

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv = findViewById(R.id.tv)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn1 -> {
                dataLevelPickerDialog.show(supportFragmentManager)
            }
            R.id.btn2 -> {
                if (selectData.isNotEmpty()) {
                    dataLevelPickerDialog.setData(selectData)
                } else {
                    val d = mutableListOf<City>()
                    d.add(City("A0", "", null))
                    d.add(City("B01", "", null))
                    d.add(City("C012", "", null))
                    dataLevelPickerDialog.setData(d)
                }
                dataLevelPickerDialog.show(supportFragmentManager)
            }
        }
    }
}