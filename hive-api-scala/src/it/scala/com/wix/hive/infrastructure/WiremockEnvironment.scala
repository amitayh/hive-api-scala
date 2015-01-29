package com.wix.hive.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

trait WiremockEnvironment {
  val serverPort: Int = 9089

  lazy val server: WireMockServer = {
    WireMock.configureFor("localhost", serverPort)
    val wireMockServer = new WireMockServer(new WireMockConfiguration().port(serverPort))
    wireMockServer.start()

    wireMockServer
  }

  def start(): Unit = server

  def stop(): Unit = {
    WireMock.shutdownServer()
  }

  def resetMocks(): Unit = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }
}

object WiremockEnvironment extends WiremockEnvironment