package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.model.AddressListModel
import cliqbuy.model.CountryListModel


class AddressListAdapter(
    val context: Context,
    var changeOnClickListener: ChangeOnClickListener,
    var deleteOnClickListener: DeleteOnClickListener,
    val addressListModel: AddressListModel,
    var setDefaultListener : (Int) -> Unit
) : RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAddress: TextView
        val tvCity: TextView
        val tvCountry: TextView
        val tvState: TextView
        val tvPostalCode: TextView
        val tvPhone: TextView
        val tvChange: TextView
        val tvDelete: TextView
        val tvAddressCount: TextView
        val tvDefault: TextView


        init {
            tvAddress = itemView.findViewById(R.id.edt_address)
            tvCity = itemView.findViewById(R.id.tv_city)
            tvCountry = itemView.findViewById(R.id.tv_country)
            tvPostalCode = itemView.findViewById(R.id.edt_postal_code)
            tvState = itemView.findViewById(R.id.edt_state)
            tvPhone = itemView.findViewById(R.id.edt_phone)
            tvChange = itemView.findViewById(R.id.tv_change)
            tvDelete = itemView.findViewById(R.id.tv_delete)
            tvAddressCount = itemView.findViewById(R.id.tv_address_count)
            tvDefault = itemView.findViewById(R.id.tv_default)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.item_address_list, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvAddressCount.text =
            context.resources.getString(R.string.address) + " " + (position + 1).toString()
        holder.tvAddress.text = addressListModel.data!![position].address
        holder.tvCity.text = addressListModel.data!![position].city
        holder.tvPostalCode.text = addressListModel.data!![position].postalCode
        holder.tvState.text = addressListModel.data!![position].state
        holder.tvCountry.text = addressListModel.data!![position].country
        holder.tvPhone.text = addressListModel.data!![position].phone
        holder.tvDefault.text =
            if (addressListModel.data!![position].setDefault == 0) context.getString(R.string.set_default) else
                context.getString(R.string.default_)

        holder.tvChange.setOnClickListener {
            changeOnClickListener.onChangeClick(
                position,
                holder.tvAddress.text.toString(),
                holder.tvCity.text.toString(),
                holder.tvState.text.toString(),
                holder.tvPostalCode.text.toString(),
                holder.tvCountry.text.toString(),
                holder.tvPhone.text.toString(), addressListModel.data!![position].id
            )
        }

        holder.tvDelete.setOnClickListener {
            deleteOnClickListener.onDeleteClick(position, addressListModel.data!![position].id)
        }

        holder.tvDefault.setOnClickListener {
            setDefaultListener.invoke(addressListModel.data!![position].id)
        }
    }

    override fun getItemCount(): Int {
        return addressListModel.data!!.size
    }

    interface ChangeOnClickListener {
        fun onChangeClick(
            pos: Int,
            address: String,
            city: String,
            state: String,
            postalCode: String,
            country: String,
            phone: String,
            id: Int,
        )
    }

    interface DeleteOnClickListener {
        fun onDeleteClick(pos: Int, id: Int)
    }


}