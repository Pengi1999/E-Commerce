package com.nhathao.e_commerce.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.adapters.RvAdapterProduct
import com.nhathao.e_commerce.models.Product

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var dbRef : DatabaseReference
    private lateinit var dsProduct: ArrayList<Product>
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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val rvNew = view.findViewById<RecyclerView>(R.id.rvNew)

        dbRef = FirebaseDatabase.getInstance().getReference("Products")
        dsProduct = arrayListOf<Product>()

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dsProduct.clear()
                if(snapshot.exists()){
                    for (productSnap in snapshot.children){
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
                        dsProduct.add(productData)
                    }
                    val mAdapter = RvAdapterProduct(dsProduct)
                    rvNew.adapter = mAdapter
                    rvNew.layoutManager = LinearLayoutManager(
                        this@HomeFragment.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}