package com.wix.hive.model.activities

import com.wix.accord.dsl._
import com.wix.hive.model.activities.ActivityType._

/**
 * @author maximn
 * @since 08-Sep-2015
 */

trait AuthActivityInfo extends ActivityInfo {
  val activityType: ActivityType

  def initiator: String
  def previousActivityStreamId: String
}

case class AuthLogin(initiator: String, previousActivityStreamId: String) extends ActivityInfo {
  override val activityType: ActivityType = `auth/login`
}

object AuthLogin {
  implicit val authLoginInfoValidator = validator[AuthLogin] { a =>
    a.previousActivityStreamId is notNull
  }
}
