package com.amrubio27.cursotestingandroid.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.amrubio27.cursotestingandroid.cart.data.local.database.dao.CartItemDao
import com.amrubio27.cursotestingandroid.cart.data.repository.CartItemRepositoryImpl
import com.amrubio27.cursotestingandroid.cart.domain.repository.CartItemRepository
import com.amrubio27.cursotestingandroid.checkout.data.repository.OrderRepositoryImpl
import com.amrubio27.cursotestingandroid.checkout.domain.repository.OrderRepository
import com.amrubio27.cursotestingandroid.core.data.coroutines.DefaultDispatchersProvider
import com.amrubio27.cursotestingandroid.core.data.local.database.MiniMarketDatabase
import com.amrubio27.cursotestingandroid.core.data.util.SystemClock
import com.amrubio27.cursotestingandroid.core.domain.coroutines.DispatchersProvider
import com.amrubio27.cursotestingandroid.core.domain.util.Clock
import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.ProductDao
import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.PromotionDao
import com.amrubio27.cursotestingandroid.productlist.data.repository.ProductRepositoryImpl
import com.amrubio27.cursotestingandroid.productlist.data.repository.PromotionRepositoryImpl
import com.amrubio27.cursotestingandroid.productlist.data.repository.SettingsRepositoryImpl
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import com.amrubio27.cursotestingandroid.productlist.domain.repository.PromotionRepository
import com.amrubio27.cursotestingandroid.productlist.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDispatchersProvider(): DispatchersProvider = DefaultDispatchersProvider()

    @Provides
    @Singleton
    fun provideProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository =
        productRepositoryImpl

    @Provides
    @Singleton
    fun providePromotionRepository(promotionRepositoryImpl: PromotionRepositoryImpl): PromotionRepository =
        promotionRepositoryImpl

    @Provides
    fun provideProductDao(database: MiniMarketDatabase): ProductDao = database.productDao()

    @Provides
    fun providePromotionDao(database: MiniMarketDatabase): PromotionDao = database.promotionDao()

    @Provides
    fun providesCartItemDao(database: MiniMarketDatabase): CartItemDao = database.cartItemDao()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): MiniMarketDatabase =
        Room
            .databaseBuilder(
                context,
                MiniMarketDatabase::class.java,
                "market_db",
            ).build()

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository =
        settingsRepositoryImpl

    @Provides
    @Singleton
    fun provideCartRepository(cartItemRepositoryImpl: CartItemRepositoryImpl): CartItemRepository =
        cartItemRepositoryImpl

    @Provides
    @Singleton
    fun provideOrderRepository(orderRepositoryImpl: OrderRepositoryImpl): OrderRepository {
        return orderRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideClock(systemClock: SystemClock): Clock = systemClock
}
