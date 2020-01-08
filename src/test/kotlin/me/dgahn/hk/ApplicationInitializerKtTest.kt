package me.dgahn.hk

import io.kotlintest.specs.FunSpec
import io.ktor.application.Application
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.contentType
import io.ktor.server.testing.cookiesSession
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.json.simple.JSONObject
import kotlin.test.assertEquals

class ApplicationInitializerKtTest : FunSpec() {
    init {
        test("로그인에 성공한 경우 상태코드 301(Found)를 받는다.") {
            withTestApplication(Application::module) {
                with(handleRequest(HttpMethod.Post, "/login") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(listOf("username" to "a", "password" to "a").formUrlEncode())
                }) {
                    assertEquals(HttpStatusCode.Found, response.status())
                }
            }
        }

        test("현재 로그인 상태인 경우 'User is Logged' 텍스트를 반환한다.") {
            withTestApplication(Application::module) {
                cookiesSession {
                    with(handleRequest(HttpMethod.Post, "/login") {
                        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                        setBody(listOf("username" to "a", "password" to "a").formUrlEncode())
                    }) {
                        assertEquals(HttpStatusCode.Found, response.status())
                    }

                    with(handleRequest(HttpMethod.Get, "/")) {
                        assertEquals("User is Logged", response.content)
                    }
                }
            }
        }
    }
}