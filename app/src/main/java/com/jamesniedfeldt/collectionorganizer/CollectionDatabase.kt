package com.jamesniedfeldt.collectionorganizer

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by James on 4/10/2018.
 * Code largely borrowed from https://developer.android.com/training/data-storage/sqlite.html
 * And from http://androidpala.com/kotlin-sqlite-database/
 */

class CollectionDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    private val db: SQLiteDatabase
    private val context: Context
    private var items = mutableListOf<Item>()
    var collectionSize: Int

    companion object : BaseColumns {
        const val TABLE_NAME = "Collection"
        const val COLUMN_NAME_ITEMNAME = "Name"
        const val COLUMN_NAME_CATEGORY = "Category"
        const val COLUMN_NAME_PIC = "Picture"
        const val COLUMN_NAME_RATING = "Rating" //TODO: implement this
        const val SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                        "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                        "$COLUMN_NAME_ITEMNAME TEXT, " +
                        "$COLUMN_NAME_CATEGORY TEXT, " +
                        "$COLUMN_NAME_PIC TEXT)"
        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TABLE_NAME}"
        const val DATABASE_NAME = "Collection.db"
        const val DATABASE_VERSION = 1
    }

    init {
        this.context = context;
        db = this.writableDatabase;
        retrieveFromDb()
        collectionSize = items.size
    }

    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //TODO: Figure out what is going on here?
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //TODO: Figure out what is going on here?
        onUpgrade(db, oldVersion, newVersion)
    }

    fun insert(vararg inList: Item){
        var assignId: Int
        var values: ContentValues

        for(i in 0 until inList.size){
            values = ContentValues().apply{
                put(COLUMN_NAME_ITEMNAME, inList[i].name)
                put(COLUMN_NAME_CATEGORY, inList[i].category)
                put(COLUMN_NAME_PIC, inList[i].pic.toString())
            }
            assignId = db.insert(TABLE_NAME, null, values).toInt()
            items.add(inList[i])
        }
        collectionSize = items.size
    }

    fun fetchByIndex(index: Int): Item? {
        return items[index]
    }

    fun delete(item: Item){
        val hashCode: Int
        val del: String

            //del = "DELETE FROM $TABLE_NAME WHERE ${BaseColumns._ID}=${hashCode.toString()}"
            //db.execSQL(del)
            items.remove(item)
            collectionSize = items.size
    }

    private fun retrieveFromDb(){
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        var tempId: Int
        var tempName: String
        var tempCategory: String
        var tempPic: String

        if(cursor.moveToFirst()){
            for(i in 0 until cursor.count){
                tempId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                tempName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ITEMNAME))
                tempCategory = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CATEGORY))
                tempPic = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PIC))
                items.add(Item(tempName, tempCategory, 0, tempPic)) //TODO: implement ratings into database (THE 0 IS TEMPORARY!)
                cursor.moveToNext()
            }
        }
    }

    inner class Adapter : ArrayAdapter<Item>(context, R.layout.list_item, items) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val listRow: View?

            if(convertView == null){
                listRow = LayoutInflater.from(context)
                        .inflate(R.layout.list_item, null, true)
            }
            else{
                listRow = convertView
            }
            val nameView: TextView = listRow!!.findViewById(R.id.item_name)
            val picView: ImageView = listRow!!.findViewById(R.id.item_pic)

            nameView.text = items[position].name
            //picView.setImageURI(items.values.elementAt(pos).pic)
            Log.i("ARRAY ADAPTER","getView called")

            return listRow
        }
    }
}