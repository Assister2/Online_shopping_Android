package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.databinding.ItemPreviewProductBinding
import cliqbuy.databinding.ItemSimilirarproductsBinding
import cliqbuy.model.ProductDataModel
import cliqbuy.ui.view.ProductDetailActivity
import com.squareup.picasso.Picasso

class ProductPreviewAdapter(val context: Context,val photos: ArrayList<ProductDataModel.Photos>, var onClickProductListerner: onProductClickListerner
) : RecyclerView.Adapter<ProductPreviewAdapter.ViewHolder> (){

    class ViewHolder(var binding : ItemPreviewProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPreviewProductBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (photos[position].path.isEmpty()) Picasso.get().load(R.drawable.ic_empty_cart_200).into(holder.binding.ivProductsImage)
        else Picasso.get().load(context.resources.getString(R.string.imageUrl)+photos[position].path).placeholder(
            R.drawable.ic_empty_cart_200).into(holder.binding.ivProductsImage)

        holder.binding.lltProductBackground.setOnClickListener {
            onClickProductListerner.onClickProduct(position)
        }
        if (photos[position].isSelected){
            holder.binding.lltProductBackground.setBackgroundResource(R.drawable.bg_preview_selected)
        }else{
            holder.binding.lltProductBackground.setBackgroundResource(R.drawable.bg_curved_window_tiny)
        }

    }

    override fun getItemCount(): Int = photos.size

    interface onProductClickListerner {
        fun onClickProduct(pos: Int)
    }
}