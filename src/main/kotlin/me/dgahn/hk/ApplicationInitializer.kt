package me.dgahn.hk

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.FormAuthChallenge
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.form
import io.ktor.auth.principal
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.get
import io.ktor.sessions.sessions

fun main() {
    embeddedServer(Netty, 8080) { module() }.start(wait = true)
}

data class IndexData(val items: List<Int>)

data class MySession(val username: String)

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Authentication) {
        form("login") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                if (credentials.name == credentials.password) UserIdPrincipal(credentials.name) else null
            }
        }

    }

    install(Sessions) {
        cookie<MySession>("SESSION")
    }

    routing {
        get("/") {
            val session: MySession? = call.sessions.get()
            if (session != null) {
                call.respondText("User is Logged")
            } else {
                call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
            }
        }
        get("/html-freemarker") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
        }
        static("/static") {
            resources("static")
        }

        route("/login") {
            get {
                call.respond(FreeMarkerContent("login.ftl", null))
            }

            authenticate("login") {
                post {
                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                    call.sessions.set("SESSION", MySession(principal.name))
                    call.respondRedirect("/", permanent = false)
                }
            }
        }
    }
}
