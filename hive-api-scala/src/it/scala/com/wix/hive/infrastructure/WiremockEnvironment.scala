package com.wix.hive.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.{Request, RequestListener, Response}

private[infrastructure] trait WiremockEnvironment {
  val serverPort: Int

  private lazy val server: WireMockServer = {
    WireMock.configureFor("localhost", serverPort)
    val wireMockServer = new WireMockServer(new WireMockConfiguration().port(serverPort))
    wireMockServer.start()

    subscribeEventListenerToRequests(wireMockServer)

    wireMockServer
  }

  def start(): Unit = server

  def stop(): Unit = {
    WireMock.shutdownServer()
  }

  val emptyListener:((Request, Response) => Unit) = (req, res) => {}

  var listener: ((Request, Response) => Unit) = emptyListener

  def setListener(f: (Request, Response) => Unit): Unit = listener = f

  def removeListener(): Unit = listener = emptyListener

  private def subscribeEventListenerToRequests(server: WireMockServer): Unit =
    server.addMockServiceRequestListener(new RequestListener {
      override def requestReceived(request: Request, response: Response): Unit =
        listener(request, response)
    })


  def resetMocks(): Unit = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }
}

object WiremockEnvironment extends WiremockEnvironment {
  override val serverPort: Int = 9089
}
