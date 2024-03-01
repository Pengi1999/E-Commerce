package com.nhathao.e_commerce.models

import java.io.Serializable

class Order (
    var orderId: String,
    var userAccountName: String,
    var shippingAddressId: String,
    var paymentMethod: String,
    var deliveryMethod: String,
    var promoCode: String,
    var totalPrice: Int,
    var createTime: Long,
    var status: String
) :Serializable {
}