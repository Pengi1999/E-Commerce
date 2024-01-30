package com.nhathao.e_commerce.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.Interfaces.RvInterface
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.activities.ProductDetailActivity
import com.nhathao.e_commerce.adapters.RvAdapterProduct
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.User

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
    private lateinit var dialog: BottomSheetDialog
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
    private lateinit var selectedSize: String
    private lateinit var selectedColor: String
    private var user: User? = null

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

        val bundleGetData = this.activity?.intent?.extras
        if (bundleGetData?.getSerializable("user") != null)
            user = bundleGetData.getSerializable("user") as User

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
                                val intent = Intent(this@HomeFragment.requireContext(), ProductDetailActivity::class.java)
                                val productSelected = dsProductNew[pos]
                                val bundle = Bundle()
                                bundle.putSerializable("productSelected", productSelected)
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }

                            override fun OnItemLongClick(pos: Int) {
                                showBottomSheetSelectColorAndSize()
                            }
                        })
                    val mAdapterSale =
                        RvAdapterProduct(dsProductSale, R.layout.layout_item, object : RvInterface {
                            override fun OnItemClick(pos: Int) {
                                val intent = Intent(this@HomeFragment.requireContext(), ProductDetailActivity::class.java)
                                val productSelected = dsProductSale[pos]
                                val bundle = Bundle()
                                bundle.putSerializable("productSelected", productSelected)
                                if (user != null)
                                    bundle.putSerializable("user", user)
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }

                            override fun OnItemLongClick(pos: Int) {
                                showBottomSheetSelectColorAndSize()
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

    private fun showBottomSheetSelectColorAndSize() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_select_colorandsize, null)
        dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(dialogView)

        val blockBlackColor = dialogView.findViewById<RelativeLayout>(R.id.blockBlackColor)
        val blockWhiteColor = dialogView.findViewById<RelativeLayout>(R.id.blockWhiteColor)
        val blockRedColor = dialogView.findViewById<RelativeLayout>(R.id.blockRedColor)
        val blockGrayColor = dialogView.findViewById<RelativeLayout>(R.id.blockGrayColor)
        val blockOrangeColor = dialogView.findViewById<RelativeLayout>(R.id.blockOrangeColor)
        val blockBlueColor = dialogView.findViewById<RelativeLayout>(R.id.blockBlueColor)

        val outlineBlackSelected = dialogView.findViewById<LinearLayout>(R.id.outlineBlackSelected)
        val outlineWhiteSelected = dialogView.findViewById<LinearLayout>(R.id.outlineWhiteSelected)
        val outlineRedSelected = dialogView.findViewById<LinearLayout>(R.id.outlineRedSelected)
        val outlineGraySelected = dialogView.findViewById<LinearLayout>(R.id.outlineGraySelected)
        val outlineOrangeSelected = dialogView.findViewById<LinearLayout>(R.id.outlineOrangeSelected)
        val outlineBlueSelected = dialogView.findViewById<LinearLayout>(R.id.outlineBlueSelected)

        val txtSizeXS = dialogView.findViewById<TextView>(R.id.txtSizeXS)
        val txtSizeS = dialogView.findViewById<TextView>(R.id.txtSizeS)
        val txtSizeM = dialogView.findViewById<TextView>(R.id.txtSizeM)
        val txtSizeL = dialogView.findViewById<TextView>(R.id.txtSizeL)
        val txtSizeXL = dialogView.findViewById<TextView>(R.id.txtSizeXL)

        val btnAddToCart = dialogView.findViewById<Button>(R.id.btnAddToCart)

        var isColorBlack: Boolean = false
        var isColorWhite: Boolean = false
        var isColorRed: Boolean = false
        var isColorGray: Boolean = false
        var isColorOrange: Boolean = false
        var isColorBlue: Boolean = false
        var isSizeXS: Boolean = false
        var isSizeS: Boolean = false
        var isSizeM: Boolean = false
        var isSizeL: Boolean = false
        var isSizeXL: Boolean = false

        blockBlackColor.setOnClickListener {
            if (!isColorBlack) {
                outlineBlackSelected.visibility = View.VISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = true
                isColorWhite = false
                isColorRed = false
                isColorGray = false
                isColorOrange = false
                isColorBlue = false

                selectedColor = "Black"
            } else {
                outlineBlackSelected.visibility = View.INVISIBLE
                isColorBlack = false
                selectedColor = ""
            }
        }

        blockWhiteColor.setOnClickListener {
            if (!isColorWhite) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.VISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = false
                isColorWhite = true
                isColorRed = false
                isColorGray = false
                isColorOrange = false
                isColorBlue = false

                selectedColor = "White"
            } else {
                outlineWhiteSelected.visibility = View.INVISIBLE
                isColorWhite = false
                selectedColor = ""
            }
        }

        blockRedColor.setOnClickListener {
            if (!isColorRed) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.VISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = false
                isColorWhite = false
                isColorRed = true
                isColorGray = false
                isColorOrange = false
                isColorBlue = false

                selectedColor = "Red"
            } else {
                outlineRedSelected.visibility = View.INVISIBLE
                isColorRed = false
                selectedColor = ""
            }
        }

        blockGrayColor.setOnClickListener {
            if (!isColorGray) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.VISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = false
                isColorWhite = false
                isColorRed = false
                isColorGray = true
                isColorOrange = false
                isColorBlue = false

                selectedColor = "Gray"
            } else {
                outlineGraySelected.visibility = View.INVISIBLE
                isColorGray = false
                selectedColor = ""
            }
        }

        blockOrangeColor.setOnClickListener {
            if (!isColorOrange) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.VISIBLE
                outlineBlueSelected.visibility = View.INVISIBLE

                isColorBlack = false
                isColorWhite = false
                isColorRed = false
                isColorGray = false
                isColorOrange = true
                isColorBlue = false

                selectedColor = "Orange"
            } else {
                outlineOrangeSelected.visibility = View.INVISIBLE
                isColorOrange = false
                selectedColor = ""
            }
        }

        blockBlueColor.setOnClickListener {
            if (!isColorBlue) {
                outlineBlackSelected.visibility = View.INVISIBLE
                outlineWhiteSelected.visibility = View.INVISIBLE
                outlineRedSelected.visibility = View.INVISIBLE
                outlineGraySelected.visibility = View.INVISIBLE
                outlineOrangeSelected.visibility = View.INVISIBLE
                outlineBlueSelected.visibility = View.VISIBLE

                isColorBlack = false
                isColorWhite = false
                isColorRed = false
                isColorGray = false
                isColorOrange = false
                isColorBlue = true

                selectedColor = "Blue"
            } else {
                outlineBlueSelected.visibility = View.INVISIBLE
                isColorBlue = false
                selectedColor = ""
            }
        }

        txtSizeXS.setOnClickListener {
            if (!isSizeXS) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size_selected)
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)

                txtSizeXS.setTextColor(resources.getColor(R.color.white))
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))

                isSizeXS = true
                isSizeS = false
                isSizeM = false
                isSizeL = false
                isSizeXL = false

                selectedSize = "XS"
            } else {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                isSizeXS = false
                selectedSize = ""
            }
        }

        txtSizeS.setOnClickListener {
            if (!isSizeS) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setBackgroundResource(R.drawable.bg_size_selected)
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)

                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeS.setTextColor(resources.getColor(R.color.white))
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))

                isSizeXS = false
                isSizeS = true
                isSizeM = false
                isSizeL = false
                isSizeXL = false

                selectedSize = "S"
            } else {
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                isSizeS = false
                selectedSize = ""
            }
        }

        txtSizeM.setOnClickListener {
            if (!isSizeM) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setBackgroundResource(R.drawable.bg_size_selected)
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)

                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeM.setTextColor(resources.getColor(R.color.white))
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))

                isSizeXS = false
                isSizeS = false
                isSizeM = true
                isSizeL = false
                isSizeXL = false

                selectedSize = "M"
            } else {
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                isSizeM = false
                selectedSize = ""
            }
        }

        txtSizeL.setOnClickListener {
            if (!isSizeL) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setBackgroundResource(R.drawable.bg_size_selected)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)

                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeL.setTextColor(resources.getColor(R.color.white))
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))

                isSizeXS = false
                isSizeS = false
                isSizeM = false
                isSizeL = true
                isSizeXL = false

                selectedSize = "L"
            } else {
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                isSizeL = false
                selectedSize = ""
            }
        }

        txtSizeXL.setOnClickListener {
            if (!isSizeXL) {
                txtSizeXS.setBackgroundResource(R.drawable.bg_size)
                txtSizeS.setBackgroundResource(R.drawable.bg_size)
                txtSizeM.setBackgroundResource(R.drawable.bg_size)
                txtSizeL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setBackgroundResource(R.drawable.bg_size_selected)

                txtSizeXS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeS.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeM.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeL.setTextColor(resources.getColor(R.color.black_custom))
                txtSizeXL.setTextColor(resources.getColor(R.color.white))

                isSizeXS = false
                isSizeS = false
                isSizeM = false
                isSizeL = false
                isSizeXL = true

                selectedSize = "XL"
            } else {
                txtSizeXL.setBackgroundResource(R.drawable.bg_size)
                txtSizeXL.setTextColor(resources.getColor(R.color.black_custom))
                isSizeXL = false
                selectedSize = ""
            }
        }

        btnAddToCart.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
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