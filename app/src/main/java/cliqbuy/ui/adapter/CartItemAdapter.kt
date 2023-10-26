package cliqbuy.ui.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.databinding.ActivityCartItemBinding
import cliqbuy.model.CartModel
import com.squareup.picasso.Picasso
import java.util.Locale


class CartItemAdapter(
    var context: Context,
    val cartItems: ArrayList<CartModel.CartItems>,
    var onincreamentListerner: increamentListerner,
    var onDecreamentListerner: decreamentListerner,
    var ondeleteItemListerner: onDeleteItemListerner,
    var actionQuantity: ((Int, Int) -> Unit)?,
    var actionUpdateShipEngine: ((Int, String) -> Unit)?,
    var isFromDeliveryInfo: Boolean = false,
) : RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {


    inner class ViewHolder(var binding: ActivityCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int) {
            var selectedItem = 0
            if (cartItems[position].productThumbnailImage!!.isEmpty()) Picasso.get()
                .load(R.drawable.ic_empty_cart_200).into(binding.ivProductsImage)
            else Picasso.get()
                .load(context.resources.getString(R.string.imageUrl) + cartItems[position].productThumbnailImage!!)
                .placeholder(
                    R.drawable.ic_empty_cart_200
                ).into(binding.ivProductsImage)

            binding.tvProductName.text = cartItems[position].productName
            binding.tvCount.text = cartItems[position].quantity.toString()
            binding.tvCurrencySymbol.text = cartItems[position].currencySymbol

            cartItems[position].itemTotalAmount =
                cartItems[position].price!! * cartItems[position].quantity!!

            val amountString =
                String.format(Locale.ENGLISH, "%.2f", cartItems[position].itemTotalAmount!!)
            binding.tvAmount.text = amountString
            if (binding.tvCount.text.toString().toInt() == cartItems[position].lowerLimit) {
                binding.tvMinus.setTextColor(context.resources.getColor(R.color.txt_four))
                binding.tvMinus.isEnabled = false
            }
            binding.tvPlus.setOnClickListener {
                increment(position)
                if (isFromDeliveryInfo)
                    actionQuantity?.invoke(
                        cartItems[position].id ?: 0,
                        cartItems[position].quantity ?: 0
                    )
            }
            binding.tvMinus.setOnClickListener {
                if (binding.tvCount.text.toString().toInt() == cartItems[position].lowerLimit) {
                    binding.tvMinus.setTextColor(context.resources.getColor(R.color.txt_four))
                    binding.tvMinus.isEnabled = false
                } else {
                    decrement(position)
                    if (isFromDeliveryInfo)
                        actionQuantity?.invoke(
                            cartItems[position].id ?: 0,
                            cartItems[position].quantity ?: 0
                        )
                }
            }
            binding.tvDelete.setOnClickListener {
                ondeleteItemListerner.onDeleteClick(
                    position,
                    cartItems[position].id!!
                )
            }


            if (isFromDeliveryInfo) {
                binding.lltShipengine.visibility = View.VISIBLE

                if ((cartItems[position].shipengineData?.shipengine ?: false)) {
                    binding.llShipingandhandlingspinner.visibility = View.VISIBLE
           /*         binding.llShipingandhandling.visibility = View.VISIBLE
                    binding.llSalestax.visibility = View.VISIBLE
                    binding.llOrdertotal.visibility = View.VISIBLE*/
                } else {
                    binding.llShipingandhandlingspinner.visibility = View.GONE
                  /*  binding.llShipingandhandling.visibility = View.VISIBLE
                    binding.llSalestax.visibility = View.VISIBLE
                    binding.llOrdertotal.visibility = View.VISIBLE*/
                }
            } else {
                binding.lltShipengine.visibility = View.GONE
            }

            var spinnerList = mutableListOf<String>()

            cartItems[adapterPosition].shipengineData?.estimateData?.let {
                if (it.size > 0) {
                    for (i in 0..(it.size - 1)) {
                        if (it.get(i).serviceCode.equals(cartItems[position].serviceCode) &&
                            it.get(i).packageType.equals(cartItems[position].packageType)
                        ) {
                            binding.shipengineSpinner.setSelection(i)
                            selectedItem = i

                        }
                    }
                }
            }



            cartItems[position].shipengineData?.estimateData?.forEach {
                var txt =
                    it.serviceType + " " + it.shippingAmount?.amount.toString() + " (" + it.deliveryDays + ") " + context.getString(
                        R.string.day
                    )
                spinnerList.add(txt)
            }

            Log.d("spinnerData", spinnerList.toString())

            var count = 0;
            binding.shipengineSpinner.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long,
                ) {

                    if (count++ == 0)
                        return
                    Log.d(
                        "spinnier",
                        "adapterPostion $adapterPosition  pos $pos  " + cartItems[adapterPosition].shipengineData?.estimateData?.size.toString()
                    )
                    actionUpdateShipEngine?.invoke(
                        cartItems[position].id ?: 0,
                        cartItems[adapterPosition].shipengineData?.estimateData?.get(pos)?.rateId.toString()
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            })


            val dataAdapter: ArrayAdapter<String> =
                object : ArrayAdapter<String>(context, R.layout.item_spinner, spinnerList) {
                    override fun getDropDownView(
                        pos: Int,
                        convertView: View?,
                        parent: ViewGroup,
                    ): View {

                        val v = super.getDropDownView(pos, convertView, parent)
                        // If this is the selected item position
                        Log.d("postions", cartItems.get(position).selectedPos.toString())
                        if (pos == cartItems.get(position).selectedPos) {
                            v.setBackgroundColor(Color.LTGRAY)
                        } else {
                            // for other views
                            v.setBackgroundColor(Color.WHITE)
                        }
                        return v
                    }
                }

            binding.shipengineSpinner.setAdapter(dataAdapter)

            /*   binding.shipengineSpinner.setAdapter(
                   ArrayAdapter<Any?>(
                       context, R.layout.item_spinner,
                       spinnerList.toList()
                   )
               )*/

            if ((cartItems[position].shippingCost ?: 0).toFloat() > 0.0f) {
                binding.valueTitleShipfee.text =
                    cartItems[position].currencySymbol + "" + cartItems[position].shippingCost.toString()
            } else {
                binding.valueTitleShipfee.text = context.getString(R.string.free)
            }

            binding.valueSalestax.text =
                cartItems[position].currencySymbol + "" + ((cartItems[position].tax
                    ?: 0) * (cartItems[position].quantity ?: 1)).toString()

            var price = ((cartItems[position].price ?: 0f) * (cartItems[position].quantity ?: 1))
            var shippingCost = (cartItems[position].shippingCost ?: 0.0)
            var tax = ((cartItems[position].tax ?: 0) * (cartItems[position].quantity ?: 1))
            var total = shippingCost.toFloat() + tax + price

            binding.valueOrdertotal.text =
                cartItems[position].currencySymbol + "" + total

        }

        fun increment(pos: Int) {
            if (cartItems[pos].upperLimit!! != binding.tvCount.text.toString().toInt()) {
                val incrementString = String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    cartItems[pos].itemTotalAmount!! + cartItems[pos].price!!
                )
                binding.tvAmount.text = incrementString
                cartItems[pos].itemTotalAmount =
                    cartItems[pos].itemTotalAmount!! + cartItems[pos].price!!
                cartItems[0].shopTotalAmount =
                    cartItems[0].shopTotalAmount!! + cartItems[pos].price!!
                cartItems[pos].quantity = cartItems[pos].quantity!! + 1
                binding.tvMinus.setTextColor(context.resources.getColor(R.color.txt_primary))
                binding.tvMinus.isEnabled = true
                binding.tvCount.text = (binding.tvCount.text.toString().toInt() + 1).toString()
                onincreamentListerner.onIncrementAmount(pos, 1, "")
            } else {
                binding.tvPlus.setTextColor(context.resources.getColor(R.color.txt_four))
                binding.tvPlus.isEnabled = false
            }
        }

        fun decrement(pos: Int) {
            if (cartItems[pos].lowerLimit!! != binding.tvCount.text.toString().toInt()) {
                val decrementString = String.format(
                    Locale.ENGLISH,
                    "%.2f",
                    cartItems[pos].itemTotalAmount!! - cartItems[pos].price!!
                )
                binding.tvAmount.text = decrementString
                cartItems[pos].itemTotalAmount =
                    cartItems[pos].itemTotalAmount!! - cartItems[pos].price!!
                cartItems[0].shopTotalAmount =
                    cartItems[0].shopTotalAmount!! - cartItems[pos].price!!
                cartItems[pos].quantity = cartItems[pos].quantity!! - 1
                binding.tvPlus.setTextColor(context.resources.getColor(R.color.txt_primary))
                binding.tvPlus.isEnabled = true
                onDecreamentListerner.onDecrementAmount(pos, 0, "")
                binding.tvCount.text = (binding.tvCount.text.toString().toInt() - 1).toString()
                if (binding.tvCount.text.toString().toInt() == cartItems[pos].lowerLimit) {
                    binding.tvMinus.setTextColor(context.resources.getColor(R.color.txt_four))
                    binding.tvMinus.isEnabled = false
                }
            } else {
                binding.tvMinus.setTextColor(context.resources.getColor(R.color.txt_four))
                binding.tvMinus.isEnabled = false
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ActivityCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    interface increamentListerner {
        fun onIncrementAmount(pos: Int, increment: Int, currencySymbol: String)
    }

    interface decreamentListerner {
        fun onDecrementAmount(pos: Int, increment: Int, currencySymbol: String)
    }

    interface onDeleteItemListerner {
        fun onDeleteClick(pos: Int, id: Int)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }
}