package com.nhathao.e_commerce.models

class Review(
    var reviewId: String,
    var userAccountName: String,
    var productId:String,
    var reviewContent:String,
    var reviewDate: Long
) {
}