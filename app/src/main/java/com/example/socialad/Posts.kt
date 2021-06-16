package com.example.socialad

class Posts {
    var uid: String? = "";
    var time: String? = "";
    var date : String? = "";
    var description: String? = "";
    var fullname: String? = "";
    var profileImage: String? = "";


    constructor(
        uid: String,
        time: String,
        date: String,
        description: String,
        fullname: String,
        profileImage: String) {
        this.uid = uid
        this.time = time
        this.date = date
        this.description = description
        this.fullname = fullname
        this.profileImage = profileImage
    }

    constructor()

}