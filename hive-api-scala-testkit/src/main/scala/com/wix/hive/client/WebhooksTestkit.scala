package com.wix.hive.client

import com.wix.hive.client.infrastructure.SimplicatorWebhooksDriver

/**
 * User: maximn
 * Date: 12/2/14
 */
trait WebhooksTestkit extends SimplicatorWebhooksDriver{
  val url: String
  val port: Int
  val secret: String
}