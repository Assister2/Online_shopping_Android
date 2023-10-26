package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.databinding.ItemWishlistBinding
import cliqbuy.model.WishlistModel
import com.squareup.picasso.Picasso


class WishListAdapter(val context: Context, private var wishlistModel: WishlistModel, var deleteOnClickListener: DeleteItemOnClickListener,var onitemClick: OnClickListener) :
    RecyclerView.Adapter<WishListAdapter.ViewHolder>() {


    class ViewHolder(var binding:ItemWishlistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemWishlistBinding.inflate(LayoutInflater.from(parent.context),parent,false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemName.text = wishlistModel.data[position].product!!.itemName
        holder.binding.itemPrice.text = wishlistModel.data[position].product!!.itemPrice

        holder.binding.ivDelete.setOnClickListener { deleteOnClickListener.onItemDelete(position) }

        holder.binding.also {
            it.rltWishlist.setOnClickListener { onitemClick.onItemClick(position)  }
        }
        if (wishlistModel.data!![position].product!!.thumbnailImage!!.isEmpty()){
            Picasso.get().load(R.drawable.ic_empty_cart).into(holder.binding.ivItemImage)
        }else{
            Picasso.get().load(context.resources.getString(R.string.imageUrl)+wishlistModel.data!![position].product!!.thumbnailImage).into(holder.binding.ivItemImage)
        }

    }

    override fun getItemCount():Int{
        return wishlistModel.data.size
    }

    interface DeleteItemOnClickListener {
        fun onItemDelete(pos: Int)
    }

    interface OnClickListener {
        fun onItemClick(pos: Int)
    }


}



