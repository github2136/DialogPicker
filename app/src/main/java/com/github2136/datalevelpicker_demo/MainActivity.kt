package com.github2136.datalevelpicker_demo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github2136.datalevelpicker.DataLevelPickerDialog
import com.github2136.datalevelpicker.IDataLevel

class MainActivity : AppCompatActivity() {
    lateinit var data: MutableList<IDataLevel>
    val dataLevelPickerDialog by lazy {
        val l5 = mutableListOf<IDataLevel>()
        repeat(2) { n ->
            val l4 = mutableListOf<City>()
            repeat(4) { l ->
                val l3 = mutableListOf<City>()
                repeat(6) { k ->
                    val l2 = mutableListOf<City>()
                    repeat(8) { j ->
                        val l1 = mutableListOf<City>()
                        repeat(10) { i ->
                            l1.add(City("A$n$l$k$j$i", "A$n$l$k$j$i", null))
                        }
                        l2.add(City("B$n$l$k$j", "B$n$l$k$j", l1))
                    }
                    l3.add(City("C$n$l$k", "C$n$l$k", l2))
                }
                l4.add(City("D$n$l", "D$n$l", l3))
            }
            l5.add(City("E$n", "E$n", l4))
        }

        DataLevelPickerDialog(l5) { data ->
            this.data = data
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
                if (::data.isInitialized) {
                    dataLevelPickerDialog.setData(data)
                } else {
                    val d = mutableListOf<IDataLevel>()
                    d.add(City("E0", "E0", null))
                    d.add(City("D01", "D01", null))
                    d.add(City("C012", "C012", null))
                    dataLevelPickerDialog.setData(d)
                }
                dataLevelPickerDialog.show(supportFragmentManager)
            }
        }
    }
}