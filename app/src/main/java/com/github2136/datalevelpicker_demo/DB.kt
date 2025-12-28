package com.github2136.datalevelpicker_demo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.logging.Level

/**
 * Created by 44569 on 2023/12/28
 */
class DB(context: Context) : SQLiteOpenHelper(context, NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        // db.execSQL(
        //     "CREATE TABLE AreaCode (\n" +
        //         "    ParentCode TEXT,\n" +
        //         "    AreaCode   TEXT    PRIMARY KEY,\n" +
        //         "    AreaName   TEXT,\n" +
        //         "    Level      INTEGER,\n" +
        //         "    Type       TEXT,\n" +
        //         "    Sort       INTEGER\n" +
        //         ");\n"
        // )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    private fun getAllAreaCode(areacode: String): MutableList<AreaCode> {
        val cursor = readableDatabase.query("AreaCode", arrayOf("ParentCode", "AreaCode", "AreaName", "Level", "Type", "Sort"), "AreaCode like ?", arrayOf("$areacode%"), null, null, "Sort")
        val list = mutableListOf<AreaCode>()
        if (cursor.count > 0 && cursor.moveToFirst()) {
            val ParentCodeIndex = cursor.getColumnIndex("ParentCode")
            val AreaCodeIndex = cursor.getColumnIndex("AreaCode")
            val AreaNameIndex = cursor.getColumnIndex("AreaName")
            val LevelIndex = cursor.getColumnIndex("Level")
            val TypeIndex = cursor.getColumnIndex("Type")
            val SortIndex = cursor.getColumnIndex("Sort")
            do {
                val ParentCode = cursor.getString(ParentCodeIndex)
                val AreaCode = cursor.getString(AreaCodeIndex)
                val AreaName = cursor.getString(AreaNameIndex)
                val Level = cursor.getInt(LevelIndex)
                val Type = cursor.getString(TypeIndex)
                val Sort = cursor.getInt(SortIndex)
                val ac = AreaCode(ParentCode, AreaCode, AreaName, Level, Type, Sort)
                list.add(ac)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    private fun getNextList(parentCode: String, list: MutableList<AreaCode>): MutableList<AreaCode>? {
        val child = list.filter { it.ParentCode == parentCode }.toMutableList()
        for (areaCode in child) {
            areaCode.next = getNextList(areaCode.AreaCode, list)
        }
        return child.takeIf { it.isNotEmpty() }
    }

    fun getAreaCodeList(areacode: String): MutableList<AreaCode> {
        val list = getAllAreaCode(areacode)
        val l = list.filter { it.Level == 0 }.toMutableList()
        for (areaCode in l) {
            areaCode.next = getNextList(areaCode.AreaCode, list)
        }
        return l
    }

    fun getLazyAreaCodeList(areacode: String): MutableList<AreaCode>? {
        val cursor = readableDatabase.query("AreaCode", arrayOf("ParentCode", "AreaCode", "AreaName", "Level", "Type", "Sort"), "ParentCode = ?", arrayOf(areacode), null, null, "Sort")
        val list = mutableListOf<AreaCode>()
        if (cursor.count > 0 && cursor.moveToFirst()) {
            val ParentCodeIndex = cursor.getColumnIndex("ParentCode")
            val AreaCodeIndex = cursor.getColumnIndex("AreaCode")
            val AreaNameIndex = cursor.getColumnIndex("AreaName")
            val LevelIndex = cursor.getColumnIndex("Level")
            val TypeIndex = cursor.getColumnIndex("Type")
            val SortIndex = cursor.getColumnIndex("Sort")
            do {
                val ParentCode = cursor.getString(ParentCodeIndex)
                val AreaCode = cursor.getString(AreaCodeIndex)
                val AreaName = cursor.getString(AreaNameIndex)
                val Level = cursor.getInt(LevelIndex)
                val Type = cursor.getString(TypeIndex)
                val Sort = cursor.getInt(SortIndex)
                val ac = AreaCode(ParentCode, AreaCode, AreaName, Level, Type, Sort)
                list.add(ac)
            } while (cursor.moveToNext())
        }
        cursor.close()
        if (list.isEmpty()) {
            return null
        } else {
            return list
        }
    }

    companion object {
        const val NAME = "areacode.db"
    }
}