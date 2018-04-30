package com.jamesniedfeldt.collectionorganizer

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edititem.*
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val PIC_REQUESTED = 123
//TODO: Finish implementing different ways of creating this activity

class EditItemActivity : AppCompatActivity(){
    var okSelected = false
    var imageUri: Uri? = null
    var fileMade = false
    var photo: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edititem)

        val info = intent.extras

        //If sent from the item randomizer, we don't want it editable
        if(!info.containsKey("NOEDIT")){
            //Use an already existing file if the item already exists
            if(info.containsKey("EDITITEM")){
                val strings = info.getStringArrayList("EDITITEM")
                photo = File(Uri.parse(strings[3]).path)
                fileMade = true

                edit_name.setText(strings[0])
                edit_category.setText(strings[1])
                rating_bar.rating = strings[2].toFloat()
                edit_pic.setImageURI(Uri.parse(strings[3]))
            }

            edit_pic.setOnClickListener { view ->
                val getPic = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if (!fileMade) {
                    try {
                        photo = createFile()
                        fileMade = true
                    } catch (x: IOException) {
                        Log.i("ERROR", "Couldn't create file")
                    }
                }

                if (photo != null) {
                    imageUri = Uri.fromFile(photo)
                    getPic.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(getPic, PIC_REQUESTED)
                }
            }

            //If sent from FilteredCategoryActivity, auto-populate the category
            if(info.containsKey("FROMFILTERED")){
                edit_category.setText(info["FROMFILTERED"].toString())
            }

            button_ok.setOnClickListener { view ->
                okSelected = true
                finish()
            }

            button_cancel.setOnClickListener { view ->
                finish()
            }
        }
    }

    //Borrowed from https://developer.android.com/training/camera/photobasics.html
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PIC_REQUESTED && resultCode == RESULT_OK) {
            edit_pic.setImageURI(null) //Clear what was there if anything
            edit_pic.setImageURI(imageUri)
        }
        else{
            photo!!.delete()
            fileMade = false
        }
    }

    //Borrowed from https://developer.android.com/training/camera/photobasics.html
    fun createFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "JPEG_${timeStamp}_"
        val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val image = File.createTempFile(fileName, ".jpg", directory)
        return image
    }

    override fun finish(){
        //User wants item added to the database
        if(okSelected){
            if(edit_name.text.toString() == ""){
                Toast.makeText(this, "Name cannot be blank.", Toast.LENGTH_LONG).show()
                okSelected = false
            }
            else if(edit_category.text.toString() == ""){
                Toast.makeText(this, "Category cannot be blank", Toast.LENGTH_LONG).show()
                okSelected = false
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                val itemStrings = arrayListOf(edit_name.text.toString(),
                        edit_category.text.toString(),
                        rating_bar.rating.toInt().toString(),
                        imageUri.toString())

                intent.putExtra("SUCCESS", itemStrings)
                setResult(RESULT_OK, intent)
                super.finish()
            }
        }
        //Activity is killed by other means
        else{
            // Photo file is now dead space, get rid of it
            if(photo != null){
                photo!!.delete()
            }
            super.finish()
        }
    }
}
