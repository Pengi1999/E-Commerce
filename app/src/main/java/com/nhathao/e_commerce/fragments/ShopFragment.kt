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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhathao.e_commerce.R
import com.nhathao.e_commerce.models.Product
import java.io.ByteArrayOutputStream

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
    private lateinit var dbRef: DatabaseReference
    private lateinit var imageProduct : ImageView
    private lateinit var product: Product
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

        imageProduct = view.findViewById<ImageView>(R.id.imageView)
        val edtName = view.findViewById<EditText>(R.id.editTextText)
        val edtDescribe = view.findViewById<EditText>(R.id.editTextText8)
        val edtSex = view.findViewById<EditText>(R.id.editTextText2)
        val edtType = view.findViewById<EditText>(R.id.editTextText3)
        val edtCategory = view.findViewById<EditText>(R.id.editTextText4)
        val edtBrand = view.findViewById<EditText>(R.id.editTextText7)
        val edtMode = view.findViewById<EditText>(R.id.editTextText6)
        val edtPrice = view.findViewById<EditText>(R.id.editTextText5)
        val buttonInsert = view.findViewById<Button>(R.id.button)
        val buttonKhoiTao = view.findViewById<Button>(R.id.button2)

        dbRef = FirebaseDatabase.getInstance().getReference("Products")

        imageProduct.setOnClickListener {
            val myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
            myFileIntent.setType("image/*")
            startForResult.launch(myFileIntent)
        }

        buttonInsert.setOnClickListener {
            product.productId = dbRef.push().key.toString()
            dbRef.child(product.productId).setValue(product)
        }

        buttonKhoiTao.setOnClickListener {
            product = Product(
                "",
                edtName.text.toString(),
                edtDescribe.text.toString(),
                "",
                edtSex.text.toString(),
                edtType.text.toString(),
                edtCategory.text.toString(),
                edtBrand.text.toString(),
                edtMode.text.toString(),
                edtPrice.text.toString().toInt())
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

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            try {
                val inputStream = this.context?.contentResolver?.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val steam = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, steam)
                val bytes = steam.toByteArray()
                product.productImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                imageProduct.setImageBitmap(myBitmap)
                inputStream?.close()
            }
            catch (ex: Exception) {
                Toast.makeText(this.context, ex.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}