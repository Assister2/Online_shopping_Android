package cliqbuy.model

import com.google.gson.annotations.SerializedName

data class CategoriesModel(
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf<Data>(),
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("status") var status: Int? = null

) :java.io.Serializable{
    data class Data(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("banner") var banner: String? = null,
        @SerializedName("icon") var icon: String? = null,
        @SerializedName("number_of_children") var numberOfChildren: Int = 0,
        @SerializedName("products_count") var productsCount: Int = 0,
        @SerializedName("links") var links: Links? = Links(),
        @SerializedName("is_category_select") var isCategorySelect: Boolean = false,
        @SerializedName("is_brand_select") var isBrandSelect: Boolean = false,
        @SerializedName("selected_category_id") var selectedCategoryId: String = "",
        @SerializedName("selected_brand_id") var selectedBrandId: String = ""

    ) :java.io.Serializable

    data class Links(

        @SerializedName("products") var products: String? = null,
        @SerializedName("sub_categories") var subCategories: String? = null

    ) :java.io.Serializable
}