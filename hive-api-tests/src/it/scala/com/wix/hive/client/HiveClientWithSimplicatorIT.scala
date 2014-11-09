package com.wix.hive.client

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.log4j.BasicConfigurator


class HiveClientWithSimplicatorIT extends BaseHiveClientIT with HubSimplicator {
  val serverPort = 8089

  override val baseUrl = s"http://localhost:$serverPort"
  val wireMockServer = new WireMockServer(new WireMockConfiguration().port(serverPort))

  override def initEnv(): Unit = {
    BasicConfigurator.configure()
    WireMock.configureFor("localhost", serverPort)
    wireMockServer.start()
  }

  override def shutdownEnv() = wireMockServer.shutdown()

  override def beforeTest(): Unit = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }
}