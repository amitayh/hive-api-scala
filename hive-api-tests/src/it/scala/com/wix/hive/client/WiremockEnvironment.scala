package com.wix.hive.client

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.log4j.BasicConfigurator

trait WiremockEnvironment {
  val serverPort = 8089

  lazy val wireMockServer = new WireMockServer(new WireMockConfiguration().port(serverPort))

  lazy val initEnvironment = {
    BasicConfigurator.configure()
    WireMock.configureFor("localhost", serverPort)
    wireMockServer.start()
  }

  def resetMocks = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }
}
