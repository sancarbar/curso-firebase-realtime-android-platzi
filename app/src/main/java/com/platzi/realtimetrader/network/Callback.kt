package com.platzi.realtimetrader.network

/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
interface Callback<T> {

    fun onSuccess(result: T?)

    fun onFailed(exception: Exception)
}