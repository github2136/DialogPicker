package com.github2136.datalevelpicker_demo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

    fun getAreaCode(areacode: String): MutableList<AreaCode> {
        // val cursor = readableDatabase.query("AreaCode", arrayOf("ParentCode", "AreaCode", "AreaName", "Level", "Type", "Sort"), "ParentCode = ?", arrayOf(areacode), null, null, "Sort")
        val cursor = readableDatabase.rawQuery("select * from areacode where ParentCode = '$areacode' order by sort",null)
        cursor.moveToFirst()

        val ParentCodeIndex = cursor.getColumnIndex("ParentCode")
        val AreaCodeIndex = cursor.getColumnIndex("AreaCode")
        val AreaNameIndex = cursor.getColumnIndex("AreaName")
        val LevelIndex = cursor.getColumnIndex("Level")
        val TypeIndex = cursor.getColumnIndex("Type")
        val SortIndex = cursor.getColumnIndex("Sort")
        val list = mutableListOf<AreaCode>()
        while (cursor.moveToNext()) {
            val ParentCode = cursor.getString(ParentCodeIndex)
            val AreaCode = cursor.getString(AreaCodeIndex)
            val AreaName = cursor.getString(AreaNameIndex)
            val Level = cursor.getInt(LevelIndex)
            val Type = cursor.getString(TypeIndex)
            val Sort = cursor.getInt(SortIndex)
            val ac = AreaCode(ParentCode, AreaCode, AreaName, Level, Type, Sort)
            list.add(ac)
        }
        cursor.close()

        return list
    }

    fun getAreaCodeList(areacode: String, limitLevel: Int): MutableList<AreaCode> {
        return getAreaCode(areacode).apply {
            firstOrNull()?.let {
                if (it.Level != limitLevel) {
                    this.forEach {
                        it.next = getAreaCodeList(it.AreaCode, limitLevel)
                    }
                }
            }
        }
    }
    companion object{
        const val NAME = "areacode.db"
    }
}