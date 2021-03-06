package com.platzi.realtimetrader.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.realtimetrader.R
import com.platzi.realtimetrader.model.Crypto
import com.platzi.realtimetrader.model.User
import com.platzi.realtimetrader.network.Callback
import com.platzi.realtimetrader.network.FirestoreService
import com.platzi.realtimetrader.network.RealtimeDataListener
import com.platzi.realtimetrader.ui.adapter.CryptosAdapter
import com.platzi.realtimetrader.ui.adapter.CryptosAdapterListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_trader.*

/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    lateinit var firestoreService: FirestoreService

    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)

    private var username: String? = null

    var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        username = intent.extras!![USERNAME_KEY]!!.toString()
        usernameTextView.text = username

        configureRecyclerView()
        loadCryptos()

        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()
            generateCryptoCurrenciesRandom()
        }
    }

    private fun configureRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = cryptosAdapter
    }

    private fun loadCryptos() {
        firestoreService.getCryptos(object : Callback<List<Crypto>> {

            override fun onSuccess(result: List<Crypto>?) {
                this@TraderActivity.runOnUiThread {

                    cryptosAdapter.cryptosList = result!!
                    cryptosAdapter.notifyDataSetChanged()

                    firestoreService.findUserById(username!!, object : Callback<User> {

                        override fun onSuccess(result: User?) {
                            user = result
                            Log.d("Developer", "User:  ${result!!.username}")
                            if (user!!.cryptosList == null) {

                                val userCryptoList = mutableListOf<Crypto>()

                                for (crypto in cryptosAdapter.cryptosList) {
                                    val cryptoUser = Crypto()
                                    cryptoUser.name = crypto.name
                                    cryptoUser.available = 0
                                    cryptoUser.imageUrl = crypto.imageUrl
                                    userCryptoList.add(cryptoUser)
                                }

                                user!!.cryptosList = userCryptoList
                                firestoreService.updateUser(user!!, null)
                            }
                            loadUserCryptos()
                            addRealtimeDatabaseListeners(user!!, cryptosAdapter.cryptosList)
                        }

                        override fun onFailed(exception: Exception) {
                            Log.e("Developer", "error", exception)
                            showGeneralServerErrorMessage()
                        }

                    })

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
        if (crypto.available > 0) {
            for (userCrypto in user!!.cryptosList!!) {
                if (userCrypto.name == crypto.name) {
                    userCrypto.available += 1
                    break
                }
            }
            crypto.available--

            firestoreService.updateUser(user!!, null)
            firestoreService.updateCrypto(crypto)
        }
    }

    fun loadUserCryptos() {
        if (user != null && user!!.cryptosList != null) {
            infoPanel.removeAllViews()
            for (crypto in user!!.cryptosList!!) {
                addUserCryptoInfoRow(crypto)
            }
        }
    }

    private fun addUserCryptoInfoRow(crypto: Crypto) {
        val view = LayoutInflater.from(this).inflate(R.layout.coin_info, infoPanel, false)
        view.findViewById<TextView>(R.id.coinLabel).text =
            getString(R.string.coin_info, crypto.name, crypto.available.toString())
        Picasso.get().load(crypto.imageUrl).into(view.findViewById<ImageView>(R.id.coinIcon))
        infoPanel.addView(view)
    }

    fun addRealtimeDatabaseListeners(currentUser: User, cryptosList: List<Crypto>) {
        firestoreService.listenForUpdates(currentUser, object : RealtimeDataListener<User> {
            override fun onDataChange(updateData: User) {
                user = updateData
                loadUserCryptos()
            }

            override fun onError(exception: java.lang.Exception) {
                Log.e("Developer", "error", exception)
                showGeneralServerErrorMessage()
            }

        })

        firestoreService.listenForUpdates(cryptosList, object : RealtimeDataListener<Crypto> {
            override fun onDataChange(updateData: Crypto) {
                var pos = 0
                for (crypto in cryptosAdapter.cryptosList) {
                    if (crypto.name.equals(updateData.name)) {
                        crypto.available = updateData.available
                        cryptosAdapter.notifyItemChanged(pos)
                        break
                    }
                    pos++
                }

            }

            override fun onError(exception: java.lang.Exception) {
                Log.e("Developer", "error", exception)
                showGeneralServerErrorMessage()
            }

        })
    }

    private fun generateCryptoCurrenciesRandom() {
        for (crypto in cryptosAdapter.cryptosList) {
            val amount = (1..10).random()
            crypto.available += amount
            firestoreService.updateCrypto(crypto)
        }
    }


}