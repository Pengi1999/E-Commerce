package com.nhathao.e_commerce.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.User
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    private lateinit var txtActionBar: TextView
    private lateinit var txtFavorites: TextView
    private lateinit var btnSearch: ImageButton
    private lateinit var btnStyleShowList: ImageButton
    private lateinit var blockFilters: LinearLayout
    private lateinit var blockSort: LinearLayout
    private lateinit var rcvFavorite: RecyclerView
    private lateinit var user: User
    private var isLogin: Boolean = false
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
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        txtActionBar = view.findViewById(R.id.txtActionBar)
        txtFavorites = view.findViewById(R.id.txtFavorites)
        btnSearch = view.findViewById(R.id.btnSearch)
        btnStyleShowList = view.findViewById(R.id.btnStyleShowList)
        blockFilters = view.findViewById(R.id.blockFilters)
        blockSort = view.findViewById(R.id.blockSort)
        rcvFavorite = view.findViewById(R.id.rcvFavorite)

        val bundleGetData = this.activity?.intent?.extras
        if (bundleGetData != null) {
            isLogin = bundleGetData.getBoolean("isLogin")
            if (isLogin)
                user = bundleGetData.getSerializable("user") as User
        }

        btnSearch.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}