package com.jamesniedfeldt.collectionorganizer

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

class FilteredCategoryActivity : AppCompatActivity(){
    lateinit var db: CollectionDatabase
    lateinit var adapter: CollectionDatabase.ItemAdapter
    lateinit var list: ListView
    lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filteredcategory)
        val info = intent.extras
        val alert = AlertDialog.Builder(this)

        if(info.containsKey("CATEGORY")){
            category = info["CATEGORY"].toString()
            supportActionBar!!.title = category as CharSequence
        }


        db = CollectionDatabase(this, true)
        db.retrieveByCategory(category)
        list = findViewById(R.id.list_items)
        adapter = db.ItemAdapter()
        list.adapter = adapter

        list.setOnItemLongClickListener(object: AdapterView.OnItemLongClickListener{
            override fun onItemLongClick(parent: AdapterView<*>?, view: View?,
                                         position: Int, id: Long): Boolean {
                val item = parent!!.getItemAtPosition(position) as Item

                alert.setPositiveButton("Yes",DialogInterface.OnClickListener{
                    dialog, which: Int ->
                    db.delete(item)
                    adapter.notifyDataSetChanged()
                })
                alert.setNegativeButton("No",DialogInterface.OnClickListener{
                    dialog, which: Int ->

                })
                alert.setMessage("Delete ${item.name}?")
                alert.show()

                return true
            }
        })

        list.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editItem(parent!!.getItemAtPosition(position) as Item)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.action_new -> {
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
        //Create new item
        if(resultCode == RESULT_OK && data!!.extras.containsKey("SUCCESS_NEW")){
            val results = data!!.extras.getStringArrayList("SUCCESS_NEW")
            val name = results[0]
            val category = results[1]
            val rating = results[2].toInt()
            val pic = results[3]
            val item = Item(name, category, rating, pic)
            db.insert(item)
        }
        //Edit already existing item
        else if(resultCode == RESULT_OK && data!!.extras.containsKey("SUCCESS_EDIT")){
            val results = data!!.extras.getStringArrayList("SUCCESS_EDIT")
            val name = results[0]
            val category = results[1]
            val rating = results[2].toInt()
            val pic = results[3]
            val item = Item(name, category, rating, pic)
            item.id = results[4].toInt()
            db.edit(item)
        }
        db.retrieveByCategory(category)
        adapter.notifyDataSetChanged()
    }

    fun newItem(){
        val intent = Intent(this, EditItemActivity::class.java)
        intent.putExtra("NEW_FROM_FILTERED", category)
        startActivityForResult(intent, NEW_ITEM)
    }

    fun editItem(item: Item){
        val intent = Intent(this, EditItemActivity::class.java)
        val itemStrings = arrayListOf(item.name,
                item.category,
                item.rating.toString(),
                item.pic.toString(),
                item.id.toString())

        intent.putExtra("EDIT_FROM_FILTERED", itemStrings)
        startActivityForResult(intent, 123)
    }
}

