package com.wix.hive.server

import com.twitter.finagle.ListeningServer
import com.twitter.util.{Duration, Future}
import com.wix.hive.server.adapters.RequestConverterFrom

/**
 * User: maximn
 * Date: 11/27/14
 */
trait WebServerBase {
  def start(): ListeningServer

  def stop(after: Duration): Future[Unit]

  protected def process[T: RequestConverterFrom](data: T): Unit
}
