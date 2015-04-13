package com.wix.hive.infrastructure

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.adapters.RequestConverterFrom

/**
 * User: maximn
 * Date: 4/12/15
 */
object IdentityRequestConverter {
  implicit object RequestConverterFromHttpRequestData extends RequestConverterFrom[HttpRequestData] {
    override def convert(req: HttpRequestData): HttpRequestData = req
  }
}
