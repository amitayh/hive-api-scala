package com.wix.hive.client

import com.wix.hive.client.infrastructure.SimplicatorWebhooksDriver

/**
 * User: maximn
 * Date: 12/2/14
 */
class WebhooksTestkit(override val url: String, override val port: Int, override val secret: String) extends SimplicatorWebhooksDriver
