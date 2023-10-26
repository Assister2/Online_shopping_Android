package cliqbuy.utils

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.gone(){
    visibility = View.GONE
}
fun View.visible(){
    visibility = View.VISIBLE
}
fun View.inVisible(){
    visibility = View.INVISIBLE
}

fun Context.showToast(msg : String)
     { if (!msg.isEmpty()) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }