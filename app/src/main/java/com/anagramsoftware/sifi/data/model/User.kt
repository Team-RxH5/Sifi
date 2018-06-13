package com.anagramsoftware.sifi.data.model

class User() {
    var fname: String = ""
    var lname: String = ""
    var tag: String = ""
    var profilePic: String? = null
    var rating: Int = 0
    var ratingCount: Int = 0

    constructor(fname: String, lname: String, tag: String, profilePic: String? = null,
                rating: Int? = null, ratingCount: Int? = null) : this() {
        this.fname = fname
        this.lname = lname
        this.tag = tag
        this.profilePic = profilePic
        rating?.let {
            this.rating = it
        }
        ratingCount?.let {
            this.ratingCount = it
        }
    }
}
