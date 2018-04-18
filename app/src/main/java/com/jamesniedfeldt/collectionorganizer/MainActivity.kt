package com.jamesniedfeldt.collectionorganizer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.jamesniedfeldt.collectionorganizer.R.id.*

class MainActivity : AppCompatActivity() {
    var add = true
    lateinit var db: CollectionDatabase
    lateinit var adapter: CollectionDatabase.Adapter
    lateinit var list: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = CollectionDatabase(this)
        list = findViewById(list_category)
        adapter = db.Adapter()
        list.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        action_new -> {
            var item1 = Item("Phoenix", "TestCat", 0, "TestPic")
            var item2 = Item("Apollo", "TestCat", 0, "TestPic")
            var item3 = Item("Athena", "TestCat", 0,"TestPic")
            if(add){

                db.insert(item1, item2, item3)
                add = false
                adapter.notifyDataSetChanged()
            }
            else{
                db.delete(item1)
                db.delete(item2)
                add = true
                adapter.notifyDataSetChanged()
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
