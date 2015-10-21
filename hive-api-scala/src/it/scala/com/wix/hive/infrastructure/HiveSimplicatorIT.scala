package com.wix.hive.infrastructure

import java.lang.reflect.{ParameterizedType, Type}

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.`type`.TypeReference
import com.github.tomakehurst.wiremock.http.{Request, Response}
import com.wix.hive.json.JacksonObjectMapper
import org.specs2.mutable.SpecificationWithJUnit

import scala.collection.immutable.List
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
 * User: maximn
 * Date: 1/13/15
 */

trait HiveSimplicatorIT extends SpecificationWithJUnit with SimplicatorHive {
  sequential
  WiremockEnvironment.start

  class Recorder[T: Manifest] extends mutable.Iterable[T] {
    private val requests = List.newBuilder[T]

    def recordingListener: (Request, Response) => Unit = (request, response) => synchronized {
      try {
        requests += JsonAs[T](request.getBodyAsString)
      } catch {
        case _: JsonProcessingException => //Ignore commands that does not parse for defined type
      }
    }

    override def iterator: Iterator[T] = requests.result().iterator
  }

  def RecordHiveCommands[T: Manifest](execution: => Unit)(implicit ec: ExecutionContext): Future[Recorder[T]] = {
    val recorder = new Recorder[T]
    WiremockEnvironment.addListener(recorder.recordingListener)
    Future {
      execution
      recorder
    }
  }
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

