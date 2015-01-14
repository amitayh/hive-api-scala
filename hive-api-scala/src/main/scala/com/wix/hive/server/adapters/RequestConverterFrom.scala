package com.wix.hive.server.adapters

import com.wix.hive.client.http.HttpRequestData

/**
 * User: maximn
 * Date: 1/13/15
 */
trait RequestConverterFrom[T] {
  def convert(req: T): HttpRequestData
}
