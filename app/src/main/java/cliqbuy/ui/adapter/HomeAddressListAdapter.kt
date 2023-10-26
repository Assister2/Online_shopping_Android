package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.model.AddressListModel
import cliqbuy.model.CountryListModel


class HomeAddressListAdapter (val context: Context,val addressListModel: AddressListModel,var onClick: OnHomeAddressClickListener) : RecyclerView.Adapter<HomeAddressListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAddress: TextView
        val tvCity: TextView
        val tvCountry: TextView
        val tvPostalCode: TextView
        val tvPhone: TextView
        val tvState: TextView
        val homeAddressLayout: CardView
        val ivTick: ImageView


        init {
            tvAddress = itemView.findViewById(R.id.edt_address)
            tvCity = itemView.findViewById(R.id.tv_city)
            tvCountry = itemView.findViewById(R.id.tv_country)
            tvPostalCode = itemView.findViewById(R.id.edt_postal_code)
            tvPhone = itemView.findViewById(R.id.edt_phone)
            tvState = itemView.findViewById(R.id.tv_state)
            homeAddressLayout = itemView.findViewById(R.id.cv_home_address_layout)
            ivTick = itemView.findViewById(R.id.iv_tick)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.item_home_address_list, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvAddress.text = addressListModel.data!![position].address
        holder.tvCity.text = addressListModel.data!![position].city
        holder.tvPostalCode.text = addressListModel.data!![position].postalCode
        holder.tvCountry.text = addressListModel.data!![position].country
        holder.tvPhone.text = addressListModel.data!![position].phone
        holder.tvState.text = addressListModel.data!![position].state

        holder.homeAddressLayout.setOnClickListener {
            onClick.onHomeAddressClick(position)
        }

        if (addressListModel.data!![position].isSelected)
            holder.ivTick.visibility = View.VISIBLE
        else
            holder.ivTick.visibility = View.GONE

    }

    override fun getItemCount(): Int {
        return addressListModel.data!!.size
    }


    interface OnHomeAddressClickListener {
        fun onHomeAddressClick(pos:Int)
    }


}