package cliqbuy.ui.adapter

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.helper.CommonKeys.showTimeFormat
import cliqbuy.model.CommonHomeModel
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit


class FlashDealAdapter(val context: Context,private var flashModel: CommonHomeModel, var itemOnClickListener: ItemOnClickListener) :
    RecyclerView.Adapter<FlashDealAdapter.ViewHolder>() {

    private val currentMilliSec = System.currentTimeMillis()
    var milliSec: Long = 0
    var timeAdder= "000"
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.iv_flash)
        val title: TextView = itemView.findViewById(R.id.tv_flash_title)
        val timer: TextView = itemView.findViewById(R.id.tv_time)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.item_flash_deal, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if (flashModel.data!!.size>0){

            if (flashModel.data!![position].banner.isEmpty()) Picasso.get().load(R.drawable.ic_empty_cart_200).into(holder.image)
            else Picasso.get().load(context.resources.getString(R.string.imageUrl)+flashModel.data!![position].banner).placeholder(R.drawable.ic_empty_cart_200).into(holder.image)

            holder.also {
                it.image.setOnClickListener { itemOnClickListener.onItemClick(position) }
                it.timer.setOnClickListener { itemOnClickListener.onItemClick(position) }
                it.title.setOnClickListener { itemOnClickListener.onItemClick(position) }
            }
            /*
            holder.image.setOnClickListener{
                itemOnClickListener.onItemClick(position)
            }
*/
            holder.title.text = flashModel.data!![position].title

            val sum = flashModel.data!![position].flashDealTimer.toString()+timeAdder

            var reservationloadtime = sum.toLong()
            reservationloadtime=currentMilliSec-reservationloadtime.toLong();
            milliSec -= reservationloadtime;

            if ( (flashModel.data!![position].flashDealTimer.toString()+timeAdder).toLong() <= currentMilliSec){
                milliSec =0
            }

            object : CountDownTimer(milliSec, 1000) {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onTick(millisUntilFinished: Long) {
                    val millis = millisUntilFinished
                    val hms = String.format(
                        showTimeFormat,
                        TimeUnit.MILLISECONDS.toDays(millis),
                        TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(
                            TimeUnit.MILLISECONDS.toDays(
                                millis
                            )
                        ),  //TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(
                                millis
                            )
                        ),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                millis
                            )
                        )
                    )
                    Log.e("timestamp",hms);
                    holder.timer.text = hms
                }

                // When the task is over it will print 00:00:00 there
                override fun onFinish() {
                    holder.timer.text =context.resources.getString(R.string.ended)
                }
            }.start()


        }



    }

    private fun callTimer(flashDealTimer: Long) {


    }

    override fun getItemCount(): Int {
        return flashModel.data!!.size
    }


    interface ItemOnClickListener {
        fun onItemClick(pos: Int)
    }


}