package cliqbuy.ui.adapter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.model.SellerModels
import cliqbuy.ui.view.SellerDetailsActivity
import cliqbuy.ui.view.ViewProductActivity
import com.squareup.picasso.Picasso

class SellerProductsAdapter(val context: Context, private val sellerModel: SellerModels,val isSeller : Boolean) : PagedListAdapter<Any, SellerProductsAdapter.ViewHolder>(SellerproductDiffCallback) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val itemprice: TextView
        val image: ImageView
        val rltItem: RelativeLayout

        init {
            name = itemView.findViewById(R.id.search_item_name)
            image = itemView.findViewById(R.id.search_item_image)
            itemprice = itemView.findViewById(R.id.search_item_price)
            rltItem = itemView.findViewById(R.id.rlt_item)
        }
    }

    companion object{
        var SellerproductDiffCallback = object : DiffUtil.ItemCallback<Any>(){
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return (oldItem as SellerModels.SellerData).id === (newItem as SellerModels.SellerData).id
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return (oldItem as SellerModels.SellerData).id == (newItem as SellerModels.SellerData).id

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = sellerModel.data!![position].name

        holder.itemprice.visibility=View.GONE
        if (sellerModel.data!![position].logo!!.isEmpty())
            Picasso.get().load(R.drawable.ic_empty_cart).error(R.drawable.ic_empty_cart).placeholder(R.drawable.ic_empty_cart).into(holder.image)
        else
            Picasso.get().load(context.resources.getString(R.string.imageUrl)+sellerModel.data!![position].logo).placeholder(R.drawable.ic_empty_cart).into(holder.image)


        /*Glide.with(context)
            .load(context.resources.getString(R.string.imageUrl)+sellerModel.data!![position].logo) // image url
            .placeholder(R.drawable.ic_empty_cart)
            .error(R.drawable.ic_empty_cart)
            .override(200, 200)
            .centerCrop()
            .into(holder.image)*/
        holder.rltItem.setOnClickListener {
            if(isSeller) {
                val intent = Intent(context, SellerDetailsActivity::class.java)
                intent.putExtra("id", sellerModel.data!![position].id)
                context.startActivity(intent)
            }else{
                var intent = Intent(context, ViewProductActivity::class.java)
                intent.putExtra("name", sellerModel.data!![position].name)
                intent.putExtra("id", sellerModel.data!![position].id.toString())
                intent.putExtra("type",2)
                val animation = ActivityOptions.makeCustomAnimation(
                    context,
                    R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
                ).toBundle()
                context.startActivity(intent, animation)
            }
        }

    }

    override fun getItemCount(): Int {
        return sellerModel.data!!.size
    }

    object DataDifferntiator : DiffUtil.ItemCallback<SellerModels.SellerData>() {

        override fun areItemsTheSame(oldItem: SellerModels.SellerData, newItem: SellerModels.SellerData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SellerModels.SellerData, newItem: SellerModels.SellerData): Boolean {
            return true
        }
    }

    fun add(produtModel: SellerModels.SellerData) {
        sellerModel.data!!.add(produtModel)

    }

    fun addAll(DetailsModels: SellerModels) {

        for (DetailsModels in DetailsModels.data) {
            add(DetailsModels)
        }

    }
    fun clearAll() {

        sellerModel.data!!.clear()
        notifyDataSetChanged()
    }
}