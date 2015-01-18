package com.wix.hive.client

import com.wix.hive.commands.activities.PagingActivitiesResult
import com.wix.hive.commands.contacts.{CreatedContact, UpsertContactResponse}
import com.wix.hive.commands.services.{Providers, Provider}
import com.wix.hive.model.activities.ActivityType._
import com.wix.hive.model.activities.{Activity, ActivityCreatedResult, ActivityTypes}
import com.wix.hive.model.contacts.{Contact, PagingContactsResult}
import com.wix.hive.model.insights.{ActivitySummary, ActivityTypesSummary}
import com.wix.hive.model.sites.SiteData
import org.specs2.matcher.{MustMatchers, Matcher, Matchers}


trait HiveCommandsMatchers extends Matchers {
  implicit def value2BeMatcher[T <: String](t: T): Matcher[T] = be_===(t)

  def beContactWithId(matcher: Matcher[String]): Matcher[Contact] = matcher ^^ { (_: Contact).id aka "contactId" }

  def beContactsWith(matcher: Matcher[Seq[Contact]]): Matcher[PagingContactsResult] = matcher ^^ { (_: PagingContactsResult).results aka "results" }

  def beCreatedContactWithId(matcher: Matcher[String]): Matcher[CreatedContact] = matcher ^^ { (_: CreatedContact).contactId aka "contactId" }

  def beAnActivityWith(idMatcher: Matcher[String]): Matcher[Activity] = idMatcher ^^ { (_: Activity).id aka "id" }

  def haveTypes(types: Seq[ActivityType]): Matcher[ActivityTypes] = be_===(types) ^^ { (_: ActivityTypes).types aka "types" }

  def haveActivityResult: Matcher[ActivityCreatedResult] = {
    not(beEqualTo("")) ^^ { (_: ActivityCreatedResult).activityId aka "activityId" } and
    not(beEqualTo("")) ^^ { (_: ActivityCreatedResult).contactId aka "contactId" }
  }

  def haveSameIds(activities: Activity*): Matcher[PagingActivitiesResult] = (res: PagingActivitiesResult) => res.results.map(_.id).toSet == activities.map(_.id).toSet

  def haveSiteUrl(url: String): Matcher[SiteData] = ((_: SiteData).url) ^^ be_==(url)

  def matchActivitySummary(summary: ActivitySummary): Matcher[ActivitySummary] = {
    be_===(summary.total) ^^ { (_: ActivitySummary).total aka "total" } and
    be_===(summary.activityTypes.length) ^^ { (_: ActivitySummary).activityTypes.length aka "activityTypes.length" }
  }

  def haveActivityOfType(typ: ActivityType): Matcher[Seq[ActivityTypesSummary]] = (_: Seq[ActivityTypesSummary]).exists(_.activityType == Some(typ))

  def haveActivityOfType(typ: ActivityType, total: Int): Matcher[ActivitySummary] = {
    be_===(total) ^^ { (_: ActivitySummary).total aka "total" } and
    haveActivityOfType(typ) ^^ { (_: ActivitySummary).activityTypes aka "types" }
  }

  def haveUpsertContactId(id: String): Matcher[UpsertContactResponse] = be_===(id) ^^ { (_: UpsertContactResponse).contactId aka "contactId" }
}
