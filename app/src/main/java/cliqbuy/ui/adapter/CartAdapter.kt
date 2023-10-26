package cliqbuy.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.databinding.ItemCartBinding
import cliqbuy.model.CartModel
import java.util.Locale

class CartAdapter(
    var context: Context,
    val cartModel: ArrayList<CartModel>,
    var onAmountChange: totalAmountChangeListerner,
    var ondeleteItemListerner: onDeleteItemListerner,
    var actionQuantity: ((Int, Int) -> Unit)? = null,
    var actionUpdateShipEngine: ((Int, String) -> Unit)? = null,
    var isFromDeliveryInfo: Boolean = false,
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    var amount: Float = 0f
    var totalAmount: Float = 0f

    inner class ViewHolder(var binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root), CartItemAdapter.increamentListerner,
        CartItemAdapter.decreamentListerner, CartItemAdapter.onDeleteItemListerner {

        fun bindData(position: Int) {
            binding.tvItemName.text = cartModel[position].name
            for (i in 0 until (cartModel[position].cartItems.size)) {
                amount += cartModel[position].cartItems[i].price!! * cartModel[position].cartItems[i].quantity!!
                cartModel[position].cartItems[0].shopTotalAmount = amount
                totalAmount += cartModel[position].cartItems[i].price!! * cartModel[position].cartItems[i].quantity!!
                /*  if (isFromDeliveryInfo)
                      totalAmount += cartModel[position].cartItems[i].price!! * cartModel[position].cartItems[i].quantity!!*/
            }

            if (isFromDeliveryInfo) {
                cartModel[position].cartItems.forEach {

                    /* it.shipengineData?.estimateData?.forEach { estimatedata ->
                         if (it.serviceCode.equals(estimatedata.serviceCode) &&
                             it.packageType.equals(estimatedata.packageType)
                         ) {
                             totalAmount += (estimatedata.shippingAmount?.originalAmount
                                 ?: 0).toFloat()
                             amount += (estimatedata.shippingAmount?.originalAmount
                                 ?: 0).toFloat()
                         }
                     }*/

                    /*  amount += (estimatedata.shippingAmount?.originalAmount
                          ?: 0).toFloat()*/

                    totalAmount += (it.shippingCost ?: 0).toFloat()
                    amount += (it.shippingCost ?: 0).toFloat()
                    totalAmount += ((it.tax ?: 0) * (it.quantity ?: 1))
                    amount += ((it.tax ?: 0) * (it.quantity ?: 1))
                }
            }


            Log.d("total", totalAmount.toString() + "   " + amount.toString())
            val totalString = String.format(Locale.ENGLISH, "%.2f", amount)
            binding.tvTotalAmt.text = totalString

            binding.tvCurrencySymbol.text = cartModel[0].cartItems[0].currencySymbol
            cartModel[0].cartItems[0].overallAmount = totalAmount
            onAmountChange.onTotalAmountChanged(
                position, totalAmount,
                cartModel[0].cartItems[0].currencySymbol.toString()
            )
            amount = 0f
            initCartItemRecyclerView(binding, cartModel[position].cartItems)


        }

        fun amountChange(pos: Int, increment: Int) {
            val totalString = String.format(
                Locale.ENGLISH,
                "%.2f",
                cartModel[position].cartItems[0].shopTotalAmount!!
            )
            binding.tvTotalAmt.text = totalString

            if (increment == 1)
                cartModel[0].cartItems[0].overallAmount =
                    cartModel[0].cartItems[0].overallAmount!! + cartModel[position].cartItems[pos].price!!
            else
                cartModel[0].cartItems[0].overallAmount =
                    cartModel[0].cartItems[0].overallAmount!! - cartModel[position].cartItems[pos].price!!

            onAmountChange.onTotalAmountChanged(
                position, cartModel[0].cartItems[0].overallAmount!!,
                cartModel[0].cartItems[0].currencySymbol.toString()
            )
        }

        override fun onIncrementAmount(pos: Int, increment: Int, currencySymbol: String) {
            amountChange(pos, increment)
        }

        override fun onDecrementAmount(pos: Int, decrement: Int, currencySymbol: String) {
            amountChange(pos, decrement)
        }

        private fun initCartItemRecyclerView(
            binding: ItemCartBinding,
            cartItems: ArrayList<CartModel.CartItems>,
        ) {
            binding.rvCartItem.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
            val cartItemAdapter =
                CartItemAdapter(
                    context,
                    cartItems,
                    this,
                    this,
                    this,
                    actionQuantity,
                    actionUpdateShipEngine,
                    isFromDeliveryInfo
                )
            binding.rvCartItem.adapter = cartItemAdapter
            cartItemAdapter.notifyDataSetChanged()
        }

        override fun onDeleteClick(pos: Int, id: Int) {
            ondeleteItemListerner.onDeleteClick(pos, id)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: CartAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    interface totalAmountChangeListerner {
        fun onTotalAmountChanged(pos: Int, amount: Float, currencySymbol: String)
    }

    interface onDeleteItemListerner {
        fun onDeleteClick(pos: Int, id: Int)
    }

    override fun getItemCount(): Int {
        return cartModel.size
    }


}