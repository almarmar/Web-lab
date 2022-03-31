package al.project

import al.project.domain.Product
import al.project.domain.Shelf
import al.project.models.*
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.core.body.formAsMap
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.*
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.PebbleTemplates
import org.http4k.template.TemplateRenderer
import org.http4k.template.viewModel

fun app(
    shelf: Shelf,
    renderer: TemplateRenderer,
):HttpHandler {
    return routes(
            "/ping" bind GET to {
                Response(OK).body("pong")
            },

        "/templates/pebble" bind GET to {
            val viewModel = PebbleViewModel("Hello there!")
            Response(OK).body(renderer(viewModel))
        },

        "/" bind GET to{
            Response(FOUND).header("Location","/start")
        },
        "/start" bind GET to{
            val model = MyStartpage("")
            Response(OK).body(renderer(model))
        },
        "/shelf" bind GET to {
            val model  = ShelfViewModel(shelf.fetchAll())
            Response(OK).body(renderer(model))
        },

        "/shelf/new" bind GET to{
            val model = NewProductViewModel("")
            Response(OK).body(renderer(model))
        },
        "/shelf/new" bind Method.POST to{ request ->
            val params = request.form()
            val categoryString=params.findSingle("category").orEmpty()
            val nameString=params.findSingle("name").orEmpty()
            val descriptionString=params.findSingle("description").orEmpty()
            val priceString=params.findSingle("price").orEmpty()
            val quantityString=params.findSingle("quantity").orEmpty()
            val vendorString=params.findSingle("vendor").orEmpty()
            val countryString=params.findSingle("country").orEmpty()
            val fPrice = priceString.toDouble()
            val fquantity = quantityString.toInt()
            val product = Product(categoryString,nameString,descriptionString,fPrice,fquantity,vendorString,countryString)
            shelf.add(product)
            Response(FOUND).header("Location","/shelf")

        },
        "/shelf/{number}" bind GET to{ request ->
            val numberString = request.path("number").orEmpty()
            val number = numberString.toInt()
            val product = shelf.fetch(number)
            val model = ProductViewModel(product)
            Response(OK).body(renderer(model))
        },
        static(ResourceLoader.Classpath("/al/project/public/"))
    )
}



fun main() {
    val shelf = Shelf()
    shelf.add(Product(
        "Кулинарные ингредиенты",
        "Русский Сахар",
        "Сахар прессованный в коробке",
        109.99,
        50,
        "ОАО Ника",
        "Россия")
    )
    val renderer = PebbleTemplates().HotReload("src/main/resources")
    val printingApp: HttpHandler = PrintRequest().then(app(shelf,renderer))

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on http://localhost:" + server.port())
}
