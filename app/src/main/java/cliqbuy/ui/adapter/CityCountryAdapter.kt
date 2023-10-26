package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.model.CityListModel
import cliqbuy.model.CommonHomeModel
import cliqbuy.model.CountryListModel
import cliqbuy.model.StateListModel

class CityCountryAdapter(val context : Context,val countyList: CountryListModel,val cityList: CityListModel,val stateList: StateListModel, val type : Int, var onClickListener : OnClickListener) : RecyclerView.Adapter<CityCountryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCityCountryName: TextView


        init {
            tvCityCountryName = itemView.findViewById(R.id.tv_city_country_name)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.item_city_country_list, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCityCountryName.text = when (type) {
            0 -> cityList.data!![position].name
            1 -> countyList.data!![position].name
            else -> stateList.data!![position].name
        }

        holder.tvCityCountryName.setOnClickListener { onClickListener.onClick(position,type) }
    }

    interface OnClickListener {
        fun onClick(pos:Int,type: Int)
    }

    override fun getItemCount(): Int {
        return when (type) {
            0 -> cityList.data!!.size
            1 -> countyList.data!!.size
            else -> stateList.data!!.size
        }

    }
}