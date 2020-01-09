package me.dgahn.hk

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication

class RoutingBDDTest : BehaviorSpec({
    Given("aaaa") {
        When("GET /snippets를 호출 했을 때") {
            var content = ""
            withTestApplication(Application::module) {
                with(handleRequest(HttpMethod.Get, "/snippets")) {
                    content = response.content ?: ""
                }
            }
            Then("OK를 반환한다.") {
                content shouldBe "{\"snippets\":[{\"user\":\"test\",\"text\":\"hello\"},{\"user\":\"test\",\"text\":\"world\"}]}"
            }
        }
    }

    Given("") {
        val snippets = "{\"snippet\": {\"text\" : \"mysnippet\"}}"
        When("POST /snippets를 호출 했을 때") {
            var content = ""
            withTestApplication(Application::module) {
                with(handleRequest(HttpMethod.Post, "/snippets") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(snippets)
                }) {
                    content = response.content.toString()
                }
            }
            Then("Token이 없으면 null 반환한다.") {
                content shouldBe "null"
            }
        }
    }


    Given("사용자이름 'test' 비밀번호가 'test'일 때") {
        val login = "{\"user\": \"test\", \"password\" : \"test\"}"
        When("POST /login-register 요청하면") {
            var content = ""
            withTestApplication(Application::module) {
                with(handleRequest(HttpMethod.Post, "/login-register") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(login)
                }) {
                    content = response.content.toString()
                }
            }
            Then("Token을 반환한다.") {
                content shouldBe "{\"token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdCJ9.96At6bwFhxebk4xk4tpkOFj-3ThxkLFNHkHaKoedOfA\"}"
            }
        }
    }

    Given("사용자이름 'test' 비밀번호가 '1234'일 때") {
        val login = "{\"user\": \"test\", \"password\" : \"1234\"}"
        When("POST /login-register 요청하면") {
            var content = ""
            withTestApplication(Application::module) {
                with(handleRequest(HttpMethod.Post, "/login-register") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(login)
                }) {
                    content = response.content.toString()
                }
            }
            Then("Invalid credentials를 반환한다.") {
                content shouldBe "{\"OK\":false,\"error\":\"Invalid credentials\"}"
            }
        }
    }
})