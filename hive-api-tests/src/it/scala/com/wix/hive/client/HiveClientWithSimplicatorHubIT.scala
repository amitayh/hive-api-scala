package com.wix.hive.client

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.log4j.BasicConfigurator


class HiveClientWithSimplicatorHubIT extends BaseHiveClientIT with SimplicatorHub {
  override val baseUrl = s"http://localhost:$serverPort"

  override def initEnv(): Unit = initEnvironment

  override def shutdownEnv() = wireMockServer.shutdown()

  override def beforeTest(): Unit = resetMocks
}