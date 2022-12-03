package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // версия БД
        const val DATABASE_VERSION = 1
        // название БД
        const val DATABASE_NAME = "tasksdb"
        // название таблицы
        const val TABLE_NAME = "tasks"
        // названия полей
        const val KEY_ID = "id"
        const val NAME = "name"
        const val SURNAME = "surname"
        const val NUMBER = "number"
        const val DOB = "dob"

    }



    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_NAME (
            $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $NAME TEXT NOT NULL,
            $SURNAME TEXT NOT NULL,
            $NUMBER TEXT NOT NULL,
            $DOB TEXT NOT NULL
            )""")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getAllTasks(): List<Task> {
        val result = mutableListOf<Task>()
        val database = this.writableDatabase
        val cursor: Cursor = database.query(
            TABLE_NAME, null, null, null,
            null, null, null
        )
        if (cursor.moveToFirst()) {
            val idIndex: Int = cursor.getColumnIndex(KEY_ID)
            val nameIndex: Int = cursor.getColumnIndex(NAME)
            val surnameIndex: Int = cursor.getColumnIndex(SURNAME)
            val numberIndex: Int = cursor.getColumnIndex(NUMBER)
            val dobIndex: Int = cursor.getColumnIndex(DOB)
            do {
                val todo = Task(
                    cursor.getLong(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(surnameIndex),
                    cursor.getString(numberIndex),
                    cursor.getString(dobIndex)
                )
                result.add(todo)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    fun getById(id: Long): Task? {
        var result: Task? = null
        val database = this.writableDatabase
        val cursor: Cursor = database.query(
            TABLE_NAME, null, "$KEY_ID = ?", arrayOf(id.toString()),
            null, null, null
        )
        if (cursor.moveToFirst()) {
            val idIndex: Int = cursor.getColumnIndex(KEY_ID)
            val nameIndex: Int = cursor.getColumnIndex(NAME)
            val surnameIndex: Int = cursor.getColumnIndex(SURNAME)
            val numberIndex: Int = cursor.getColumnIndex(NUMBER)
            val dobIndex: Int = cursor.getColumnIndex(DOB)

            result = Task(
                cursor.getLong(idIndex),
                cursor.getString(nameIndex),
                cursor.getString(surnameIndex),
                cursor.getString(numberIndex),
                cursor.getString(dobIndex)
            )
        }
        cursor.close()
        return result
    }

    fun addTask(name: String, surname: String, number : String, dob: String): Long {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, name)
        contentValues.put(SURNAME, surname)
        contentValues.put(NUMBER, number)
        contentValues.put(DOB, dob)
        val id = database.insert(TABLE_NAME, null, contentValues)
        close()
        return id
    }

    fun updateTask(id: Long, name: String, surname: String, number : String, dob: String) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, name)
        contentValues.put(SURNAME, surname)
        contentValues.put(NUMBER, number)
        contentValues.put(DOB, dob)
        database.update(TABLE_NAME, contentValues, "$KEY_ID = ?", arrayOf(id.toString()))
        close()
    }

    fun deleteTask(id: Long) {
        val database = this.writableDatabase
        database.delete(TABLE_NAME, "$KEY_ID = ?", arrayOf(id.toString()))
        close()
    }

}

