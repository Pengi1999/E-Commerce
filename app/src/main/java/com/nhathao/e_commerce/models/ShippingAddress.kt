package com.nhathao.e_commerce.models

import java.io.Serializable

class ShippingAddress (
    var shippingAddressId: String,
    var userAccountName: String,
    var fullName: String,
    var address: String,
    var city: String,
    var region: String,
    var zipCode: String,
    var country: String,
    var used: Boolean,
    var status: Boolean
) : Serializable {
}