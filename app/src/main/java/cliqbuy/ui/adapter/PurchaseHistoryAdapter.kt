package cliqbuy.ui.adapter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.databinding.PurchaseHistoryItemBinding
import cliqbuy.model.PurchaseHistoryModel
import cliqbuy.ui.view.OrderDetailsActivity
import cliqbuy.ui.view.PurchaseHistory
import com.squareup.picasso.Picasso

class PurchaseHistoryAdapter(val context: Context, val purchaseHistoryModel: ArrayList<PurchaseHistoryModel.Data>) : RecyclerView.Adapter<PurchaseHistoryAdapter.ViewHolder>() {


    inner class ViewHolder(var binding: PurchaseHistoryItemBinding) :RecyclerView.ViewHolder(binding.root){

        fun bindData(position: Int) {
            binding.tvOrderId.text = purchaseHistoryModel[position].code
            binding.tvDate.text = purchaseHistoryModel[position].date
            binding.tvPaymentStatus.text = purchaseHistoryModel[position].paymentStatusString
            binding.tvAmount.text = purchaseHistoryModel[position].grandTotal
            binding.tvDeliveryStatus.text = purchaseHistoryModel[position].deliveryStatusString

            if (purchaseHistoryModel[position].paymentStatus == "unpaid")
                Picasso.get().load(R.drawable.ic_unpaid).error(R.drawable.ic_unpaid).placeholder(R.drawable.ic_unpaid).into(binding.ivPaid)
            else
                Picasso.get().load(R.drawable.ic_tick).error(R.drawable.ic_tick).placeholder(R.drawable.ic_tick).into(binding.ivPaid)

            binding.rltOrder.setOnClickListener {

                    var intent = Intent(context, OrderDetailsActivity::class.java)
                    intent.putExtra("id",purchaseHistoryModel[position].id)
                    val animation = ActivityOptions.makeCustomAnimation(
                        context,
                        R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
                    ).toBundle()
                    context.startActivity(intent, animation)

            }
        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseHistoryAdapter.ViewHolder = ViewHolder(
        PurchaseHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PurchaseHistoryAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return purchaseHistoryModel.size
    }

}