package cliqbuy.ui.adapter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.model.CommonHomeModel
import cliqbuy.ui.view.ProductDetailActivity
import cliqbuy.ui.view.ProfileActivity
import com.squareup.picasso.Picasso

class FeaturedProductsAdapter(val context: Context, private val productsModel: CommonHomeModel) : RecyclerView.Adapter<FeaturedProductsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val productPrice: TextView
        val discountPrice: TextView
        val image: ImageView
        val cvFeatureProducts: CardView

        init {
            name = itemView.findViewById(R.id.tv_products_name)
            productPrice = itemView.findViewById(R.id.tv_products_price)
            discountPrice = itemView.findViewById(R.id.tv_discount_price)
            image = itemView.findViewById(R.id.iv_products_image)
            cvFeatureProducts = itemView.findViewById(R.id.cv_feature_products)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater: LayoutInflater = LayoutInflater.from(parent.getContext())
        when (viewType) {
            NEW_ARRIVAL -> {
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_arrivals, parent, false)
                viewHolder = ViewHolder(view)
            }
            TOP_SELLING -> {
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_selling, parent, false)
                viewHolder = ViewHolder(view)
            }
            FEATURED_PRODUCTS -> {
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature_products, parent, false)
                viewHolder = ViewHolder(view)
            }
        }
        return (viewHolder as ViewHolder?)!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = productsModel.data!![position].name
        holder.productPrice.text = productsModel.data!![position].mainPrice
        holder.discountPrice.text = productsModel.data!![position].strokedPrice
        holder.discountPrice.paintFlags = holder.discountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        if (productsModel.data!![position].thumbnailImage.isEmpty())
            Picasso.get().load(R.drawable.ic_empty_cart).into(holder.image)
        else
            Picasso.get().load(context.resources.getString(R.string.imageUrl)+productsModel.data!![position].thumbnailImage).placeholder(R.drawable.ic_empty_cart).into(holder.image)

        if (productsModel.data!![position].hasDiscount)
            holder.discountPrice.visibility = View.VISIBLE
        else
            holder.discountPrice.visibility = View.INVISIBLE

        holder.cvFeatureProducts.setOnClickListener {
            var intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", productsModel.data!![position].id.toString())
            val animation = ActivityOptions.makeCustomAnimation(
                context,
                R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
            ).toBundle()
            context.startActivity(intent, animation)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (productsModel.type) {
            "new_arrival" -> {
                NEW_ARRIVAL
            }
            "top_selling" -> {
                TOP_SELLING
            }
            else -> {
                FEATURED_PRODUCTS
            }
        }
    }

    override fun getItemCount(): Int {
        return productsModel.data!!.size
    }

    companion object {
        private const val NEW_ARRIVAL = 0
        private const val TOP_SELLING = 1
        private const val FEATURED_PRODUCTS = 2
    }
}