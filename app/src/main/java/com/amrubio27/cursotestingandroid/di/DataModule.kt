package com.amrubio27.cursotestingandroid.di

import com.amrubio27.cursotestingandroid.core.data.coroutines.DefaultDispatchersProvider
import com.amrubio27.cursotestingandroid.core.domain.coroutines.DispatchersProvider
import com.amrubio27.cursotestingandroid.productlist.data.repository.ProductRepositoryImpl
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDispatchersProvider(
        defaultDispatchersProvider: DefaultDispatchersProvider
    ): DispatchersProvider {
        return DefaultDispatchersProvider()
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository {
        return productRepositoryImpl
    }
}