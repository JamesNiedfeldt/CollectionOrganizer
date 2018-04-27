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

class EditItemActivity : AppCompatActivity(){
    var okSelected = false
    var imageUri: Uri? = null
    var fileMade = false
    var photo: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edititem)

        val info = intent.extras

        //Borrowed from https://developer.android.com/training/camera/photobasics.html
        edit_pic.setOnClickListener{ view ->
            val getPic = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(!fileMade){
                try{
                    photo = createFile()
                    fileMade = true
                }
                catch(x: IOException){
                    Log.i("ERROR","Couldn't create file")
                }
            }

            if(photo != null){
                imageUri = Uri.fromFile(photo)
                getPic.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(getPic, PIC_REQUESTED)
            }
        }

        //Called from "NEW" menu item
        if(info.containsKey("NEWITEM")){
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
        else{ //Activity is killed by other means
            // Photo file is now dead space, get rid of it
            if(photo != null){
                photo!!.delete()
            }
            super.finish()
        }
    }
}
