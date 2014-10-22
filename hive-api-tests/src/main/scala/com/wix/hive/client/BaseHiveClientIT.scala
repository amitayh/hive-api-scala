package com.wix.hive.client

import java.util.UUID

import com.wix.hive.commands.contacts.{GetContacts, GetContactById}
import com.wix.hive.model.Contact
import org.joda.time.DateTime
import org.specs2.matcher.{MatchResult, Expectable, Matcher}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope


abstract class BaseHiveClientIT extends SpecificationWithJUnit { this: HiveApiDrivers =>

  def initEnv(): Unit
  def shutdownEnv(): Unit

  val baseUrl: String

  step(initEnv())

  trait Context extends Scope {
println(new GetContacts())

    val me = AppDef.random

    val client = new HiveClient(me.appId, me.secret, me.instanceId, baseUrl = baseUrl)

    def beAContactWith(id: String): Matcher[Contact] =  new Matcher[Contact] {
      override def apply[S <: Contact](t: Expectable[S]): MatchResult[S] =  result(t.value.id == id, "", "", t)
    }
  }

  "Hive client" should {

    "get contact by ID" in new Context {
      val userId = randomId
      givenContactFetchById(myself = me, respondsWith = Contact(id = userId, createdAt = new DateTime()))

      val command = GetContactById(userId)

      client.execute(command) must beAContactWith(id = userId).await
    }
  }

  step(shutdownEnv())
}

trait HiveApiDrivers {

  def randomId: String = UUID.randomUUID().toString

  def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit

  case class AppDef(appId: String, instanceId: String, secret: String)
  object AppDef {
    def random: AppDef = AppDef(randomId, randomId, randomId)
  }
}