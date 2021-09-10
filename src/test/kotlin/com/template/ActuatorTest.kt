package com.template

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get
import java.net.URI

class ActuatorTest : ApiIntegrationTest() {

    @Test
    fun healthCheckApiIsOpen() {
        val test = mockMvc.get(URI.create("/actuator/health"))
        val result = test.andExpect {
            status { isOk() }
            jsonPath("status") { exists() }
        }.andReturn()
        val statusResult = JsonPath.read<String>(result.response.contentAsString, "$.status")
        assertEquals("UP", statusResult)
   }

    @Test
    fun infoApiIsOpen() {
        val test = mockMvc.get(URI.create("/actuator/info"))
        test.andExpect {
            status { isOk() }
            jsonPath("application.author") { exists() }
            jsonPath("application.version") { exists() }
            jsonPath("application.description") { exists() }
            jsonPath("application.more_info") { exists() }
        }
   }
}