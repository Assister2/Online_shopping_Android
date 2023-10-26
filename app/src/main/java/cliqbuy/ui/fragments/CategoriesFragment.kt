package cliqbuy.ui.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import cliqbuy.R
import cliqbuy.common.CommonFragment
import cliqbuy.databinding.FragmentCategoriesBinding
import cliqbuy.helper.Enums
import cliqbuy.helper.Enums.REQ_TOP_CATEGORY
import cliqbuy.model.CategoriesModel
import cliqbuy.ui.adapter.CategoryAdapter
import cliqbuy.ui.view.HomeActivity
import cliqbuy.ui.view.ViewProductActivity


class CategoriesFragment : CommonFragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(layoutInflater)
        initAllViews()
        binding.btnViewallproducts.setOnClickListener {
            var intent = Intent(requireActivity(), ViewProductActivity::class.java)
            intent.putExtra("name", getData().name)
            intent.putExtra("id", getData().id.toString())
            val animation = ActivityOptions.makeCustomAnimation(
                requireActivity(),
                R.anim.ub__slide_in_right, R.anim.ub__slide_out_left
            ).toBundle()
            requireActivity().startActivity(intent, animation)
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initApiResponseHandler()

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setHeader(
            getTitle(), !getData().name.isEmpty()
        )
    }

    fun initAllViews() {
        setVisibilityOfViews(binding.btnViewallproducts, !getData().name.isEmpty())
        setText(binding.btnViewallproducts, requireContext().resources.getString(R.string.all_products) + " " + getData().name)
        initCategoryViews()
        initApiCall()
    }

    fun setVisibilityOfViews(view: View, visible: Boolean) = apply {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setText(view: TextView, text: String) = apply {
        view.text = text
    }

    fun getData(): CategoriesModel.Data = arguments?.getSerializable("bundleKey")?.let {
        (it as CategoriesModel.Data)
    } ?: CategoriesModel.Data()

    fun getTitle() = if (getData().name.isEmpty()) {
        if (arguments?.getString("comeFrom").equals("1"))
            requireContext().resources.getString(R.string.categories) else
            requireContext().resources.getString(R.string.top_categories)
    } else getData().name


    fun initCategoryViews() {
        categoryAdapter = CategoryAdapter {onItemClicked(it)}
        binding.rvCategoryList.adapter = categoryAdapter
    }

    fun onItemClicked( data : CategoriesModel.Data){
        val bundle = Bundle().apply {
            putSerializable("bundleKey", data)
            putString("comeFrom", arguments?.getString("comeFrom"))
        }
        setFragmentResult("requestKey", bundle)
    }

    fun initApiCall(){
        if (getData().name.isEmpty()) {
            if (arguments?.getString("comeFrom").equals("1"))
                callCategoriesApi(getData().id.toString())
            else
                callTopCategoriesApi()
        }
        else callCategoriesApi(getData().id.toString())
    }

    fun callCategoriesApi(id: String) {
        commonViewModel!!.apiRequest(Enums.REQ_CATEGORY, HashMap<String, String>().apply {
            put("parent_id", id)
        },requireContext())
    }

    fun callTopCategoriesApi() {
        commonViewModel!!.apiRequest(Enums.REQ_TOP_CATEGORY, HashMap(), requireContext())
    }

    private fun initApiResponseHandler() {
        commonViewModel?.liveDataResponse?.observe(requireActivity(), androidx.lifecycle.Observer {
            if (commonViewModel?.apiResponseHandler!!.isSuccess) {
                when (commonViewModel!!.responseCode) {
                    Enums.REQ_CATEGORY, REQ_TOP_CATEGORY -> {
                        categoryAdapter.categoryList = (it as CategoriesModel).data
                    }
                }
            }else{
                commonMethods.hideProgressDialog()
            }
        })
    }

    companion object {
        fun newInstance(): CategoriesFragment {
            return CategoriesFragment()
        }
    }

}