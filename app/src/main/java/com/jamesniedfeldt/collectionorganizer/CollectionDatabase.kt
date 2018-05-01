package com.jamesniedfeldt.collectionorganizer

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import java.io.File

/* A collection of items that interacts with the database and provides adapters for the listviews.
 *
 * Code largely borrowed from https://developer.android.com/training/data-storage/sqlite.html
 * And from http://androidpala.com/kotlin-sqlite-database/
 */

class CollectionDatabase(context: Context, filtered: Boolean = false) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    private val db = this.writableDatabase
    private val context = context
    private var items = mutableListOf<Item>()
    private var categories = mutableListOf<String>()
    var collectionSize: Int

    companion object : BaseColumns {
        const val TABLE_NAME = "Collection"
        const val COLUMN_NAME_ITEMNAME = "Name"
        const val COLUMN_NAME_CATEGORY = "Category"
        const val COLUMN_NAME_PIC = "Picture"
        const val COLUMN_NAME_RATING = "Rating"
        const val SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                        "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                        "$COLUMN_NAME_ITEMNAME TEXT, " +
                        "$COLUMN_NAME_CATEGORY TEXT, " +
                        "$COLUMN_NAME_RATING INTEGER, " +
                        "$COLUMN_NAME_PIC TEXT)"
        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TABLE_NAME}"
        const val DATABASE_NAME = "Collection.db"
        const val DATABASE_VERSION = 1
    }

    init {
        if(!filtered){
            // Only populates categories
            retrieveCategories()
        }
        collectionSize = items.size
    }

    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun retrieveItem(index: Int): Item {
        return items[index]
    }

    //Add a new item to the database
    fun insert(vararg inList: Item){
        var values: ContentValues

        for(i in 0 until inList.size){
            values = ContentValues().apply{
                put(COLUMN_NAME_ITEMNAME, inList[i].name)
                put(COLUMN_NAME_CATEGORY, inList[i].category)
                put(COLUMN_NAME_RATING, inList[i].rating)
                put(COLUMN_NAME_PIC, inList[i].pic.toString())
            }
            inList[i].id = db.insert(TABLE_NAME, null, values).toInt()
            items.add(inList[i])
        }
        items.sort()
        collectionSize = items.size
    }

    //Edit an item already in the database
    fun edit(item: Item){
        var values: ContentValues

        values = ContentValues().apply{
            put(COLUMN_NAME_ITEMNAME, item.name)
            put(COLUMN_NAME_CATEGORY, item.category)
            put(COLUMN_NAME_RATING, item.rating)
            put(COLUMN_NAME_PIC, item.pic.toString())
        }
        db.update(TABLE_NAME, values, "${BaseColumns._ID} = ${item.id}", null)
        items.remove(item)
        items.add(item)
        items.sort()
    }

    //Delete an item from the database
    fun delete(item: Item){
        val del = "DELETE FROM $TABLE_NAME WHERE ${BaseColumns._ID}=${item.id}"
        var photo: File? = null

        photo = File(item.pic.path)
        photo.delete()

        db.execSQL(del)
        items.remove(item)
        collectionSize = items.size
    }

    //Get only unique categories for MainActivity
    fun retrieveCategories(){
        val cursor = db.rawQuery("SELECT DISTINCT $COLUMN_NAME_CATEGORY " +
                "FROM $TABLE_NAME", null)
        var temp: String

        categories.clear()

        if(cursor.moveToFirst()) {
            for (i in 0 until cursor.count) {
                temp = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CATEGORY))

                categories.add(temp)
                cursor.moveToNext()
            }
        }
        categories.sort()
    }

    //Get all items of a given category for FilteredCategoryActivity
    fun retrieveByCategory(category: String){
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME " +
                "WHERE $COLUMN_NAME_CATEGORY='$category'", null)
        var tempName: String
        var tempCategory: String
        var tempRating: Int
        var tempPic: String
        var tempItem: Item

        items.clear()

        if(cursor.moveToFirst()){
            for(i in 0 until cursor.count){
                tempName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ITEMNAME))
                tempCategory = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CATEGORY))
                tempRating = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_RATING))
                tempPic = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PIC))

                tempItem = Item(tempName, tempCategory, tempRating, tempPic)
                tempItem.id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))

                items.add(tempItem)
                cursor.moveToNext()
            }
        }
        items.sort()
        collectionSize = items.size
    }

    // Adapts list of items to the listView
    inner class ItemAdapter : ArrayAdapter<Item>(context, R.layout.list_item, items) {
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
            val rateView: RatingBar = listRow!!.findViewById(R.id.item_rate)

            nameView.text = items[position].name
            rateView.rating = items[position].rating.toFloat()

            return listRow
        }
    }

    // Adapts list of categories to the listView
    inner class CategoryAdapter : ArrayAdapter<String>(context, R.layout.list_item, categories) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val listRow: View?

            if(convertView == null){
                listRow = LayoutInflater.from(context)
                        .inflate(R.layout.list_category, null, true)
            }
            else{
                listRow = convertView
            }
            val categoryView: TextView = listRow!!.findViewById(R.id.category_only)

            categoryView.text = categories[position]

            return listRow
        }
    }
}