package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.databinding.ItemFilterCheckBinding
import cliqbuy.model.CategoriesModel


class FilterBrandsAdapter(val context: Context, val brandsModel: CategoriesModel) : RecyclerView.Adapter<FilterBrandsAdapter.ViewHolder>() {


    inner class ViewHolder(var binding: ItemFilterCheckBinding) :RecyclerView.ViewHolder(binding.root){
        var brandIds = ""
        fun bindData(position: Int) {
            binding.tvName.text = brandsModel.data[position].name

            binding.cbFilter.isChecked=brandsModel.data[position].isBrandSelect

            binding.rltFilter.setOnClickListener {
                brandIds = ""
                brandsModel.data[position].isBrandSelect = !brandsModel.data[position].isBrandSelect
                binding.cbFilter.isChecked = brandsModel.data[position].isBrandSelect
                for (i in brandsModel.data.indices){
                    if (brandsModel.data[i].isBrandSelect){
                        brandIds += brandsModel.data[i].id.toString() + ","
                        brandsModel.data[0].selectedBrandId = brandIds.substring(0, brandIds.length - 1)
                    }

                }

            }
        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterBrandsAdapter.ViewHolder = ViewHolder(
        ItemFilterCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: FilterBrandsAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return brandsModel.data.size
    }

}