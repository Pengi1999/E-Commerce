package com.nhathao.e_commerce.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.Interfaces.RvInterface
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
    private lateinit var dbRef: DatabaseReference
    private lateinit var dsProductSale: ArrayList<Product>
    private lateinit var dsProductNew: ArrayList<Product>
    private lateinit var smallBanner: RelativeLayout
    private lateinit var bigBanner: RelativeLayout
    private lateinit var btnCheckSale: Button
    private lateinit var blockMain1: LinearLayout
    private lateinit var blockMain2: LinearLayout
    private lateinit var blockSale: RelativeLayout
    private lateinit var blockNew: RelativeLayout
    private lateinit var rvSale: RecyclerView
    private lateinit var rvNew: RecyclerView

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

        bigBanner = view.findViewById(R.id.bigBanner)
        smallBanner = view.findViewById(R.id.smallBanner)
        btnCheckSale = view.findViewById(R.id.btnCheckSale)
        blockMain1 = view.findViewById(R.id.blockMain1)
        blockMain2 = view.findViewById(R.id.blockMain2)
        blockSale = view.findViewById(R.id.blockSale)
        blockNew = view.findViewById(R.id.blockNew)
        rvSale = view.findViewById<RecyclerView>(R.id.rvSale)
        rvNew = view.findViewById<RecyclerView>(R.id.rvNew)

        dbRef = FirebaseDatabase.getInstance().getReference("Products")
        dsProductSale = arrayListOf<Product>()
        dsProductNew = arrayListOf<Product>()

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsProductSale.clear()
                dsProductNew.clear()
                if (snapshot.exists()) {
                    for (productSnap in snapshot.children) {
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
                        if (productData.productMode == "NEW") {
                            dsProductNew.add(productData)
                        } else if (productData.productMode == "") {

                        } else {
                            dsProductSale.add(productData)
                        }
                    }
                    val mAdapterNew =
                        RvAdapterProduct(dsProductNew, R.layout.layout_item, object : RvInterface {
                            override fun OnItemClick(pos: Int) {
                                Toast.makeText(
                                    this@HomeFragment.requireContext(),
                                    "Vao chi tiet san pham ${dsProductNew[pos].productName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun OnItemLongClick(pos: Int) {
                                Toast.makeText(
                                    this@HomeFragment.requireContext(),
                                    "Chon color ma size roi add vao bag ${dsProductNew[pos].productName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    val mAdapterSale =
                        RvAdapterProduct(dsProductSale, R.layout.layout_item, object : RvInterface {
                            override fun OnItemClick(pos: Int) {
                                Toast.makeText(
                                    this@HomeFragment.requireContext(),
                                    "Vao chi tiet san pham ${dsProductSale[pos].productName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun OnItemLongClick(pos: Int) {
                                Toast.makeText(
                                    this@HomeFragment.requireContext(),
                                    "Chon color ma size roi add vao bag ${dsProductSale[pos].productName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    rvSale.adapter = mAdapterSale
                    rvSale.layoutManager = LinearLayoutManager(
                        this@HomeFragment.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    rvNew.adapter = mAdapterNew
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

        btnCheckSale.setOnClickListener {
            bigBanner.visibility = View.GONE
            smallBanner.visibility = View.VISIBLE
            blockSale.visibility = View.VISIBLE
        }

        smallBanner.setOnClickListener {
            blockMain2.visibility = View.VISIBLE
            blockMain1.visibility = View.GONE
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