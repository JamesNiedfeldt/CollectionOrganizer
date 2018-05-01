package com.jamesniedfeldt.collectionorganizer

import android.net.Uri

/* A container for all attributes of items to be stored. id is not intended to be changed
 * by anything other than the CollectionDatabase.
 */

class Item(name: String, category: String, rating: Int, pic: String) : Comparable<Item> {
    var id: Int = -1 //Meant to be set only by the CollectionDatabase
    var name: String = name
    var category: String = category
    var rating: Int = rating
    var pic: Uri = Uri.parse(pic)

    override fun compareTo(other: Item): Int {
        when {
            this.name < other.name -> {
                return -1
            }
            this.name == other.name -> {
                return 0
            }
            this.name > other.name -> {
                return 1
            }
        }
        return 0
    }
}