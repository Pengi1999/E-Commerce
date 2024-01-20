package com.nhathao.e_commerce.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
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
    private lateinit var btnBack: ImageButton
    private lateinit var listViewCategory: ListView
    private lateinit var listCategory:List<String>
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

        btnBack = view.findViewById(R.id.btnBack)
        listViewCategory = view.findViewById(R.id.listViewCategory)

        val sex = this.activity?.intent?.getStringExtra("Sex")
        val type = this.activity?.intent?.getStringExtra("Type")

        if(type == "Clothes" && sex == "Women")
            listCategory = listOf<String>("Tops", "Shirts & Blouses", "Cardigans & Sweaters", "Knitwear", "Blazers", "Outerwear", "Pants", "Jeans", "Shorts", "Skirts", "Dresses")
        else
            listCategory = listOf()

        listViewCategory.adapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_list_item_1,
            listCategory)

        btnBack.setOnClickListener {
            this.activity?.supportFragmentManager?.popBackStack()
        }

        listViewCategory.setOnItemClickListener { parent, view, position, id ->
            this.activity?.intent?.putExtra("Sex", sex)
            this.activity?.intent?.putExtra("Category", listCategory[position])
            this.activity?.supportFragmentManager?.beginTransaction()?.apply {
                addToBackStack(null)
                replace(R.id.frame_layout, CatalogFragment())
                commit()
            }
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