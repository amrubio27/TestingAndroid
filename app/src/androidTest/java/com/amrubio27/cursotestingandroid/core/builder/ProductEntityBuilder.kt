package com.amrubio27.cursotestingandroid.core.builder

import com.amrubio27.cursotestingandroid.productlist.data.local.database.entity.ProductEntity

class ProductEntityBuilder {
    private var id: String = "product-1"
    private var name: String = "Producto de pruebas"
    private var description: String = "Descripción del producto de pruebas"
    private var price: Double = 10.0
    private var stock: Int = 10
    private var imageUrl: String? = null
    private var category: String = "Categoría de pruebas"

    fun withId(id: String) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
    fun withDescription(description: String) = apply { this.description = description }
    fun withPrice(price: Double) = apply { this.price = price }
    fun withStock(stock: Int) = apply { this.stock = stock }
    fun withImageUrl(imageUrl: String?) = apply { this.imageUrl = imageUrl }
    fun withCategory(category: String) = apply { this.category = category }

    fun build() = ProductEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        stock = stock,
        imageUrl = imageUrl,
        category = category
    )
}

fun productEntity(block: ProductEntityBuilder.() -> Unit = {}) =
    ProductEntityBuilder().apply(block).build()