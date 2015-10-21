package com.wix.hive.infrastructure

import com.fasterxml.jackson.core.JsonProcessingException
import com.github.tomakehurst.wiremock.http.{Request, Response}
import org.specs2.mutable.SpecificationWithJUnit

import scala.collection.immutable.List
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

/**
 * User: maximn
 * Date: 1/13/15
 */

trait HiveSimplicatorIT extends SpecificationWithJUnit with SimplicatorHive {
  sequential
  WiremockEnvironment.start()

  class Recorder[T: ClassTag] {
    private val requests = List.newBuilder[T]

    def recordingListener: (Request, Response) => Unit = (request, response) => {
      try {
        requests.synchronized {
          requests += JsonAs[T](request.getBodyAsString)
        }
      } catch {
        case _: JsonProcessingException => //Ignore commands that does not parse for defined type
      }
    }

    def andPlay(player: (mutable.Iterable[T]) => Unit) = {
      player(records)
    }

    def records: mutable.Iterable[T] = {
      new mutable.Iterable[T] {
        override def iterator: Iterator[T] = requests.result().iterator
      }
    }
  }

  def RecordHiveCommands[T: ClassTag](execution: => Unit)(implicit ec: ExecutionContext): Recorder[T] = {
    val recorder = new Recorder[T]
    WiremockEnvironment.addListener(recorder.recordingListener)
    execution
    recorder
  }
}

