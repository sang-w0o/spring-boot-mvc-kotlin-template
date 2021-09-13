package com.template

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get
import java.net.URI

class ActuatorTest : ApiIntegrationTest() {

    @DisplayName("Actuator - Health Check")
    @Test
    fun healthCheckApiIsOpen() {
        val test = mockMvc.get(URI.create("/actuator/health"))
        test.andExpect {
            status { isOk() }
            jsonPath("status") { value("UP") }
        }
    }

    @DisplayName("Actuator - Information")
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
