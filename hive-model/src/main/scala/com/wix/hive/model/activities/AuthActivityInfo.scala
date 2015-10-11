package com.wix.hive.model.activities

import com.wix.hive.model.activities.ActivityType._
import com.wix.hive.model.activities.SiteMemberStatus.SiteMemberStatus

/**
 * @author maximn
 * @since 08-Sep-2015
 */

trait AuthActivityInfo extends ActivityInfo {
  val activityType: ActivityType

  def initiator: String
  def previousActivityStreamId: String
}

case class AuthLogin(initiator: String, previousActivityStreamId: String) extends AuthActivityInfo {
  override val activityType: ActivityType = `auth/login`
}


case class AuthStatusInfo(initiator: String,
                          previousActivityStreamId: String,
                          status: SiteMemberStatus) extends AuthActivityInfo{
  override val activityType: ActivityType = `auth/status-change`

}

object SiteMemberStatus extends Enumeration {
  type SiteMemberStatus = Value
  val APPLICANT, ACTIVE, INACTIVE, DENIED = Value
}
