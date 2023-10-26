package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.databinding.ItemPaymentBinding
import cliqbuy.model.PaymentMethodsModel
import com.squareup.picasso.Picasso

class PaymentListAdapter(
    val context: Context,
    private var paymentMethodsModel: ArrayList<PaymentMethodsModel>,
    var onClick: OnClick
) : RecyclerView.Adapter<PaymentListAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.also {

            it.binding.tvPaymentTitle.text = paymentMethodsModel[position].title
            it.binding.rltPayments.setOnClickListener { onClick.onItemClick(position) }

            if (paymentMethodsModel[position].isSelected) {
                it.binding.ivTick.isVisible = true
                it.binding.rltPayments.setBackgroundResource(R.drawable.bg_curved_payment_selected)
            } else {
                it.binding.ivTick.isVisible = false
                it.binding.rltPayments.setBackgroundResource(R.drawable.bg_curved_payment_unselected)

            }

            if (paymentMethodsModel[position].image.isNullOrEmpty()) Picasso.get()
                .load(R.drawable.ic_empty_cart).into(it.binding.ivIcons)
            else Picasso.get().load(paymentMethodsModel[position].image).into(it.binding.ivIcons)

        }
    }

    interface OnClick {
        fun onItemClick(position: Int)
    }

    override fun getItemCount() = paymentMethodsModel.size
}