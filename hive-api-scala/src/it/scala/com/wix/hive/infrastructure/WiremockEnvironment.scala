package com.wix.hive.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.{Response, Request, RequestListener}

//TODO: Merge to 1 wiremock after killink tests/contract tests modules
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

object WiremockSimplicator {
  val serverPort: Int = 9089

  lazy val server: WireMockServer = {
    WireMock.configureFor("localhost", serverPort)
    val wireMockServer = new WireMockServer(new WireMockConfiguration().port(serverPort))
    wireMockServer.start()

//    wireMockServer.addMockServiceRequestListener(new RequestListener {
//      override def requestReceived(request: Request, response: Response): Unit = println(request)
//    })

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