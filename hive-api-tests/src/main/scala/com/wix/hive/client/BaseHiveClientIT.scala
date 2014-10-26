package com.wix.hive.client

import java.util.UUID

import com.wix.hive.commands.GetActivityById
import com.wix.hive.commands.contacts.{GetContactById, GetContacts}
import com.wix.hive.model.{AuthRegister, Activity, Contact}
import org.joda.time.DateTime
import org.specs2.matcher.Matcher
import org.specs2.mutable.{Before, SpecificationWithJUnit}
import org.specs2.specification.Scope
import org.specs2.time.NoTimeConversions._

import scala.concurrent.duration.FiniteDuration


abstract class BaseHiveClientIT extends SpecificationWithJUnit {
  this: HiveApiDrivers =>

  sequential

  def initEnv(): Unit

  def shutdownEnv(): Unit

  def beforeTest(): Unit

  val baseUrl: String

  step(initEnv())


  trait Context extends Scope {//Before {
    def before = beforeTest()

    val me = AppDef.random


    val client = new HiveClient(me.appId, me.secret, me.instanceId, baseUrl = baseUrl)

    def now = new DateTime()

    def beAContactWith(id: String): Matcher[Contact] = (contact: Contact) => contact.id == id

    def beAnActivityWith(id: String): Matcher[Activity] = (activity: Activity) => activity.id == id
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

      client.execute(GetActivityById(activityId)) must beAnActivityWith(id = activityId).await(timeout = FiniteDuration(100, "seconds"))
    }
  }

  step(shutdownEnv())
}

trait HiveApiDrivers {

  def randomId: String = UUID.randomUUID().toString

  def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit

  def givenAppWithActivities(myself: AppDef, activities: Activity*): Unit


  case class AppDef(appId: String, instanceId: String, secret: String)

  object AppDef {
    def random: AppDef = AppDef(randomId, randomId, randomId)
  }

}