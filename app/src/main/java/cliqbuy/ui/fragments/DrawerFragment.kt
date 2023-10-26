package cliqbuy.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cliqbuy.R
import cliqbuy.common.CommonFragment
import cliqbuy.databinding.FragmentSearchDrawerBinding
import cliqbuy.model.CategoriesModel
import cliqbuy.ui.adapter.FilterBrandsAdapter
import cliqbuy.ui.adapter.FilterCategoriesAdapter

class DrawerFragment : CommonFragment() {

    private var applyFilterListener: ApplyFilterListener? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var containerView: View? = null
    lateinit var binding: FragmentSearchDrawerBinding
    lateinit var categoriesModel: CategoriesModel
    lateinit var brandModel: CategoriesModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            applyFilterListener = context as ApplyFilterListener
        } catch (e : RuntimeException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSearchDrawerBinding.inflate(layoutInflater)
        binding.tvClear.setOnClickListener {
            binding.edtMaximum.text!!.clear()
            binding.edtMinimum.text!!.clear()
            for (i in categoriesModel.data!!.indices) {
                categoriesModel.data!![i].isCategorySelect = false
            }
            for (i in brandModel.data!!.indices) {
                brandModel.data!![i].isBrandSelect = false
            }
            applyFilterListener!!.onApplyFilter("","","","")
            initBrandsRecyclerView()
            initCategoriesRecyclerView()
        }
        binding.lltBottom.setOnClickListener {

        }
        binding.tvApply.setOnClickListener {
            if (binding.edtMaximum.text.toString().isNotEmpty() && binding.edtMinimum.text.toString().isNotEmpty()){
                if (binding.edtMaximum.text.toString().toFloat() < binding.edtMinimum.text.toString().toFloat()){
                    commonMethods.showToast(requireActivity(),resources.getString(R.string.min_price_check))
                    return@setOnClickListener
                }
            }
            applyFilterListener!!.onApplyFilter(binding.edtMaximum.text.toString(),binding.edtMinimum.text.toString(),brandModel.data[0].selectedBrandId,categoriesModel.data[0].selectedCategoryId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    fun init(fragmentId: Int, drawerLayout: DrawerLayout) {
        if (arguments != null) {
            val bundle = arguments
            categoriesModel = bundle!!.getSerializable("categoriesModel") as CategoriesModel
            brandModel = bundle!!.getSerializable("brandModel") as CategoriesModel
            initCategoriesRecyclerView()
            initBrandsRecyclerView()
        }
        containerView = requireActivity().findViewById(fragmentId)
        mDrawerLayout = drawerLayout
        val drawerToggle = object : ActionBarDrawerToggle(activity, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                activity!!.invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                activity!!.invalidateOptionsMenu()
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                //toolbar!!.alpha = 1 - slideOffset / 2
            }
        }

        mDrawerLayout?.addDrawerListener(drawerToggle)
        mDrawerLayout?.post { drawerToggle.syncState() }

    }

    private fun initCategoriesRecyclerView(){
        binding.rvCategory.layoutManager =  LinearLayoutManager(requireActivity(),
            LinearLayoutManager.VERTICAL, false)
        val filterCategoriesAdapter =
            FilterCategoriesAdapter(requireActivity(),categoriesModel)
        binding.rvCategory.adapter = filterCategoriesAdapter

    }

    private fun initBrandsRecyclerView(){
        binding.rvBrands.layoutManager =  LinearLayoutManager(requireActivity(),
            LinearLayoutManager.VERTICAL, false)
        val filterCategoriesAdapter =
            FilterBrandsAdapter(requireActivity(),brandModel)
        binding.rvBrands.adapter = filterCategoriesAdapter

    }



    interface FragmentDrawerListener {
        fun onDrawerItemSelected(view: View, position: Int)
    }

    interface ApplyFilterListener {
        fun onApplyFilter(max: String,min : String , brandId : String,categoryId : String)
    }
}