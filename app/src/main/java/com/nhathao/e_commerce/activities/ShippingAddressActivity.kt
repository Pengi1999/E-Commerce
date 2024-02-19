package com.nhathao.e_commerce.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.Interfaces.EventShippingAddressItemListening
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.adapters.RvAdapterReview
import com.nhathao.e_commerce.adapters.RvAdapterShippingAddress
import com.nhathao.e_commerce.databinding.ActivityShippingAddressBinding
import com.nhathao.e_commerce.models.Review
import com.nhathao.e_commerce.models.ShippingAddress
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityShippingAddressBinding
class ShippingAddressActivity : AppCompatActivity() {
    private lateinit var dbRefShippingAddress: DatabaseReference
    private lateinit var dsShippingAddressOfUser: MutableList<ShippingAddress>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRefShippingAddress = FirebaseDatabase.getInstance().getReference("ShippingAddresses")

        val bundleGet = intent.extras
        val user = bundleGet?.getSerializable("user") as User

        dsShippingAddressOfUser = mutableListOf()

        dbRefShippingAddress.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsShippingAddressOfUser.clear()
                if (snapshot.exists()) {
                    for (shippingAddressSnap in snapshot.children) {
                        if (shippingAddressSnap.child("userAccountName").value.toString() == user.userAccountName &&
                            shippingAddressSnap.child("status").value.toString().toBoolean()){
                            val shippingAddress = ShippingAddress(
                                shippingAddressSnap.child("shippingAddressId").value.toString(),
                                shippingAddressSnap.child("userAccountName").value.toString(),
                                shippingAddressSnap.child("fullName").value.toString(),
                                shippingAddressSnap.child("address").value.toString(),
                                shippingAddressSnap.child("city").value.toString(),
                                shippingAddressSnap.child("region").value.toString(),
                                shippingAddressSnap.child("zipCode").value.toString(),
                                shippingAddressSnap.child("country").value.toString(),
                                shippingAddressSnap.child("used").value.toString().toBoolean(),
                                shippingAddressSnap.child("status").value.toString().toBoolean()
                            )
                            dsShippingAddressOfUser.add(shippingAddress)
                        }
                    }
                    val shippingAddressAdapter = RvAdapterShippingAddress(dsShippingAddressOfUser, object : EventShippingAddressItemListening{
                        override fun OnClickEditItemListening(pos: Int) {
                            val intent = Intent(this@ShippingAddressActivity, EditingShippingAddressActivity::class.java)
                            val bundlePassing = Bundle()
                            bundlePassing.putSerializable("shippingAddress", dsShippingAddressOfUser[pos])
                            intent.putExtras(bundlePassing)
                            startActivity(intent)
                        }
                    })
                    binding.rcvShippingAddress.adapter = shippingAddressAdapter
                    binding.rcvShippingAddress.layoutManager = LinearLayoutManager(
                        this@ShippingAddressActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddingShippingAddressActivity::class.java)
            val bundlePassing = Bundle()
            bundlePassing.putSerializable("user", user)
            intent.putExtras(bundlePassing)
            startActivity(intent)
        }

    }
}