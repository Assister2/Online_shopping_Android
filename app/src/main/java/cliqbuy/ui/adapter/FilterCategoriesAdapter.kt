package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.databinding.ItemFilterCheckBinding
import cliqbuy.model.CategoriesModel


class FilterCategoriesAdapter(val context: Context, val categoriesModel: CategoriesModel) : RecyclerView.Adapter<FilterCategoriesAdapter.ViewHolder>() {


    inner class ViewHolder(var binding: ItemFilterCheckBinding) :RecyclerView.ViewHolder(binding.root){

        fun bindData(position: Int) {
            binding.tvName.text = categoriesModel.data[position].name

            binding.cbFilter.isChecked=categoriesModel.data[position].isCategorySelect

            binding.rltFilter.setOnClickListener {
                if (categoriesModel.data[position].id.toString() != categoriesModel.data[0].selectedCategoryId || categoriesModel.data[0].selectedCategoryId == "" ) {
                    categoriesModel.data[0].selectedCategoryId =
                        categoriesModel.data[position].id.toString()
                    binding.cbFilter.isChecked = true
                    categoriesModel.data[position].isCategorySelect =
                        !categoriesModel.data[position].isCategorySelect
                    for (i in 0 until categoriesModel.data.size) {
                        if (categoriesModel.data[i].isCategorySelect && i != position) {
                            categoriesModel.data[i].isCategorySelect =
                                !categoriesModel.data[i].isCategorySelect
                            notifyItemChanged(i)
                            break
                        }
                    }
                }else{
                    categoriesModel.data[0].selectedCategoryId = ""
                    binding.cbFilter.isChecked = false
                }
            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterCategoriesAdapter.ViewHolder = ViewHolder(
        ItemFilterCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: FilterCategoriesAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return categoriesModel.data.size
    }

}