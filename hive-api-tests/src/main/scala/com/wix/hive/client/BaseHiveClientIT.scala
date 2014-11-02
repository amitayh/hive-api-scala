package com.wix.hive.client

import java.util.UUID

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.commands._
import com.wix.hive.model.ActivityType.ActivityType
import com.wix.hive.model._
import dispatch.url
import org.joda.time.DateTime
import org.specs2.matcher.Matcher
import org.specs2.mutable.{Before, SpecificationWithJUnit}
import org.specs2.time.NoTimeConversions
import scala.concurrent.duration._

import scala.concurrent.Await


abstract class BaseHiveClientIT extends SpecificationWithJUnit with NoTimeConversions {
  this: HiveApiDrivers =>

  sequential

  def initEnv(): Unit

  def shutdownEnv(): Unit

  def beforeTest(): Unit

  val baseUrl: String

  step(initEnv())


  trait Context extends Before {
    def before = beforeTest()

    val me = AppDef.random
    val instance = me.instanceId

    val client = new HiveClient(me.appId, me.secret, baseUrl = baseUrl)

    def now = new DateTime()

    val activityId = randomId

    val activity = Activity(id = "id", createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE"))
    val pagingFirstPage = (0 to 25).map((id: Int) => activity.copy(id = id.toString))
    val paigngSecondPage = (25 to 40).map((id: Int) => activity.copy(id = id.toString))
    val pagingAllActivities = pagingFirstPage ++ paigngSecondPage

    implicit def value2BeMatcher[T](t: T): Matcher[T] = be_===(t)


    def beAnActivityWith(id: String): Matcher[Activity] = (activity: Activity) => activity.id == id

    def haveTypes(types: Seq[ActivityType]): Matcher[ActivityTypes] = (_: ActivityTypes).types == types

    def haveActivityResult: Matcher[ActivityCreatedResult] = (res: ActivityCreatedResult) => res.activityId.nonEmpty && res.contactId.nonEmpty

    def haveSameIds(activities: Activity*): Matcher[PagingActivitiesResult] = (res: PagingActivitiesResult) => res.results.map(_.id).toSet == activities.map(_.id).toSet

    def haveSiteUrl(url: String): Matcher[SiteData] = ((_: SiteData).url) ^^ be_==(url)

    def matchActivitySummary(summary: ActivitySummary): Matcher[ActivitySummary] = (s: ActivitySummary) => (s.total == summary.total) && (s.activityTypes.length == summary.activityTypes.length)

    def haveActivityOfType(typ: ActivityType): Matcher[Seq[ActivityTypesSummary]] = (_:Seq[ActivityTypesSummary]).exists(_.activityType == Some(typ))
    def haveActivityOfType(typ: ActivityType, total: Int): Matcher[ActivitySummary] =
      be_===(total) ^^ { (_: ActivitySummary).total aka "total" } and
        haveActivityOfType(typ) ^^ { (_: ActivitySummary).activityTypes aka "types" }
  }

  "Hive client" should {

    "get activity by ID" in new Context {
      givenAppWithActivitiesById(me, Activity(id = activityId, createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE")))

      client.execute(me.instanceId, GetActivityById(activityId)) must beAnActivityWith(id = activityId).await
    }

    "get list of all activity types" in new Context {
      val types = Seq(ActivityType.`auth/login`, ActivityType.`music/album-fan`)
      givenAppActivityTypes(me, types: _*)

      client.execute(me.instanceId, GetActivityTypes()) must haveTypes(types).await
    }

    "create activity for contact" in new Context {
      givenAppWithContactExist(me, randomId)
      val userSessionToken = getValidUserSessionToken

      val command = CreateActivity(userSessionToken, ActivityCreationData(createdAt = now, activityInfo = AuthRegister("iunt", "preAc", "ACTIVE")))

      client.execute(instance, command) must haveActivityResult.await

      verifyActivityCreated(me)
    }

    "get all activities" in new Context {
      val allActivities = Activity(id = activityId, createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE"))

      givenAppWithActivitiesBulk(me, allActivities)

      client.execute(instance, GetActivities()) must haveSameIds(allActivities).await
    }

    "get all activities with paging" in new Context {
      givenAppWithActivitiesBulk(me, pagingAllActivities: _*)

      val firstPageResult = Await.result(client.execute(instance, GetActivities(pageSize = PageSizes.`25`)), Duration("1 second"))
      firstPageResult must haveSameIds(pagingFirstPage: _*)

      firstPageResult.nextPageCommand match {
        case Some(cmd) => client.execute(instance, cmd) must haveSameIds(paigngSecondPage: _*).await()
        case None => failure("Didn't get the second page")
      }
    }.pendingUntilFixed("PageSize is not implemented in the server")

    "get site's URL" in new Context {
      val url = "http://somesite.com/wix"
      givenAppWithSite(me, url)

      client.execute(instance, Site) must haveSiteUrl(url).await
    }

    "create 'Notification' message to all users of the application" in new Context {
      val notification = new NotificationCreationData("title", "content", NotificationType.BusinessTips)

      failure("not implemented on server side")
    }.pendingUntilFixed


    "get insights (activity summary) for a contact" in new Context {
      val contactId = "cb2c0182-0ac7-4c80-acfb-09cc8c5fb744"
      val activityType = ActivityType.`auth/login`
      val summaryFrom = new DateTime(2010, 1, 1, 0, 0)
      val summary = ActivitySummary(Seq(ActivityTypesSummary(Some(activityType), 1, summaryFrom)), 1, summaryFrom)
      givenAppWithUserActivities(me, contactId, summary)

      client.execute(instance, InsightActivitySummary(Some(contactId))) must haveActivityOfType(typ = activityType, total = 1).await
    }


    "get insights (activity summary) for all contacts" in new Context {
      val activityType = ActivityType.`auth/login`
      val summaryFrom = new DateTime(2010, 1, 1, 0, 0)
      val summary = ActivitySummary(Seq(ActivityTypesSummary(Some(activityType), 1, summaryFrom)), 1, summaryFrom)
      givenAppWithActivities(me, summary)

      client.execute(instance, InsightActivitySummary()) must haveActivityOfType(typ = activityType, total = 1).await
    }
  }

  step(shutdownEnv())
}

trait HiveApiDrivers {

  def randomId: String = UUID.randomUUID().toString

  def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit

  def givenAppWithActivitiesById(myself: AppDef, activities: Activity*): Unit

  def givenAppWithActivitiesBulk(myself: AppDef, activities: Activity*): Unit

  def givenAppActivityTypes(app: AppDef, types: ActivityType*): Unit

  def givenAppWithContactExist(app: AppDef, contactId: String): Unit

  def getValidUserSessionToken: String

  def verifyActivityCreated(appDef: AppDef): Unit

  def givenAppWithSite(appDef: AppDef, url: String): Unit

  def givenAppWithUserActivities(app: AppDef, contactId: String, responseWith: ActivitySummary): Unit

  def givenAppWithActivities(app: AppDef, responseWith: ActivitySummary): Unit

  case class AppDef(appId: String, instanceId: String, secret: String)

  object AppDef {
    def random: AppDef = AppDef(randomId, randomId, randomId)
  }
}