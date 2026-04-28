package com.amrubio27.cursotestingandroid.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.core.internal.deps.dagger.Module
import com.amrubio27.cursotestingandroid.cart.data.local.database.dao.CartItemDao
import com.amrubio27.cursotestingandroid.cart.data.repository.CartItemRepositoryImpl
import com.amrubio27.cursotestingandroid.cart.domain.repository.CartItemRepository
import com.amrubio27.cursotestingandroid.core.data.coroutines.DefaultDispatchersProvider
import com.amrubio27.cursotestingandroid.core.data.local.database.MiniMarketDatabase
import com.amrubio27.cursotestingandroid.core.data.util.SystemClock
import com.amrubio27.cursotestingandroid.core.domain.coroutines.DispatchersProvider
import com.amrubio27.cursotestingandroid.core.domain.util.Clock
import com.amrubio27.cursotestingandroid.di.DataModule
import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.ProductDao
import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.PromotionDao
import com.amrubio27.cursotestingandroid.productlist.data.repository.ProductRepositoryImpl
import com.amrubio27.cursotestingandroid.productlist.data.repository.PromotionRepositoryImpl
import com.amrubio27.cursotestingandroid.productlist.data.repository.SettingsRepositoryImpl
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import com.amrubio27.cursotestingandroid.productlist.domain.repository.PromotionRepository
import com.amrubio27.cursotestingandroid.productlist.domain.repository.SettingsRepository
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

private val Context.testingDataStore: DataStore<Preferences> by preferencesDataStore("testing_settings")

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
object TestDataModule {
    @Provides
    @Singleton
    fun provideDispatchersProvider(): DispatchersProvider {
        return DefaultDispatchersProvider()
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository {
        return productRepositoryImpl
    }

    @Provides
    @Singleton
    fun providePromotionRepository(
        promotionRepositoryImpl: PromotionRepositoryImpl
    ): PromotionRepository {
        return promotionRepositoryImpl
    }


    @Provides
    fun provideProductDao(database: MiniMarketDatabase): ProductDao = database.productDao()

    @Provides
    fun providePromotionDao(database: MiniMarketDatabase): PromotionDao = database.promotionDao()

    @Provides
    fun providesCartItemDao(database: MiniMarketDatabase): CartItemDao = database.cartItemDao()

    @Provides
    @Singleton
    fun provideDatabase(): MiniMarketDatabase {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return Room.inMemoryDatabaseBuilder(
            context,
            MiniMarketDatabase::class.java
        ).build()
    }

    @Provides
    @Singleton
    fun provideDataStore(): DataStore<Preferences> {
        return ApplicationProvider.getApplicationContext<Context>().testingDataStore
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository {
        return settingsRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        cartItemRepositoryImpl: CartItemRepositoryImpl
    ): CartItemRepository {
        return cartItemRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideClock(systemClock: SystemClock): Clock {
        return systemClock
    }
}