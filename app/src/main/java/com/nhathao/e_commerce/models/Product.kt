package com.nhathao.e_commerce.models

class Product (
    var productId: String,
    var productName: String,
    var productDescribe: String,
    var productImage: String,
    var productSex: String,
    var productType: String,
    var productCategory: String,
    var productBrand: String,
    var productMode: String,
    var productPrice: Int,
    var productRating: Float? = 0f,
    var productRatingQuantity: Int? = 0
) {
}