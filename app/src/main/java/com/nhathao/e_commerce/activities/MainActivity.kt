package com.nhathao.e_commerce.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var dbRef: DatabaseReference
    private lateinit var user: User
    private var isLogin = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())

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
                    if (!isLogin) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startForResult.launch(intent)
                    } else {
                        binding.bottomNavigationView.menu.findItem(R.id.bag).setChecked(true)
                        replaceFragment(BagFragment())
                    }
                }

                R.id.favorite -> {
                    if (!isLogin) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startForResult.launch(intent)
                    } else {
                        binding.bottomNavigationView.menu.findItem(R.id.favorite).setChecked(true)
                        replaceFragment(FavoriteFragment())
                    }
                }

                R.id.profile -> {
                    if (!isLogin) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startForResult.launch(intent)
                    } else {
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

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val bundleGet = data?.extras
                if (bundleGet != null) {
                    isLogin = bundleGet.getBoolean("isLogin")
                    user = bundleGet.getSerializable("user") as User
                    val bundleIntentMain = Bundle()
                    bundleIntentMain.putBoolean("isLogin", isLogin)
                    bundleIntentMain.putSerializable("user", user)
                    intent.putExtras(bundleIntentMain)
                    binding.bottomNavigationView.menu.findItem(R.id.profile).setChecked(true)
                    replaceFragment(ProfileFragment())
                }
            }
        }

    override fun onResume() {
        super.onResume()
        val bundleGetData = intent.extras
        if (bundleGetData != null) {
            isLogin = bundleGetData.getBoolean("isLogin")
            if (isLogin)
                user = intent.extras!!.getSerializable("user") as User
        }
    }
}