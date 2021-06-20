package com.example.socialad

class Messages {
    var from: String? = "";
    var time: String? = "";
    var date : String? = "";
    var message: String? = "";

    constructor(from: String?, time: String?, date: String?, message: String?) {
        this.from = from
        this.time = time
        this.date = date
        this.message = message
    }

    constructor()

}