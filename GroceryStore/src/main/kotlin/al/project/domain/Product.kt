package al.project.domain

data class Product(
    val category: String,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val vendor: String,
    val country: String,
)
