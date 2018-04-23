package com.jamesniedfeldt.collectionorganizer

import android.net.Uri
import android.provider.BaseColumns
import android.util.Log

/**
 * Created by James on 4/10/2018.
 */

class Item(name: String, category: String, rating: Int, pic: String) {
    var id: Int = -1
    var name: String
    var category: String
    var rating: Int
    var pic: Uri?

    init {
        /* id is not declared in the constructor as it is supposed to be assigned when the item is
         * added to or taken from the database. Ideally id would be private but I couldn't figure
         * out how to make only mutable via the CollectionDatabase. */
        this.name = name
        this.category = category
        this.rating = rating
        this.pic = Uri.parse(pic)
    }
}