package com.nhathao.e_commerce.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var btnBack: ImageButton
    private lateinit var btnViewAll: Button
    private lateinit var listViewCategory: ListView
    private lateinit var listCategory:MutableList<String>
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
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        dbRef = FirebaseDatabase.getInstance().getReference("Products")

        btnBack = view.findViewById(R.id.btnBack)
        btnViewAll = view.findViewById(R.id.btnViewAll)
        listViewCategory = view.findViewById(R.id.listViewCategory)

        val sex = this.activity?.intent?.getStringExtra("Sex")
        val type = this.activity?.intent?.getStringExtra("Type")

        listCategory = mutableListOf()
        if(type == "Clothes"){
            listCategory.add("Tops")
        }

        dbRef.get().addOnSuccessListener {
            if(it.exists()) {
                for (productSnap in it.children){
                    val productSnapSex = productSnap.child("productSex").value.toString()
                    val productSnapType = productSnap.child("productType").value.toString()
                    val productSnapCategory = productSnap.child("productCategory").value.toString()
                    if (productSnapSex == sex && productSnapType == type) {
                        if (!listCategory.contains(productSnapCategory)) {
                            listCategory.add(productSnapCategory)
                        }
                    }
                }
            }
        }.addOnFailureListener{
            Log.wtf("firebase", "Error getting data", it)
        }

//        if(type == "Clothes" && sex == "Women")
//            listCategory = listOf<String>("Tops", "Shirts & Blouses", "Cardigans & Sweaters", "Knitwear", "Blazers", "Outerwear", "Pants", "Jeans", "Shorts", "Skirts", "Dresses")
//        else
//            listCategory = listOf()

        listViewCategory.adapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_list_item_1,
            listCategory)

        btnBack.setOnClickListener {
            this.activity?.supportFragmentManager?.popBackStack()
        }

        btnViewAll.setOnClickListener {
            replaceFragment("All")
        }

        listViewCategory.setOnItemClickListener { parent, view, position, id ->
            replaceFragment(listCategory[position])
        }
        return view
    }

    private fun replaceFragment(category: String) {
        this.activity?.intent?.putExtra("Category", category)
        this.activity?.supportFragmentManager?.beginTransaction()?.apply {
            addToBackStack(null)
            replace(R.id.frame_layout, CatalogFragment())
            commit()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}