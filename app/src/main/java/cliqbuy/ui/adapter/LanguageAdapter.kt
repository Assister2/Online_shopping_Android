package cliqbuy.ui.adapter

import cliqbuy.R
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.databinding.LanguageListBinding


class LanguageAdapter (private val languageList: Array<String>,
                       private val languageCode: Array<String>, private val selectedLanguage : String
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private lateinit var onItemClickListener : (String,String)->Unit

    fun setOnItemClickListener(onItemClickListener : (String,String)->Unit){
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(LanguageListBinding.inflate(LayoutInflater.from(viewGroup.context)))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.bindView()
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    inner class ViewHolder(var binding: LanguageListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(){
            binding.languagenameTxt.text = languageList[adapterPosition]
            binding.radioButton.buttonTintList = getColorList()
            binding.radioButton.isChecked =   selectedLanguage.equals(languageList[adapterPosition],ignoreCase = true)

            binding.root.setOnClickListener {
                onItemClickListener(languageList[adapterPosition], languageCode[adapterPosition]
                )
            }
        }

        fun getColorList() : ColorStateList =
            ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(
                android.R.attr.state_checked)
            ), intArrayOf(getContext().resources.getColor(R.color.gray),
                getContext().resources.getColor(R.color.bg_button)))

        private fun getContext() = binding.root.context
    }
}
