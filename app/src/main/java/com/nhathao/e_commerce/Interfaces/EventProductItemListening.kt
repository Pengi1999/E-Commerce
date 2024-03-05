package com.nhathao.e_commerce.Interfaces

interface EventProductItemListening {
    fun OnItemClick (pos: Int)
    fun OnItemLongClick (pos: Int)

    fun onClickBtnFavoriteItem (pos: Int)
}