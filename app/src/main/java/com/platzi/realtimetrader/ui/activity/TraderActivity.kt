package com.platzi.realtimetrader.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.realtimetrader.R
import com.platzi.realtimetrader.model.Crypto
import com.platzi.realtimetrader.network.Callback
import com.platzi.realtimetrader.network.FirebaseService
import com.platzi.realtimetrader.ui.adapter.CryptosAdapter
import com.platzi.realtimetrader.ui.adapter.CryptosAdapterListener
import kotlinx.android.synthetic.main.activity_trader.*

/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    lateinit var firebaseService: FirebaseService

    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)
        firebaseService = FirebaseService(FirebaseFirestore.getInstance())
        configureRecyclerView()
        loadCryptos()
    }

    private fun configureRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = cryptosAdapter
    }

    private fun loadCryptos() {
        firebaseService.getCryptos(object : Callback<List<Crypto>> {

            override fun onSuccess(result: List<Crypto>?) {
                this@TraderActivity.runOnUiThread {

                    cryptosAdapter.cryptosList = result!!
                    cryptosAdapter.notifyDataSetChanged()

                }
            }

            override fun onFailed(exception: Exception) {
                Log.e("Developer", "error", exception)
                showGeneralServerErrorMessage()
            }
        })
    }

    fun showGeneralServerErrorMessage() {
        Snackbar.make(fab, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    override fun onBuyCryptoClicked(crypto: Crypto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}