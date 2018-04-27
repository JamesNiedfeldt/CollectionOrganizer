package com.jamesniedfeldt.collectionorganizer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

class FilteredCategoryActivity : AppCompatActivity(){
    lateinit var db: CollectionDatabase
    lateinit var adapter: CollectionDatabase.ItemAdapter
    lateinit var list: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filteredcategory)

        val info = intent.extras
        db = CollectionDatabase(this, false)
        db.retrieveByCategory(info["CATEGORY"].toString())
        list = findViewById(R.id.list_items)
        adapter = db.ItemAdapter()
        list.adapter = adapter

        if(info.containsKey("CATEGORY")){
            supportActionBar!!.title = info["CATEGORY"] as CharSequence
        }

        list.setOnItemLongClickListener(object: AdapterView.OnItemLongClickListener{
            override fun onItemLongClick(parent: AdapterView<*>?, view: View?,
                                         position: Int, id: Long): Boolean {
                db.delete(parent!!.getItemAtPosition(position) as Item)
                return true
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    override fun finish(){
        super.finish()
    }
}

