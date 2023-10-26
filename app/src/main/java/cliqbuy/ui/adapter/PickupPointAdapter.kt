package cliqbuy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.model.PickupPointModel

class PickupPointAdapter(val context: Context, private val pickupPointModel: PickupPointModel, private val pickupPointInterface : PickupPointAdapterInterface) : RecyclerView.Adapter<PickupPointAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val phone: TextView
        val location: TextView
        val pickupLayout: CardView
        val ivTick: ImageView
        init {
            name = itemView.findViewById(R.id.edt_name)
            phone = itemView.findViewById(R.id.edt_phone)
            location = itemView.findViewById(R.id.edt_location)
            pickupLayout = itemView.findViewById(R.id.cv_pickup_layout)
            ivTick = itemView.findViewById(R.id.iv_tick)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PickupPointAdapter.ViewHolder {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.item_pickup_point_list, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = pickupPointModel.data!![position].name
        holder.phone.text = pickupPointModel.data!![position].phone
        holder.location.text = pickupPointModel.data!![position].address

        holder.pickupLayout.setOnClickListener {
            pickupPointInterface.onPickupPointClick(position)
        }

        if (pickupPointModel.data!![position].isSelected)
            holder.ivTick.visibility = View.VISIBLE
        else
            holder.ivTick.visibility = View.GONE

    }


    interface PickupPointAdapterInterface {
        fun onPickupPointClick(value:Int)
    }

    override fun getItemCount(): Int {
        return pickupPointModel.data!!.size
    }
}