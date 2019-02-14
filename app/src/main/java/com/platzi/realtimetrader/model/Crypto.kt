package com.platzi.realtimetrader.model

/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
class Crypto(var name: String = "", var imageUrl: String = "", var available: Int = 0) {
    fun getDocumentId(): String {
        return name.toLowerCase()
    }
}