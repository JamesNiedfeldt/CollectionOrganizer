package com.jamesniedfeldt.collectionorganizer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.jamesniedfeldt.collectionorganizer.R.id.*

const val NEW_ITEM = 111

class MainActivity : AppCompatActivity() {
    var add = true
    lateinit var db: CollectionDatabase
    lateinit var adapter: CollectionDatabase.ItemAdapter
    lateinit var categoryList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = CollectionDatabase(this)
        categoryList = findViewById(list_category)
        adapter = db.ItemAdapter()
        categoryList.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        action_new -> {
            newItem()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(resultCode == RESULT_OK){
            val results = data!!.extras.getStringArrayList("SUCCESS")
            val name = results[0]
            val category = results[1]
            val rating = results[2].toInt()
            val pic = results[3]
            val item = Item(name, category, rating, pic)
            db.insert(item)
            adapter.notifyDataSetChanged()
        }
    }

    fun newItem(){
        val intent = Intent(this, EditItemActivity::class.java)
        intent.putExtra("NEWITEM", "")
        startActivityForResult(intent, NEW_ITEM)
    }
}
