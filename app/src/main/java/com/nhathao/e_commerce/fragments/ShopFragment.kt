package com.nhathao.e_commerce.fragments

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.nhathao.e_commerce.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShopFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShopFragment : Fragment() {
    private lateinit var btnBack:ImageButton
    private lateinit var btnSearch:ImageButton
    private lateinit var txtWomen:TextView
    private lateinit var txtMen:TextView
    private lateinit var txtKids:TextView
    private lateinit var underWomen:LinearLayout
    private lateinit var underMen:LinearLayout
    private lateinit var underKids:LinearLayout
    private lateinit var cardNew:CardView
    private lateinit var cardClothes:CardView
    private lateinit var cardShoes:CardView
    private lateinit var cardAccessories:CardView
    private var sex:String = "Women"

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
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        btnBack = view.findViewById(R.id.btnBack)
        btnSearch = view.findViewById(R.id.btnSearch)
        txtWomen = view.findViewById(R.id.txtWomen)
        txtMen = view.findViewById(R.id.txtMen)
        txtKids = view.findViewById(R.id.txtKids)
        underWomen = view.findViewById(R.id.underWomen)
        underMen = view.findViewById(R.id.underMen)
        underKids = view.findViewById(R.id.underKids)
        cardNew = view.findViewById(R.id.cardNew)
        cardClothes = view.findViewById(R.id.cardClothes)
        cardShoes = view.findViewById(R.id.cardShoes)
        cardAccessories = view.findViewById(R.id.cardAccessories)

        btnBack.setOnClickListener {
            this.activity?.supportFragmentManager?.popBackStack()
        }

        txtWomen.setOnClickListener {
            txtWomen.typeface = Typeface.DEFAULT_BOLD
            txtMen.typeface = Typeface.DEFAULT
            txtKids.typeface = Typeface.DEFAULT
            underWomen.visibility = View.VISIBLE
            underMen.visibility = View.INVISIBLE
            underKids.visibility = View.INVISIBLE

            sex = txtWomen.text.toString()
        }

        txtMen.setOnClickListener {
            txtWomen.typeface = Typeface.DEFAULT
            txtMen.typeface = Typeface.DEFAULT_BOLD
            txtKids.typeface = Typeface.DEFAULT
            underWomen.visibility = View.INVISIBLE
            underMen.visibility = View.VISIBLE
            underKids.visibility = View.INVISIBLE

            sex = txtMen.text.toString()
        }

        txtKids.setOnClickListener {
            txtWomen.typeface = Typeface.DEFAULT
            txtMen.typeface = Typeface.DEFAULT
            txtKids.typeface = Typeface.DEFAULT_BOLD
            underWomen.visibility = View.INVISIBLE
            underMen.visibility = View.INVISIBLE
            underKids.visibility = View.VISIBLE

            sex = txtKids.text.toString()
        }

        cardNew.setOnClickListener {
            replaceFragment("New")
        }

        cardClothes.setOnClickListener {
            replaceFragment("Clothes")
        }

        cardShoes.setOnClickListener {
            replaceFragment("Shoes")
        }

        cardAccessories.setOnClickListener {
            replaceFragment("Accessories")
        }
        return view
    }

    private fun replaceFragment(type: String) {
        this.activity?.intent?.putExtra("Sex", sex)
        this.activity?.intent?.putExtra("Type", type)
        this.activity?.supportFragmentManager?.beginTransaction()?.apply {
            addToBackStack(null)
            replace(R.id.frame_layout, CategoryFragment())
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
         * @return A new instance of fragment ShopFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShopFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}