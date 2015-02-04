package com.wix.hive.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.{Request, RequestListener, Response}

trait WiremockEnvironment {
  val serverPort: Int

  private lazy val server: WireMockServer = {
    WireMock.configureFor("localhost", serverPort)
    val wireMockServer = new WireMockServer(new WireMockConfiguration().port(serverPort))
    wireMockServer.start()

    wireMockServer
  }

  def start(): Unit = server

  def stop(): Unit = {
    WireMock.shutdownServer()
  }


  def addEventListener(f: (Request, Response) => Unit): Unit =
    server.addMockServiceRequestListener(new RequestListener {
      override def requestReceived(request: Request, response: Response): Unit =
        f(request, response)
    })


  def resetMocks(): Unit = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }
}

object WiremockEnvironment extends WiremockEnvironment {
  override val serverPort: Int = 9089
}
