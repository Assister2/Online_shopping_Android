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
import cliqbuy.model.ViewProductModel
import cliqbuy.ui.view.ProductDetailActivity
import com.squareup.picasso.Picasso
import java.util.ArrayList

class ViewProductAdapter (val context: Context, private val viewproductsModel: ViewProductModel) : RecyclerView.Adapter<ViewProductAdapter.ViewHolder>() {
    private var isLoadingAdded = false
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

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.item_feature_products, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = viewproductsModel.data!![position].name
        holder.productPrice.text = viewproductsModel.data!![position].mainPrice
        holder.discountPrice.text = viewproductsModel.data!![position].strokedPrice
        holder.discountPrice.paintFlags = holder.discountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        if (viewproductsModel.data!![position].thumbnailImage!!.isEmpty())
            Picasso.get().load(R.drawable.ic_empty_cart).into(holder.image)
        else
            Picasso.get().load(context.resources.getString(R.string.imageUrl)+viewproductsModel.data!![position].thumbnailImage).placeholder(
                R.drawable.ic_empty_cart).into(holder.image)

        if (viewproductsModel.data!![position].hasDiscount!!)
            holder.discountPrice.visibility = View.VISIBLE
        else
            holder.discountPrice.visibility = View.INVISIBLE

        holder.cvFeatureProducts.setOnClickListener {
            var intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", viewproductsModel.data!![position].id.toString())
            val animation = ActivityOptions.makeCustomAnimation(
                context,
                R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
            ).toBundle()
            context.startActivity(intent, animation)

            if (isLoadingAdded){}
        }

    }

    override fun getItemCount(): Int {
        return viewproductsModel.data!!.size
    }
    fun addLoadingFooter() {
        isLoadingAdded = true

    }
    fun add(produtModel: ViewProductModel.Data) {
        viewproductsModel.data!!.add(produtModel)
        notifyItemInserted(viewproductsModel.data!!.size - 1)
    }

    fun addAll(DetailsModels: ViewProductModel) {
        for (DetailsModels in DetailsModels.data) {
            add(DetailsModels)
        }
    }
}