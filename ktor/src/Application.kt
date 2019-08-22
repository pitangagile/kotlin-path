package com.kto

import com.ktor.model.Person
import com.ktor.util.Errors
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
        }
    }

    val persons: ArrayList<Person> = ArrayList()

    routing {

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"GRUPO DE ESTUDO DE KOTLIN" }
                    h2 { + "Lista de presença:"}
                    ul {
                        li { +"Matheus" }
                        li { +"Glauber" }
                        li { +"Mei" }
                        li { +"André" }
                        li { +"João" }
                        li { +"Bruno" }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }

        route("/person") {

            get{
                call.respond(persons)
            }

            get("/{name}") {
                val name = call.parameters["name"]
                val response = persons.firstOrNull{person ->
                    person.name == name
                }
                call.respond( response ?: Errors.valueNotFound)
            }

            post{
                val post: Person? = call.receive() as Person
                post?.let { person ->
                    persons.add(person)
                    call.respond(mapOf("OK" to true))
                    return@post
                }
                call.respond(Errors.insertionFail)
            }

        }

    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
