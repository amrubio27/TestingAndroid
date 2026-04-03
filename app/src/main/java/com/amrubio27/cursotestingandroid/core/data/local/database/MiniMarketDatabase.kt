package com.amrubio27.cursotestingandroid.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amrubio27.cursotestingandroid.cart.data.local.database.dao.CartItemDao
import com.amrubio27.cursotestingandroid.cart.data.local.database.entity.CartItemEntity
import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.ProductDao
import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.PromotionDao
import com.amrubio27.cursotestingandroid.productlist.data.local.database.entity.ProductEntity
import com.amrubio27.cursotestingandroid.productlist.data.local.database.entity.PromotionEntity

@Database(
    entities = [
        ProductEntity::class,
        PromotionEntity::class,
        CartItemEntity::class
    ],
    version = 1,
    exportSchema = true
)

abstract class MiniMarketDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun promotionDao(): PromotionDao
    abstract fun cartItemDao(): CartItemDao

}