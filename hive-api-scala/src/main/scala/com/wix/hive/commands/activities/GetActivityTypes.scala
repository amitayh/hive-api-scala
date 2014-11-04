package com.wix.hive.commands.activities

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.model.activities.ActivityTypes

case class GetActivityTypes() extends ActivityCommand[ActivityTypes] {
  override val urlParams: String = "/types"

  override val method: HttpMethod = GET
}
