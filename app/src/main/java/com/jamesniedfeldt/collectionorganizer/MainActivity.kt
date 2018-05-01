package com.jamesniedfeldt.collectionorganizer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.jamesniedfeldt.collectionorganizer.R.id.*

/* Initial activity. The screen displays all of the categories of items saved.
 * Selecting a category from the list will begin a new activity displaying
 * a list of those items with the selected category.
 */

const val NEW_ITEM = 111

class MainActivity : AppCompatActivity() {
    lateinit var db: CollectionDatabase
    lateinit var adapter: CollectionDatabase.CategoryAdapter
    lateinit var list: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = CollectionDatabase(this)
        list = findViewById(list_category)
        adapter = db.CategoryAdapter()
        list.adapter = adapter

        list.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showItems(parent!!.getItemAtPosition(position) as String)
            }
        })
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
        //Add a newly created item to the database
        if(resultCode == RESULT_OK){
            val results = data!!.extras.getStringArrayList("SUCCESS_NEW")
            val name = results[0]
            val category = results[1]
            val rating = results[2].toInt()
            val pic = results[3]
            val item = Item(name, category, rating, pic)
            db.insert(item)
        }
        db.retrieveCategories()
        adapter.notifyDataSetChanged()
    }

    override fun onResume(){
        super.onResume()

        db.retrieveCategories()
        adapter.notifyDataSetChanged()
    }

    //Create a new item
    private fun newItem(){
        val intent = Intent(this, EditItemActivity::class.java)
        intent.putExtra("NEW_FROM_MAIN", "")
        startActivityForResult(intent, NEW_ITEM)
    }

    //Show items from the selected category
    private fun showItems(category: String){
        val intent = Intent(this, FilteredCategoryActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }
}
