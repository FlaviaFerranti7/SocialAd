package com.example.socialad

class Users {
    var fullname: String? = "";
    var profileImage: String? = "";
    var status: String? = "";
    var country: String? = "";
    var username: String? = "";

    @Transient
    var key: String? = ""


    constructor(fullname: String?, profileImage: String?, status: String?, country: String?, username: String?) {
        this.fullname = fullname
        this.profileImage = profileImage
        this.status = status
        this.country = country
        this.username = username
    }

    constructor()


}