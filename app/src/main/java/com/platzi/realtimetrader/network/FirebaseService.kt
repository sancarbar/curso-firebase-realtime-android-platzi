package com.platzi.realtimetrader.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.realtimetrader.model.Crypto
import com.platzi.realtimetrader.model.User

/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
const val CRYPTO_COLLECTION_NAME = "cryptos"
const val USERS_COLLECTION_NAME = "users"

class FirebaseService(val firebaseFirestore: FirebaseFirestore) {


    fun setDocument(data: Any, collectionName: String, id: String, callback: Callback<Void>) {
        firebaseFirestore.collection(collectionName).document(id).set(data)
            .addOnSuccessListener { callback.onSuccess(null) }
            .addOnFailureListener { exception ->
                Log.w("Developer", "Error getting documents.", exception)
                callback.onFailed(exception)
            }
    }


    fun updateUser(user: User, callback: Callback<User>?) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(user.username)
            .update("cryptosList", user.cryptosList)
            .addOnSuccessListener { result ->
                if (callback != null)
                    callback.onSuccess(user)
            }
            .addOnFailureListener { exception ->
                Log.w("Developer", "Error getting documents.", exception)
                if (callback != null)
                    callback.onFailed(exception)
            }
    }

    fun updateCrypto(crypto: Crypto) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME).document(crypto.getDocumentId())
            .update("available", crypto.available)
    }


    fun getCryptos(callback: Callback<List<Crypto>>) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val cryptosList = result.toObjects(Crypto::class.java)
                    callback.onSuccess(cryptosList)
                    break
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Developer", "Error getting documents.", exception)
                callback.onFailed(exception)
            }
    }

    fun findUserById(id: String, callback: Callback<User>) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(id)
            .get()
            .addOnSuccessListener { result ->
                if (result.data == null)
                    callback.onSuccess(null)
                else
                    callback.onSuccess(result.toObject(User::class.java)!!)
            }
            .addOnFailureListener { exception ->
                Log.w("Developer", "Error getting documents.", exception)
                callback.onFailed(exception)
            }

    }



}