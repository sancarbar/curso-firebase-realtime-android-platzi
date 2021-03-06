package com.platzi.realtimetrader.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.platzi.realtimetrader.R
import com.platzi.realtimetrader.model.Crypto
import com.squareup.picasso.Picasso

/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
class CryptosAdapter(val cryptosAdapterListener: CryptosAdapterListener) :
    RecyclerView.Adapter<CryptosAdapter.ViewHolder>() {


    var cryptosList: List<Crypto> = ArrayList()

    override fun onBindViewHolder(holder: CryptosAdapter.ViewHolder, position: Int) {
        val crypto = cryptosList[position]
        holder.name.text = crypto.name

        val context = holder.itemView.context
        holder.available.text =
            context.getString(R.string.available_message, crypto.available.toString())
        Picasso.get().load(crypto.imageUrl).into(holder.image)

        holder.buyButton.isEnabled = crypto.available > 0

        holder.buyButton.setOnClickListener {
            cryptosAdapterListener.onBuyCryptoClicked(crypto)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.crypto_row, viewGroup, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return cryptosList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.nameTextView)
        var available = view.findViewById<TextView>(R.id.availableTextView)
        var image = view.findViewById<ImageView>(R.id.image)
        var buyButton = view.findViewById<Button>(R.id.buyButton)

    }
}