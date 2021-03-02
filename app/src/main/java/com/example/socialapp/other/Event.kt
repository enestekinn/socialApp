package com.example.socialapp.other

class Event<T>(private val content : T) {

    var hasBeenHandled = false
        private set // only be read from outside

    fun getContentIfNotHandled() : T? {
        return if(!hasBeenHandled){
            hasBeenHandled = true
            content
        }
        else{
            null
        }
    }

    fun peekContent() = content
    
}