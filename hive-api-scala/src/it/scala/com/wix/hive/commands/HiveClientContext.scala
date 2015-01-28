package com.wix.hive.commands

import com.wix.hive.client.HiveClient
import com.wix.hive.infrastructure.{AppDef, WiremockEnvironment}
import org.specs2.mutable.Before

/**
 * User: maximn
 * Date: 1/28/15
 */
trait HiveClientContext extends Before {

  override def before: Any = WiremockEnvironment.resetMocks()

  val baseUrl = s"http://localhost:${HiveSimplicatorIT.serverPort}"

  val app = AppDef.random
  val instance = app.instanceId

  val client = new HiveClient(app.appId, app.secret, baseUrl = baseUrl)

}
