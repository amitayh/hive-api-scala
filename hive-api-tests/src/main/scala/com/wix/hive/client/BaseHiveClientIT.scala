package com.wix.hive.client

import java.util.UUID

import com.wix.hive.commands.{PostActivity, GetActivityTypes, GetActivityById}
import com.wix.hive.commands.contacts.{GetContactById, GetContacts}
import com.wix.hive.model._
import org.joda.time.DateTime
import org.specs2.matcher.Matcher
import org.specs2.mutable.{Before, SpecificationWithJUnit}
import org.specs2.specification.Scope

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, FiniteDuration}


abstract class BaseHiveClientIT extends SpecificationWithJUnit {
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

    def beAContactWith(id: String): Matcher[Contact] = (contact: Contact) => contact.id == id

    def beAnActivityWith(id: String): Matcher[Activity] = (activity: Activity) => activity.id == id

    def haveTypes(types: Seq[String]): Matcher[ActivityTypes] = (_: ActivityTypes).types == types

    def haveActivityResult(): Matcher[ActivityCreatedResult] = (res:ActivityCreatedResult) => res.activityId.nonEmpty && res.contactId.nonEmpty
  }

  "Hive client" should {

    "get contact by ID" in new Context {
      val userId = randomId
      givenContactFetchById(myself = me, respondsWith = Contact(id = userId, createdAt = new DateTime()))

      client.execute(GetContactById(userId)) must beAContactWith(id = userId).await
    }

    "get activity by ID" in new Context {
      val activityId = randomId

      givenAppWithActivities(me, Activity(id = activityId, createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE")))

      client.execute(GetActivityById(activityId)) must beAnActivityWith(id = activityId).await
    }

    "get list of all activity types" in new Context {
      val types = Seq("type1/some", "another/type_2")
      givenAppActivityTypes(me, types: _*)

      client.execute(GetActivityTypes()) must haveTypes(types).await
    }

    "create activity for contact" in new Context {
      givenAppWithContactExist(me, randomId)
      val userSessionToken = getValidUserSessionToken

      val command = PostActivity(userSessionToken, CreateActivity(createdAt = now, activityInfo = AuthRegister("iunt", "preAc", "ACTIVE")))

      client.execute(command) must haveActivityResult().await

      verifyActivityCreated(me)
    }
  }

  step(shutdownEnv())
}

trait HiveApiDrivers {

  def randomId: String = UUID.randomUUID().toString

  def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit

  def givenAppWithActivities(myself: AppDef, activities: Activity*): Unit

  def givenAppActivityTypes(app: AppDef, types: String*): Unit

  def givenAppWithContactExist(app: AppDef, contactId: String): Unit
  def getValidUserSessionToken: String

  def verifyActivityCreated(appDef: AppDef): Unit

  case class AppDef(appId: String, instanceId: String, secret: String)

  object AppDef {
    def random: AppDef = AppDef(randomId, randomId, randomId)
  }

}