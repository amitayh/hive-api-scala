package com.wix.hive.infrastructure

import java.lang.reflect.{ParameterizedType, Type}
import java.util.concurrent.atomic.AtomicReference

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.`type`.TypeReference
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.{Request, RequestListener, Response}
import com.wix.hive.json.JacksonObjectMapper
import org.specs2.matcher.Matchers

import scala.collection.immutable.List
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

private[infrastructure] trait WiremockEnvironment extends Matchers {
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

  val listeners = new AtomicReference[scala.List[(Request, Response) => Unit]](Nil)

  /**
   * @deprecated use addListener
   */
  @Deprecated
  def setListener(f: (Request, Response) => Unit): Unit = addListener(f)

  /**
   * @deprecated use removeListeners
   */
  @Deprecated
  def removeListener(): Unit = removeListeners()

  def addListener(f: (Request, Response) => Unit): Unit = listeners.set(f :: listeners.get())

  def removeListeners(): Unit = listeners.set(Nil)

  private def subscribeEventListenerToRequests(server: WireMockServer): Unit =
    server.addMockServiceRequestListener(new RequestListener {
      override def requestReceived(request: Request, response: Response): Unit =
        listeners.get().foreach(_.apply(request, response))
    })


  def resetMocks(): Unit = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }

  class Collector[T](implicit mn: Manifest[T]) extends mutable.Iterable[T] {
    private val requests = List.newBuilder[T]

    def requestListener: (Request, Response) => Unit = (request, response) => {
      try {
        requests += JsonAs[T](request.getBodyAsString)
      } catch {
        case _: JsonProcessingException => //Ignore commands that does not parse for defined type
      }
    }

    override def iterator: Iterator[T] = requests.result().iterator
  }

  def collect[T](execution: => Unit)(implicit mn: Manifest[T], ec: ExecutionContext): Future[Collector[T]] = {
    val collector = new Collector[T]
    WiremockEnvironment.addListener(collector.requestListener)
    Future {
      execution
      collector
    }
  }

}

object WiremockEnvironment extends WiremockEnvironment {
  override val serverPort: Int = 9089
}

private[infrastructure] object JsonAs {

  def apply[T](json: String)(implicit mn: Manifest[T]): T = {
    JacksonObjectMapper.mapper.readValue(json, typeReference[T])
  }

  private def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) {
      m.runtimeClass
    }
    else new ParameterizedType {
      def getRawType = m.runtimeClass

      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

      def getOwnerType = null
    }
  }
}
