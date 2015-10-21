package com.wix.hive.infrastructure

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.{Request, RequestListener, Response}
import org.specs2.matcher.Matchers

private[infrastructure] trait WiremockEnvironment extends Matchers {
  val serverPort: Int

  type Listener = (Request, Response) => Unit

  private lazy val server: WireMockServer = {
    WireMock.configureFor("localhost", serverPort)
    val wireMockServer = new WireMockServer(new WireMockConfiguration().port(serverPort))
    wireMockServer.start()

    subscribeEventListenerToRequests(wireMockServer)

    wireMockServer
  }

  def start(): Unit = server

  def stop(): Unit = WireMock.shutdownServer()

  val listeners = new ConcurrentLinkedQueue[Listener]()

  /**
   * @deprecated use addListener
   */
  @Deprecated
  def setListener(f: Listener): Unit = addListener(f)

  /**
   * @deprecated use removeListeners
   */
  @Deprecated
  def removeListener(): Unit = removeListeners()

  def addListener(listener: Listener): Unit = listeners.add(listener)

  def removeListeners(): Unit = listeners.clear

  private def subscribeEventListenerToRequests(server: WireMockServer): Unit = {
    server.addMockServiceRequestListener(new RequestListener {
      override def requestReceived(request: Request, response: Response): Unit =
        listeners.forEach(new Consumer[Listener]() {
          override def accept(listener: Listener): Unit = listener(request, response)
        })
    })
  }

  def resetMocks(): Unit = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }

}

object WiremockEnvironment extends WiremockEnvironment {
  override val serverPort: Int = 9089
}

