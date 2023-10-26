package cliqbuy.ui.adapter


import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.databinding.CategoryItemBinding
import cliqbuy.model.CategoriesModel
import cliqbuy.ui.view.ViewProductActivity
import cliqbuy.utils.showToast
import com.squareup.picasso.Picasso

class CategoryAdapter(private val onClick: (CategoriesModel.Data) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var categoryList: ArrayList<CategoriesModel.Data> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(categoryList.get(position))
    }

    override fun getItemCount(): Int = categoryList.size

    inner class ViewHolder(var binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(item: CategoriesModel.Data) = with(binding) {
            catName.text = item.name
            loadImage(item.icon, itemLogo)

            btnSubcategory.alpha = if (!(item.numberOfChildren > 0)) 0.2F else 1.0F
            viewProduct.alpha = if (!(item.productsCount > 0)) 0.2F else 1.0F
            btnSubcategory.setOnClickListener {
                if ((item.numberOfChildren > 0))
                    onClick.invoke(item)
                else {
                    getContext().showToast(getContext().resources.getString(R.string.subcategories_notavailable))
                }
            }
            viewProduct.setOnClickListener {
                if ((item.productsCount > 0))
                {
                    var intent = Intent(getContext(), ViewProductActivity::class.java)
                    intent.putExtra("name", categoryList[position].name)
                    intent.putExtra("id", categoryList[position].id.toString())
                    val animation = ActivityOptions.makeCustomAnimation(
                        getContext(),
                        R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
                    ).toBundle()
                    getContext().startActivity(intent, animation)
                }
                else {
                    getContext().showToast(getContext().resources.getString(R.string.products_notavailable))
                }

            }
        }

        fun loadImage(path: String?, view: ImageView) = path?.let {
            // if (it.isNotEmpty())
            Picasso.get().load(getContext().resources.getString(R.string.imageUrl) + it)
                .placeholder(getContext().resources.getDrawable(R.drawable.ic_empty_cart))
                .into(view)
        }

        fun getContext() = binding.root.context
    }

}