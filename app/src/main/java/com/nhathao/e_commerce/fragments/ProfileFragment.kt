package com.nhathao.e_commerce.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.activities.LoginActivity
import com.nhathao.e_commerce.activities.SettingActivity
import com.nhathao.e_commerce.activities.ShippingAddressActivity
import com.nhathao.e_commerce.models.ShippingAddress
import com.nhathao.e_commerce.models.User
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var dbRefShippingAddress: DatabaseReference
    private lateinit var dbRefOrder: DatabaseReference
    private lateinit var btnSearch: ImageButton
    private lateinit var imgAvatar: ImageView
    private lateinit var txtFullName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtOrderQuantity: TextView
    private lateinit var txtShippingAddressQuantity: TextView
    private lateinit var cardMyOrder: CardView
    private lateinit var cardShippingAddress: CardView
    private lateinit var cardPaymentMethod: CardView
    private lateinit var cardPromocode: CardView
    private lateinit var cardReview: CardView
    private lateinit var cardSetting: CardView
    private lateinit var btnLogOut: Button
    private lateinit var user: User
    private var isLogin: Boolean = false
    private var requestCodeChangeAccount = 4

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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        dbRefUser = FirebaseDatabase.getInstance().getReference("Users")
        dbRefShippingAddress = FirebaseDatabase.getInstance().getReference("ShippingAddresses")
        dbRefOrder = FirebaseDatabase.getInstance().getReference("Orders")

        btnSearch = view.findViewById(R.id.btnSearch)
        imgAvatar = view.findViewById(R.id.imgAvatar)
        txtFullName = view.findViewById(R.id.txtFullName)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtOrderQuantity = view.findViewById(R.id.txtOrderQuantity)
        txtShippingAddressQuantity = view.findViewById(R.id.txtShippingAddressQuantity)
        cardMyOrder = view.findViewById(R.id.cardMyOrder)
        cardShippingAddress = view.findViewById(R.id.cardShippingAddress)
        cardPaymentMethod = view.findViewById(R.id.cardPaymentMethod)
        cardPromocode = view.findViewById(R.id.cardPromocode)
        cardReview = view.findViewById(R.id.cardReview)
        cardSetting = view.findViewById(R.id.cardSetting)
        btnLogOut = view.findViewById(R.id.btnLogOut)

        val bundleGetData = this.activity?.intent?.extras
        if (bundleGetData != null) {
            isLogin = bundleGetData.getBoolean("isLogin")
            if (isLogin)
                user = bundleGetData.getSerializable("user") as User
        }

        dbRefShippingAddress.addValueEventListener(object : ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var shippingAddressQuantity = 0
                    for (shippingAddressSnap in snapshot.children) {
                        if (shippingAddressSnap.child("userAccountName").value.toString() == user.userAccountName &&
                            shippingAddressSnap.child("status").value.toString().toBoolean()
                        ) {
                            shippingAddressQuantity++
                        }
                    }
                    if (shippingAddressQuantity < 1)
                        txtShippingAddressQuantity.text =
                            "You don't have a shipping address yet"
                    else if (shippingAddressQuantity == 1)
                        txtShippingAddressQuantity.text = "$shippingAddressQuantity address"
                    else
                        txtShippingAddressQuantity.text = "$shippingAddressQuantity addresses"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        dbRefOrder.addValueEventListener(object : ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var orderQuantity = 0
                    for (orderSnap in snapshot.children) {
                        if (orderSnap.child("userAccountName").value.toString() == user.userAccountName) {
                            orderQuantity++
                        }
                    }
                    if (orderQuantity < 1)
                        txtOrderQuantity.text =
                            "You don't have a order yet"
                    else if (orderQuantity == 1)
                        txtOrderQuantity.text = "Already have $orderQuantity order"
                    else
                        txtOrderQuantity.text = "Already have $orderQuantity orders"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        btnSearch.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
        }

        imgAvatar.setOnClickListener {
            val myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
            myFileIntent.setType("image/*")
            startForResult.launch(myFileIntent)
        }

        cardMyOrder.setOnClickListener {
            this.activity?.supportFragmentManager?.beginTransaction()?.apply {
                addToBackStack(null)
                replace(R.id.frame_layout, MyOrdersFragment())
                commit()
            }
        }

        cardShippingAddress.setOnClickListener {
            val intent = Intent(this.requireContext(), ShippingAddressActivity::class.java)
            val bundlePassing = Bundle()
            bundlePassing.putSerializable("user", user)
            intent.putExtras(bundlePassing)
            startActivity(intent)
        }

        cardPaymentMethod.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
        }

        cardPromocode.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
        }

        cardReview.setOnClickListener {
            Toast.makeText(this.requireContext(), "Do it later", Toast.LENGTH_SHORT).show()
        }

        cardSetting.setOnClickListener {
            val intent = Intent(this.activity, SettingActivity::class.java)
            val bundlePassing = Bundle()
            bundlePassing.putSerializable("user", user)
            intent.putExtras(bundlePassing)
            startActivity(intent)
        }

        btnLogOut.setOnClickListener {
            val intent = Intent(this.activity, LoginActivity::class.java)
            startForResult.launch(intent)
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
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if (user.avatar != "") {
            val bytes = Base64.decode(user.avatar, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imgAvatar.setImageBitmap(bitmap)
        }
        dbRefUser.child(user.userAccountName).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user.userAccountName = snapshot.child("userAccountName").value.toString()
                    user.userPWD = snapshot.child("userPWD").value.toString()
                    user.userName = snapshot.child("userName").value.toString()
                    user.secretCode = snapshot.child("secretCode").value.toString()
                    user.birthday = snapshot.child("birthday").value.toString()
                    user.avatar = snapshot.child("avatar").value.toString()
                    user.email = snapshot.child("email").value.toString()
                    txtFullName.text = user.userName
                    txtEmail.text = user.email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bundleGet = result.data?.extras
                if (bundleGet != null) {
                    val requestCode = bundleGet.getInt("requestCode")
                    if (requestCode == requestCodeChangeAccount) {
                        isLogin = bundleGet.getBoolean("isLogin")
                        this.activity?.intent?.putExtra("isLogin", isLogin)
                        user = bundleGet.getSerializable("user") as User
                        this.activity?.intent?.putExtra("user", user)
                        this.activity?.supportFragmentManager?.beginTransaction()?.apply {
                            replace(R.id.frame_layout, HomeFragment())
                            commit()
                        }
                        val bottomNavigationViewMain = this.activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                        bottomNavigationViewMain?.menu?.findItem(R.id.home)?.setChecked(true)
                    }
                }
                else{
                    val uri = result.data?.data
                    try {
                        val inputStream = this.context?.contentResolver?.openInputStream(uri!!)
                        val myBitmap = BitmapFactory.decodeStream(inputStream)
                        val steam = ByteArrayOutputStream()
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, steam)
                        val bytes = steam.toByteArray()
                        user.avatar = Base64.encodeToString(bytes, Base64.DEFAULT)
                        dbRefUser.child(user.userAccountName).child("avatar").setValue(user.avatar)
                        imgAvatar.setImageBitmap(myBitmap)
                        inputStream?.close()
                    } catch (ex: Exception) {
                        Toast.makeText(this.context, ex.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                val intent = result.data
                val bundleGet = intent?.extras
                if (bundleGet != null) {
                    isLogin = bundleGet.getBoolean("isLogin")
                    this.activity?.intent?.putExtra("isLogin", isLogin)
                    this.activity?.supportFragmentManager?.beginTransaction()?.apply {
                        replace(R.id.frame_layout, HomeFragment())
                        commit()
                    }
                    val bottomNavigationViewMain = this.activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNavigationViewMain?.menu?.findItem(R.id.home)?.setChecked(true)
                }
            }
        }
}