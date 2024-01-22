package com.nhathao.e_commerce.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.adapters.RvAdapterCategory
import com.nhathao.e_commerce.adapters.RvAdapterProduct
import com.nhathao.e_commerce.models.Product

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CatalogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CatalogFragment : Fragment() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var btnBack: ImageButton
    private lateinit var btnStyleShowList: ImageButton
    private lateinit var txtCategory: TextView
    private lateinit var rcvCategory: RecyclerView
    private lateinit var dsCategory: ArrayList<String>
    private lateinit var dsProduct: ArrayList<Product>
    private lateinit var rcvProduct: RecyclerView
    private lateinit var adapterProduct:RvAdapterProduct
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        dbRef = FirebaseDatabase.getInstance().getReference("Products")
        dsCategory = arrayListOf<String>()
        dsProduct = arrayListOf<Product>()

        btnBack = view.findViewById(R.id.btnBack)
        btnStyleShowList = view.findViewById(R.id.btnStyleShowList)
        txtCategory = view.findViewById(R.id.txtCategory)
        rcvCategory = view.findViewById(R.id.rcvCategory)
        rcvProduct = view.findViewById(R.id.rcvProduct)

        val sex = this.activity?.intent?.getStringExtra("Sex")
        val type = this.activity?.intent?.getStringExtra("Type")
        val category = this.activity?.intent?.getStringExtra("Category")

        txtCategory.setText("$sex's $category")

        dbRef.get().addOnSuccessListener {
            if(it.exists()) {
                for (productSnap in it.children){
                    if (productSnap.child("productSex").value.toString() == sex &&
                        productSnap.child("productType").value.toString() == type) {
                        val productData = Product(
                            productSnap.child("productId").value.toString(),
                            productSnap.child("productName").value.toString(),
                            productSnap.child("productDescribe").value.toString(),
                            productSnap.child("productImage").value.toString(),
                            productSnap.child("productSex").value.toString(),
                            productSnap.child("productType").value.toString(),
                            productSnap.child("productCategory").value.toString(),
                            productSnap.child("productBrand").value.toString(),
                            productSnap.child("productMode").value.toString(),
                            productSnap.child("productPrice").value.toString().toInt(),
                            productSnap.child("productRating").value.toString().toFloat(),
                            productSnap.child("productRatingQuantity").value.toString().toInt(),
                        )
                        if (category == "All"){
                            dsProduct.add(productData)
                        }
                        else if (category == "Tops") {
                            if (productData.productCategory == "Shirt" ||
                                productData.productCategory == "Blouse" ||
                                productData.productCategory == "Dress") {
                                dsProduct.add(productData)
                            }
                        }
                        else {
                            if (productData.productCategory == category) {
                                dsProduct.add(productData)
                            }
                        }
                        if (!dsCategory.contains(productSnap.child("productCategory").value.toString())) {
                            dsCategory.add(productSnap.child("productCategory").value.toString())
                        }
                    }
                }
            }
        }.addOnFailureListener{
            Log.wtf("firebase", "Error getting data", it)
        }

        val adapterCategory = RvAdapterCategory(dsCategory)
        rcvCategory.adapter = adapterCategory
        rcvCategory.layoutManager = LinearLayoutManager(
            this.requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        adapterProduct = RvAdapterProduct(dsProduct, R.layout.layout_item2)
        rcvProduct.adapter = adapterProduct
        rcvProduct.layoutManager = LinearLayoutManager(
            this.requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        btnBack.setOnClickListener {
            this.activity?.supportFragmentManager?.popBackStack()
        }

        btnStyleShowList.setOnClickListener {

            adapterProduct = RvAdapterProduct(dsProduct, R.layout.layout_item)
            rcvProduct.adapter = adapterProduct
            rcvProduct.layoutManager = GridLayoutManager(
                this.requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CatalogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CatalogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}