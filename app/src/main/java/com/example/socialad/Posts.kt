package com.example.socialad

class Posts {
    var uid: String? = "";
    var time: String? = "";
    var date : String? = "";
    var description: String? = "";
    var fullname: String? = "";
    var profileImage: String? = "";
    var place: String? = "";
    var latitude: String? = "";
    var longitude: String? = "";


    constructor(
        uid: String,
        time: String,
        date: String,
        description: String,
        fullname: String,
        profileImage: String,
        place: String,
        latitude: String,
        longitude: String) {
        this.uid = uid
        this.time = time
        this.date = date
        this.description = description
        this.fullname = fullname
        this.profileImage = profileImage
        this.place = place
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor()

}