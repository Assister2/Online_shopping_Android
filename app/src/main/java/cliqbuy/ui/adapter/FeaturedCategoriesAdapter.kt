package cliqbuy.ui.adapter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
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
import cliqbuy.ui.view.ViewProductActivity
import com.squareup.picasso.Picasso

class FeaturedCategoriesAdapter(val context: Context, val categoryList: CommonHomeModel) : RecyclerView.Adapter<FeaturedCategoriesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val image: ImageView
        val featurecategories : CardView
        init {
           featurecategories = itemView.findViewById(R.id.cv_feature_categories)
            name = itemView.findViewById(R.id.tv_categories_name)
            image = itemView.findViewById(R.id.iv_categories_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.item_feature_categories, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = categoryList.data!![position].name
        if (categoryList.data!![position].banner.isEmpty()) {
            Picasso.get().load(R.drawable.ic_empty_cart).placeholder(R.drawable.ic_empty_cart).into(holder.image)
        }else{
            Picasso.get().load(context.resources.getString(R.string.imageUrl)+categoryList.data!![position].banner)
                .placeholder(R.drawable.ic_empty_cart).into(holder.image)
        }


        holder.featurecategories.setOnClickListener {
            var intent = Intent(context, ViewProductActivity::class.java)
            intent.putExtra("name", categoryList.data!![position].name)
            intent.putExtra("id", categoryList.data!![position].id.toString())
            val animation = ActivityOptions.makeCustomAnimation(
                context,
                R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
            ).toBundle()
            context.startActivity(intent, animation)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.data!!.size
    }
}