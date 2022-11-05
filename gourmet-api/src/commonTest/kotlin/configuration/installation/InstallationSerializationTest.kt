package configuration.installation

import configuration.Parameters
import plugins.ActionId
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class InstallationSerializationTest {
    @Test
    fun `deserialize an installation configuration`() {
        val json = """
                {
                  "installation": {
                    "jobs": {
                      "prepareRpi": {
                        "name": "Prepare Raspberry PI for usage",
                        "steps": [
                          {
                            "name": "Set basic environment",
                            "uses": "updateEnvironment@v1",
                            "parameters": {
                              "newHostname": "victim.cult-of-funny-hats"
                            }
                          },
                          {
                            "name": "Set initial SSH context",
                            "uses": "updateEnvironment@v1",
                            "parameters": {
                              "credentials": {
                                "username": "ubuntu",
                                "password": "ubuntu"
                              },
                              "hostnames": [
                                "192.168.1.123",
                                "192.168.1.124",
                                "192.168.1.125"
                              ]
                            }
                          }
                        ]
                      },
                      "provisionServer": {
                        "name": "Provision for server usage",
                        "steps": [
                          {
                            "name": "Set some retry limit",
                            "uses": "updateRetryLimit@v1",
                            "parameters": {
                              "environment": "first",
                              "retries": 3
                            }
                          }
                        ]
                      }
                    }
                  }
                }
        """.trimIndent()

        val installation = Json.decodeFromString<Installation>(json)

        val expectedInstallation = Installation(
            mapOf(
                JobId("prepareRpi") to Job(
                    "Prepare Raspberry PI for usage",
                    listOf(
                        Step(
                            "Set basic environment",
                            ActionId("updateEnvironment@v1"),
                            Parameters.Map(
                                mapOf(
                                    "newHostname" to Parameters.String("victim.cult-of-funny-hats"),
                                ),
                            ),
                        ),
                        Step(
                            "Set initial SSH context",
                            ActionId("updateEnvironment@v1"),
                            Parameters.Map(
                                mapOf(
                                    "credentials" to Parameters.Map(
                                        mapOf(
                                            "username" to Parameters.String("ubuntu"),
                                            "password" to Parameters.String("ubuntu"),
                                        ),
                                    ),
                                    "hostnames" to Parameters.List(
                                        listOf(
                                            Parameters.String("192.168.1.123"),
                                            Parameters.String("192.168.1.124"),
                                            Parameters.String("192.168.1.125"),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                JobId("provisionServer") to Job(
                    "Provision for server usage",
                    listOf(
                        Step(
                            "Set some retry limit",
                            ActionId("updateRetryLimit@v1"),
                            Parameters.Map(
                                mapOf(
                                    "environment" to Parameters.String("first"),
                                    "retries" to Parameters.Integer(3)
                                ),
                            ),
                        ),
                    ),
                ),
            )
        )

        installation.shouldBe(expectedInstallation)
    }

    @Test
    fun `installation to json`() {
        val formatter = Json {
            prettyPrint = true
        }

        val installation = Installation(
            mapOf(
                JobId("testing") to Job(
                    "Test this",
                    listOf(
                        Step(
                            "Do something",
                            ActionId("voucher@v1"),
                            Parameters.Map(
                                mapOf(
                                    "foo" to Parameters.String("Dddd"),
                                    "bar" to Parameters.Integer(4),
                                    "baz" to Parameters.List(
                                        listOf(
                                            Parameters.Integer(19),
                                            Parameters.Integer(1),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val json = formatter.encodeToString(installation)
        val expectedJson = """
            {
                "installation": {
                    "jobs": {
                        "testing": {
                            "name": "Test this",
                            "steps": [
                                {
                                    "name": "Do something",
                                    "uses": "voucher@v1",
                                    "parameters": {
                                        "foo": "Dddd",
                                        "bar": 4,
                                        "baz": [
                                            19,
                                            1
                                        ]
                                    }
                                }
                            ]
                        }
                    }
                }
            }
        """.trimIndent()

        json.shouldBe(expectedJson)
    }
}