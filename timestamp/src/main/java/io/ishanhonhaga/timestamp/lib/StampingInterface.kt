package io.ishanhonhaga.timestamp.lib

interface StampingInterface {

    fun onError(errorMsg: String)

    fun onCompleted(success: Boolean)

}