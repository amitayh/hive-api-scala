package com.wix.hive.server

import com.wix.hive.client.http.HttpRequestData

/**
 * User: maximn
 * Date: 11/27/14
 */


//
//
//class InstanceDecoder(key: String) {
//  def decode(instance: String): Try[WixInstance] = ???
//}
//
//class WixInstance()
//

//"instanceId": "138e00ea-3284-8869-ebbc-0314d8854549",
//"signDate": "2014-11-25T07:21:10.920Z",
//"uid": "fde01512-8eed-4f42-878f-b891a7a1be66",
//"permissions": "OWNER",
//"ipAndPort": "88.119.150.196/35982",
//"vendorProductId": null,
//"demoMode": false

//trait WebhookRequestProcessor extends ReqeustProcessor[Webhook] {
//  val secret: String
//  private val processor = new WebhooksProcessor(secret)
//
//  override def process[T < HttpRequestData](data: HttpRequestData): Try[Webhook] = processor.convert(data)
//}




//class PrintlnEventProcessor[T <: Webhook] extends WebhookProcessor[T] {
//  override def process(data: Try[_]): Unit = println(data.toString)
//}