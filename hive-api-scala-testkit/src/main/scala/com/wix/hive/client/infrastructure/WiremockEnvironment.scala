package com.wix.hive.client.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

trait WiremockEnvironment {
  val serverPort: Int

  lazy val wireMockServer = new WireMockServer(new WireMockConfiguration().port(serverPort))

  def start(): Unit = {
    WireMock.configureFor("localhost", serverPort)
    wireMockServer.start()
  }

  def stop(): Unit = {
    WireMock.shutdownServer()
  }

  def resetMocks(): Unit = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }
}
