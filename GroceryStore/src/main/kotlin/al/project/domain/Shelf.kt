package al.project.domain

class Shelf {
    val shelf = mutableListOf<Product>()

    override fun toString():String{
        return shelf
            .map { product -> product.name.toString() }
            .joinToString ("\n")
    }

    fun add(product : Product ){
        shelf.add(product)
    }

    fun fetch(index: Int): Product{
        return shelf[index]
    }

    fun fetchAll():Iterable<IndexedValue<Product>> = shelf.withIndex()
}