package com.platzi.realtimetrader.network

/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
interface RealtimeDataListener<T> {
    fun onDataChange(updateData: T)

    fun onError(exception: Exception)
}