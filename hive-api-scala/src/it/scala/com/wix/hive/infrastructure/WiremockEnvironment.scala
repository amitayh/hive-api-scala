package com.wix.hive.infrastructure

import java.util.concurrent.atomic.AtomicReference

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

  def stop(): Unit = WireMock.shutdownServer()


  val emptyListener: (Request, Response) => Unit = (req: Request, res: Response) => {}

  val listener = new AtomicReference[(Request, Response) => Unit](emptyListener)

  def setListener(f: (Request, Response) => Unit): Unit = listener.set(f)

  def removeListener(): Unit = listener.set(emptyListener)

  private def subscribeEventListenerToRequests(server: WireMockServer): Unit =
    server.addMockServiceRequestListener(new RequestListener {
      override def requestReceived(request: Request, response: Response): Unit =
        listener.get().apply(request, response)
    })


  def resetMocks(): Unit = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }
}

object WiremockEnvironment extends WiremockEnvironment {
  override val serverPort: Int = 9089
}
