package com.wix.hive.drivers

import com.wix.hive.commands.activities.{CreateActivity, GetActivities, GetActivityById, PagingActivitiesResult}
import com.wix.hive.commands.contacts.{CreateContactActivity, GetContactActivities}
import com.wix.hive.model.activities.ActivityType._
import com.wix.hive.model.activities.{ActivityType, _}
import com.wix.hive.model.insights.{ActivitySummary, ActivityTypesSummary}
import org.joda.time.DateTime
import org.specs2.matcher.{Matcher, Matchers}

/**
 * User: maximn
 * Date: 1/27/15
 */
trait ActivitiesTestSupport extends Matchers {
  def beActivitiesWithIds(activities: Activity*): Matcher[PagingActivitiesResult] = (res: PagingActivitiesResult) => res.results.map(_.id).toSet == activities.map(_.id).toSet

  def haveActivityResult(activityId: Matcher[String] = not(beEqualTo("")),
                         contactId: Matcher[String] = not(beEqualTo(""))): Matcher[ActivityCreatedResult] = {
    activityId ^^ {(_: ActivityCreatedResult).activityId aka "activityId"} and
      contactId ^^ {(_: ActivityCreatedResult).contactId aka "contactId"}
  }

  def beAnActivityWith(idMatcher: Matcher[String]): Matcher[Activity] = idMatcher ^^ {(_: Activity).id aka "id"}

  def haveTypes(types: Seq[ActivityType]): Matcher[ActivityTypes] = be_===(types) ^^ {(_: ActivityTypes).types aka "types"}


  def now = new DateTime()

  val cursor = "5e841234-9d1b-432a-b0dc-d8747a23bb87"
  val activityId = "d903da2e-c3b9-40cf-b2ad-2ff879c26f09"

  val authRegister = AuthRegister("ini", "stream", "ACTIVE")
  val activity = Activity(id = activityId, createdAt = now, activityInfo = authRegister)
  val pagingFirstPage = (0 to 25).map((id: Int) => activity.copy(id = id.toString))
  val paigngSecondPage = (25 to 40).map((id: Int) => activity.copy(id = id.toString))
  val pagingAllActivities = pagingFirstPage ++ paigngSecondPage

  val summaryAactivityType = ActivityType.`auth/login`
  val summaryFrom = new DateTime(2010, 1, 1, 10, 10)
  val summary = ActivitySummary(Seq(ActivityTypesSummary(Some(summaryAactivityType), 1, summaryFrom)), 1, summaryFrom)

  val pagingActivityResult = PagingActivitiesResult(1, None, None, Seq(activity))

  val createdAt = new DateTime(2015, 1, 2, 3, 4)

  val userSessionId = "8e9b4d2e-e304-4109-955e-dda7416e83a7"

  val types = ActivityTypes(ActivityType.values.toSeq)

  def aGetContactActivitiesCommand(contactId: String) = GetContactActivities(contactId, cursor = Some(cursor))

  def aCreateContactActivity(contactId: String) = CreateContactActivity(contactId, createdAt, activityInfo = authRegister)

  def anActivityCreateResult(contactId: String) = ActivityCreatedResult(activityId, contactId)

  val getActivityByIdCommand = GetActivityById(activityId)
  val createActivityCommand = CreateActivity(userSessionId, ActivityCreationData(createdAt = now, activityInfo = authRegister))

  val getActivitiesCommand = GetActivities()
}

object ActivitiesTestSupport extends ActivitiesTestSupport{

}