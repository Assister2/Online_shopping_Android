package cliqbuy.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.databinding.ItemOrderDetailBinding
import cliqbuy.model.OrderDetailItemModel

class OrderDetailItemAdapter(val context: Context, val orderItemModel: OrderDetailItemModel, val onitemClick: onItemClick) : RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder>() {


    inner class ViewHolder(var binding: ItemOrderDetailBinding) :RecyclerView.ViewHolder(binding.root){

        fun bindData(position: Int) {
            binding.tvPriceValue.text = orderItemModel.data[position].price
            binding.tvQuantity.text = orderItemModel.data[position].quantity.toString() + " " +
                    Html.fromHtml(context.resources.getString(R.string.multiplication_symbol))+ " " +
                    context.resources.getString(R.string.item)
            binding.tvProductName.text = orderItemModel.data[position].productName

            binding.rltOrder.setOnClickListener {
                onitemClick.onItemClicked(position, orderItemModel.data[position].deliveryStatus!!)
            }
        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailItemAdapter.ViewHolder = ViewHolder(
        ItemOrderDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: OrderDetailItemAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return orderItemModel.data.size
    }

    interface onItemClick {
        fun onItemClicked(pos: Int,deliveryStatus:String)
    }


}