package com.nhathao.e_commerce.models

class ShippingAddress (
    var shippingAddressId: String,
    var fullName: String,
    var address: String,
    var city: String,
    var region: String,
    var zipCode: String,
    var country: String,
    var isUsed: Boolean
) {
}