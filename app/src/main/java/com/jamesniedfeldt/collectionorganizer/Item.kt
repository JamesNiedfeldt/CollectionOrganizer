package com.jamesniedfeldt.collectionorganizer

import android.net.Uri
import android.provider.BaseColumns
import android.util.Log

/**
 * Created by James on 4/10/2018.
 */

class Item(name: String, category: String, pic: String) {
    var name: String
    var category: String
    var pic: Uri?

    init {
        this.name = name
        this.category = category
        this.pic = Uri.parse(pic)
    }
}