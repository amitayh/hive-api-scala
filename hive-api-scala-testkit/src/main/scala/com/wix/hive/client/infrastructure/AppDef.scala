package com.wix.hive.client.infrastructure

import java.util.UUID

/**
 * User: maximn
 * Date: 11/18/14
 */
case class AppDef(appId: String, instanceId: String, secret: String)

object AppDef {
  private def randomId: String = UUID.randomUUID().toString

  def random: AppDef = AppDef(randomId, randomId, randomId)
}
