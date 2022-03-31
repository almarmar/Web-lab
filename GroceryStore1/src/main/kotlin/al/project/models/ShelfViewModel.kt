package al.project.models

import al.project.domain.Product
import org.http4k.template.ViewModel

data class ShelfViewModel(val productList:Iterable<IndexedValue<Product>>):ViewModel
