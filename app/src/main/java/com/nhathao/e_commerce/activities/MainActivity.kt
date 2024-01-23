package com.nhathao.e_commerce.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.adapters.RvAdapterProduct
import com.nhathao.e_commerce.databinding.ActivityMainBinding
import com.nhathao.e_commerce.fragments.BagFragment
import com.nhathao.e_commerce.fragments.FavoriteFragment
import com.nhathao.e_commerce.fragments.HomeFragment
import com.nhathao.e_commerce.fragments.ProfileFragment
import com.nhathao.e_commerce.fragments.ShopFragment
import com.nhathao.e_commerce.models.Product
import com.nhathao.e_commerce.models.User

private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var dbRef : DatabaseReference
    private var isLogin = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        val bundle = intent.extras
        if (bundle != null) {
            val user = bundle.getSerializable("user") as User
            isLogin = bundle.getBoolean("isLogin", false)
        }

        dbRef = FirebaseDatabase.getInstance().getReference("Products")

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    binding.bottomNavigationView.menu.findItem(R.id.home).setChecked(true)
                    replaceFragment(HomeFragment())
                }
                R.id.shop -> {
                    binding.bottomNavigationView.menu.findItem(R.id.shop).setChecked(true)
                    replaceFragment(ShopFragment())
                }
                R.id.bag -> {
                    binding.bottomNavigationView.menu.findItem(R.id.bag).setChecked(true)
                    replaceFragment(BagFragment())
                }
                R.id.favorite -> {
                    binding.bottomNavigationView.menu.findItem(R.id.favorite).setChecked(true)
                    replaceFragment(FavoriteFragment())
                }
                R.id.profile -> {
                    if (!isLogin){
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        binding.bottomNavigationView.menu.findItem(R.id.profile).setChecked(true)
                        replaceFragment(ProfileFragment())
                    }
                }
            }
            false
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            addToBackStack(null)
            replace(R.id.frame_layout, fragment)
            commit()
        }
    }
}