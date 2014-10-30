package com.wix.hive.client


import java.util.UUID

import com.wix.hive.commands._
import com.wix.hive.commands.contacts.{GetContactById, PageSizes}
import com.wix.hive.model.ActivityType.ActivityType
import com.wix.hive.model._
import org.joda.time.DateTime
import org.specs2.matcher.Matcher
import org.specs2.mutable.{Before, SpecificationWithJUnit}
import org.specs2.time.NoTimeConversions

import scala.concurrent.Await
import scala.concurrent.duration._


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


    val client = new HiveClient(me.appId, me.secret, me.instanceId, baseUrl = baseUrl)

    def now = new DateTime()

    val activityId = randomId

    val activcity = Activity(id = "id", createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE"))
    val firstPage = (0 to 25).map((id: Int) => activcity.copy(id = id.toString))
    val secondPage = (25 to 40).map((id: Int) => activcity.copy(id = id.toString))
    val allActivities = firstPage ++ secondPage

    implicit def value2BeMatcher[T](t: T): Matcher[T] = be_===(t)

    def beAContactWith(id: String): Matcher[Contact] = (_: Contact).id == id

    def beAnActivityWith(id: String): Matcher[Activity] = (activity: Activity) => activity.id == id

    def haveTypes(types: Seq[ActivityType]): Matcher[ActivityTypes] = (_: ActivityTypes).types == types

    def haveActivityResult: Matcher[ActivityCreatedResult] = (res: ActivityCreatedResult) => res.activityId.nonEmpty && res.contactId.nonEmpty

    def haveSameIds(activities: Activity*): Matcher[PagingActivitiesResult] = (res: PagingActivitiesResult) => res.results.map(_.id).toSet == activities.map(_.id).toSet

    def haveSiteUrl(url: Matcher[String]): Matcher[SiteData] = url ^^ ((_:SiteData).url aka "siteUrl")
  }

  "Hive client" should {

    "get contact by ID" in new Context {
      val userId = randomId
      givenContactFetchById(myself = me, respondsWith = Contact(id = userId, createdAt = new DateTime()))

      client.execute(GetContactById(userId)) must beAContactWith(id = userId).await
    }

    "get activity by ID" in new Context {
      givenAppWithActivitiesById(me, Activity(id = activityId, createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE")))

      client.execute(GetActivityById(activityId)) must beAnActivityWith(id = activityId).await
    }

    "get list of all activity types" in new Context {
      val types = Seq(ActivityType.`auth/login`, ActivityType.`music/album-fan`)
      givenAppActivityTypes(me, types: _*)

      client.execute(GetActivityTypes()) must haveTypes(types).await
    }

    "create activity for contact" in new Context {
      givenAppWithContactExist(me, randomId)
      val userSessionToken = getValidUserSessionToken

      val command = CreateActivity(userSessionToken, ActivityCreationData(createdAt = now, activityInfo = AuthRegister("iunt", "preAc", "ACTIVE")))

      client.execute(command) must haveActivityResult.await

      verifyActivityCreated(me)
    }

    "get all activities" in new Context {
      val activity = Activity(id = activityId, createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE"))

      givenAppWithActivitiesBulk(me, activity)

      client.execute(GetActivities()) must haveSameIds(activity).await
    }

    "get all activities with paging" in new Context {
      givenAppWithActivitiesBulk(me, allActivities: _*)

      val firstPageResult = Await.result(client.execute(GetActivities(pageSize = PageSizes.`25`)), 1.second)
      firstPageResult must haveSameIds(firstPage: _*)

      firstPageResult.nextPageCommand match {
        case Some(cmd) => client.execute(cmd) must haveSameIds(secondPage: _*).await()
        case None => failure("Didn't get the second page")
      }
    }.pendingUntilFixed("PageSize is not implemented in the server")

    "get site's URL" in new Context {
      val url = "http://somesite.com/wix"
      givenAppWithSite(me, url)

      client.execute(Site) must haveSiteUrl(url).await
    }

    "create 'Notification' message to all users of the application" in new Context {
      val notification = new NotificationCreationData("title", "content", NotificationType.BusinessTips)

      failure("not implemented on server side")
    }.pendingUntilFixed
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


  case class AppDef(appId: String, instanceId: String, secret: String)

  object AppDef {
    def random: AppDef = AppDef(randomId, randomId, randomId)
  }

}