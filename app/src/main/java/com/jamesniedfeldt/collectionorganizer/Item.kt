package com.jamesniedfeldt.collectionorganizer

import android.net.Uri

class Item(name: String, category: String, rating: Int, pic: String) {
    var id: Int = -1 //Meant to be set only by the CollectionDatabase
    var name: String = name
    var category: String = category
    var rating: Int = rating
    var pic: Uri = Uri.parse(pic)
}