package com.platzi.realtimetrader.ui.adapter

import com.platzi.realtimetrader.model.Crypto

interface CryptosAdapterListener {

    fun onBuyCryptoClicked(crypto: Crypto)
}