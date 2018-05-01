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
const val NEW_FROM_MAIN = "NEW_FROM_MAIN"
const val NEW_FROM_FILTERED = "NEW_FROM_FILTERED"
const val EDIT_FROM_FILTERED = "EDIT_FROM_FILTERED"
const val FROM_RANDOMIZER = "FROM_RANDOMIZER"

class EditItemActivity : AppCompatActivity(){
    var delete = true
    var imageUri: Uri? = null
    var fileMade = false
    var photo: File? = null
    var toReturn: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edititem)

        val info = intent.extras

        when {
            info.containsKey(NEW_FROM_MAIN) -> setupNewFromMain()
            info.containsKey(NEW_FROM_FILTERED) -> setupNewFromFiltered(info.getString(NEW_FROM_FILTERED))
            info.containsKey(EDIT_FROM_FILTERED) -> setupEditFromFiltered(info)
            info.containsKey(FROM_RANDOMIZER) -> setupFromRandomizer()
        }


        //If sent from the item randomizer, we don't want it editable
        if(!info.containsKey(FROM_RANDOMIZER)){
            edit_pic.setOnClickListener { view ->
                val getPic = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if (photo != null) {
                    imageUri = Uri.fromFile(photo)
                    getPic.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(getPic, PIC_REQUESTED)
                }
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
        //If activity is killed by anything other than "ok", delete the photo file
        if(delete){
            // Photo file is now dead space, get rid of it
            if(photo != null){
                photo!!.delete()
            }
            super.finish()
        }
        else{
            super.finish()
        }
    }

    //Activity is called via MainActivity (new menu item)
    fun setupNewFromMain(){
        try {
            photo = createFile()
        } catch (x: IOException) {
            Log.i("ERROR", "Couldn't create file")
        }

        button_ok.setOnClickListener { view ->
            if(edit_name.text.toString() == ""){
                Toast.makeText(this, "Name cannot be blank.", Toast.LENGTH_LONG).show()
            }
            else if(edit_category.text.toString() == ""){
                Toast.makeText(this, "Category cannot be blank", Toast.LENGTH_LONG).show()
            }
            else {
                toReturn = Intent(this, MainActivity::class.java)
                val itemStrings = arrayListOf(edit_name.text.toString(),
                        edit_category.text.toString(),
                        rating_bar.rating.toInt().toString(),
                        imageUri.toString())

                intent.putExtra("SUCCESS_NEW", itemStrings)
                setResult(RESULT_OK, intent)
                delete = false
                finish()
            }
        }

        button_cancel.setOnClickListener { view ->
            delete = true
            finish()
        }
    }

    //Activity is called from FilteredCategoryActivity (new menu item)
    fun setupNewFromFiltered(category: String){
        edit_category.setText(category)
        try {
            photo = createFile()
        } catch (x: IOException) {
            Log.i("ERROR", "Couldn't create file")
        }

        button_ok.setOnClickListener { view ->
            if(edit_name.text.toString() == ""){
                Toast.makeText(this, "Name cannot be blank.", Toast.LENGTH_LONG).show()
            }
            else if(edit_category.text.toString() == ""){
                Toast.makeText(this, "Category cannot be blank", Toast.LENGTH_LONG).show()
            }
            else {
                toReturn = Intent(this, MainActivity::class.java)
                val itemStrings = arrayListOf(edit_name.text.toString(),
                        edit_category.text.toString(),
                        rating_bar.rating.toInt().toString(),
                        imageUri.toString())

                intent.putExtra("SUCCESS_NEW", itemStrings)
                setResult(RESULT_OK, intent)
                delete = false
                finish()
            }
        }

        button_cancel.setOnClickListener { view ->
            delete = true
            finish()
        }
    }

    //Activity is called from FilteredCategoryActivity (list item)
    fun setupEditFromFiltered(info: Bundle){
        val strings = info.getStringArrayList(EDIT_FROM_FILTERED)
        photo = File(Uri.parse(strings[3]).path)
        imageUri = Uri.fromFile(photo)
        fileMade = true
        delete = false

        edit_name.setText(strings[0])
        edit_category.setText(strings[1])
        rating_bar.rating = strings[2].toFloat()
        edit_pic.setImageURI(Uri.parse(strings[3]))

        button_ok.setOnClickListener { view ->
            if(edit_name.text.toString() == ""){
                Toast.makeText(this, "Name cannot be blank.", Toast.LENGTH_LONG).show()
            }
            else if(edit_category.text.toString() == ""){
                Toast.makeText(this, "Category cannot be blank", Toast.LENGTH_LONG).show()
            }
            else {
                toReturn = Intent(this, MainActivity::class.java)
                val itemStrings = arrayListOf(edit_name.text.toString(),
                        edit_category.text.toString(),
                        rating_bar.rating.toInt().toString(),
                        imageUri.toString(),
                        strings[4]) //Id of the item needs to be returned

                intent.putExtra("SUCCESS_EDIT", itemStrings)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        button_cancel.setOnClickListener { view ->
            finish()
        }
    }

    //Activity is called from FilteredCategoryActivity (randomizer)
    fun setupFromRandomizer(){

    }
}
