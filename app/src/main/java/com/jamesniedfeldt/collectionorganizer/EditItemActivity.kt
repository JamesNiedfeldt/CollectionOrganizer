package com.jamesniedfeldt.collectionorganizer

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edititem.*
import android.graphics.Bitmap
import android.R.attr.data
import android.support.v4.app.NotificationCompat.getExtras



class EditItemActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edititem)

        val info = intent.extras

        edit_pic.setOnClickListener{ view ->
            val getPic = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(getPic.resolveActivity(packageManager) != null){
                startActivityForResult(getPic, 1)
            }
        }

        //Called from "NEW" menu item
        if(info.containsKey("NEWITEM")){
            button_ok.setOnClickListener { view ->
                finish()
            }
        }
    }

    //Borrowed from https://developer.android.com/training/camera/photobasics.html
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val extras = data!!.extras
            val imageBitmap = extras.get("data") as Bitmap

            edit_pic.setImageBitmap(imageBitmap)
        }
    }

    override fun finish(){
        if(edit_name.text.toString() == ""){
            Toast.makeText(this, "Name cannot be blank.", Toast.LENGTH_LONG).show()
        }
        else if(edit_category.text.toString() == ""){
            Toast.makeText(this, "Category cannot be blank", Toast.LENGTH_LONG).show()
        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            val itemStrings = arrayListOf(edit_name.text.toString(),
                    edit_category.text.toString(),
                    rating_bar.rating.toInt().toString(),
                    "")

            intent.putExtra("SUCCESS", itemStrings)
            setResult(RESULT_OK, intent)
            super.finish()
        }
    }
}
