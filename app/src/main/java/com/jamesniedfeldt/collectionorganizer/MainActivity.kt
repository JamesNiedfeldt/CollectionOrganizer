package com.jamesniedfeldt.collectionorganizer

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import com.jamesniedfeldt.collectionorganizer.R.id.*

class MainActivity : AppCompatActivity() {
    var add = true
    var db: CollectionDatabase? = null
    var list: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = CollectionDatabase(this)
        list = findViewById(list_category)
        list!!.adapter = db!!.Adapter()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        action_new -> {
            var item1: Item
            var item2: Item
            var item3: Item
            if(add){
                item1 = Item("Phoenix", "TestCat", "TestPic")
                item2 = Item("Apollo", "TestCat", "TestPic")
                item3 = Item("Athena", "TestCat", "TestPic")
                db!!.insert(item1, item2, item3)
                add = false
            }
            else{
                db!!.delete(1)
                db!!.delete(2)
                add = true
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
