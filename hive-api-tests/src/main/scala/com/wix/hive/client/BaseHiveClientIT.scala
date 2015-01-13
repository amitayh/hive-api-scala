package com.wix.hive.client

import com.wix.hive.client.infrastructure.{AppDef, HiveApiDrivers}
import com.wix.hive.commands.activities._
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.commands.contacts._
import com.wix.hive.commands.insights.InsightActivitySummary
import com.wix.hive.commands.services.ServiceDone
import com.wix.hive.commands.sites.Site
import com.wix.hive.drivers.ServicesTestSupport
import com.wix.hive.model.activities._
import com.wix.hive.model.contacts._
import com.wix.hive.model.insights.{ActivitySummary, ActivityTypesSummary}
import com.wix.hive.model.notifications.{NotificationCreationData, NotificationType}
import org.joda.time.DateTime
import org.specs2.mutable.Before

import scala.concurrent.Await
import scala.concurrent.duration._


abstract class BaseHiveClientIT extends BaseIT  {
  this: HiveApiDrivers =>

  val baseUrl: String

  step(initEnv())

  trait Context extends Before
  with ServicesTestSupport
  with HiveCommandsMatchers {
    def before = beforeTest()

    val app = AppDef.random
    val callerApp = AppDef.random
    val providerApp = app

    val instance = app.instanceId

    val client = new HiveClient(app.appId, app.secret, baseUrl = baseUrl)

    val clientForInstance = client.executeForInstance(instance)

    def now = new DateTime()

    val contactId = "e5d81850-5dd8-407f-9acc-7ffd6c924ecf"
    val contact = Contact(contactId, new DateTime(2010, 1, 1, 0, 0))
    val anotherContactId = "c34a8709-6b14-4959-9db7-33a584daefad"
    val anotherContact = Contact(anotherContactId, new DateTime(2010, 1, 1, 0, 0))

    val contactName = ContactName(first = Some("First"), last = Some("Last"))

    val emailId = "48d21810-1a8a-4b69-ba25-8272f598667b"
    val contactEmail = ContactEmailDTO(email = "maximn@wix.com", tag = "emailtag", emailStatus = EmailStatus.OptOut)

    val dateId = "e1157acc-41aa-460e-87e9-7cee90778b06"
    val contactData = ContactData(name = Some(contactName), emails = Seq(contactEmail))

    val date = new DateTime(2013, 1, 2, 2 ,3)
    val contactDate = ContactDateDTO(tag = "date-tag", date)

    val addressId = "9a9cf711-d537-44a5-97e3-d45b7e7ffe53"
    val address = AddressDTO("tag-address-dto")

    val phone = "972-54-5556767"
    val email = "maximn@wix.com"
    val emailStatus = EmailStatus.OptOut

    val modifiedAt = new DateTime(2012, 2, 10, 10, 10)
    val phoneId = "2ac68c77-d4e6-4e37-9e82-bfa2479bb1d1"
    val contactPhone = ContactPhoneDTO("tag-phone-add", phone)

    val urlId = "c8226786-cca9-48a9-8750-a2043c867d35"
    val url = "http://wix.com/somesite"
    val contactUrl = ContactUrlDTO("tag-contact-add", url)

    val contactCompany = CompanyDTO(Some("role-comp"), contactName.first)
    val contactPicture = PictureDTO("some-pic")
    val contactAddress = AddressDTO("tag-address-contact")

    val activityId = "d903da2e-c3b9-40cf-b2ad-2ff879c26f09"

    val activity = Activity(id = "id", createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE"))
    val pagingFirstPage = (0 to 25).map((id: Int) => activity.copy(id = id.toString))
    val paigngSecondPage = (25 to 40).map((id: Int) => activity.copy(id = id.toString))
    val pagingAllActivities = pagingFirstPage ++ paigngSecondPage

    val summaryAactivityType = ActivityType.`auth/login`
    val summaryFrom = new DateTime(2010, 1, 1, 10, 10)
    val summary = ActivitySummary(Seq(ActivityTypesSummary(Some(summaryAactivityType), 1, summaryFrom)), 1, summaryFrom)
    val authRegister = AuthRegister("ini", "stream", "ACTIVE")

    val cursor = "5e841234-9d1b-432a-b0dc-d8747a23bb87"
  }

  "Hive client" should {

    "get contact by ID" in new Context {
      givenContactFetchById(app, contact)

      client.execute(instance, GetContactById(contactId)) must beContactWithId(contactId).await(3, FiniteDuration(3, "seconds"))
    }

    "get contacts" in new Context {
      givenAppWithContacts(app, Seq(contact, anotherContact): _*)

      client.execute(instance, GetContacts()) must beContactsWith(contain(allOf(beContactWithId(contactId), beContactWithId(anotherContactId)))).await
    }.pendingUntilFixed( """The server doesn't comply with the protocol, returns an array of Contacts instead of aPagingContactsResult""")

    "create a contact" in new Context {
      givenContactCreatedById(app, contactData, contactId)

      client.execute(instance, CreateContact(name = Some(contactName), emails = Seq(contactEmail))) must beCreatedContactWithId(contactId).await
    }

    "upsert a contact" in new Context {
      givenContactUpsertByPhoneAndEmail(app, Some(phone), Some(email), contactId)

      client.execute(instance, UpsertContact(Some(phone), Some(email))) must haveUpsertContactId(contactId).await
    }

    "add address to contact" in new Context {
      givenContactAddAddress(app, contactId, modifiedAt, address)

      client.execute(instance, AddAddress(contactId, modifiedAt, address)) must beContactWithId(contactId).await
    }

    "add email to contact" in new Context {
      givenContactAddEmail(app, contactId, modifiedAt, contactEmail)

      client.execute(instance, AddEmail(contactId, modifiedAt, contactEmail)) must beContactWithId(contactId).await
    }

    "add phone to contact" in new Context {
      givenContactAddPhone(app, contactId, modifiedAt, contactPhone)

      client.execute(instance, AddPhone(contactId, modifiedAt, contactPhone)) must beContactWithId(contactId).await
    }

    "add URL to contact" in new Context {
      givenContactAddUrl(app, contactId, modifiedAt, contactUrl)

      client.execute(instance, AddUrl(contactId, modifiedAt, contactUrl)) must beContactWithId(contactId).await
    }

    "add date to contact" in new Context {
      givenContactAddDate(app, contactId, modifiedAt, contactDate)

      client.execute(instance, AddDate(contactId, modifiedAt, contactDate)) must beContactWithId(contactId).await
    }

    "update contact's name" in new Context {
      givenContactUpdateName(app, contactId, modifiedAt, contactName)

      client.execute(instance, UpdateName(contactId, modifiedAt, contactName)) must beContactWithId(contactId).await
    }

    "update contact's company" in new Context {
      givenContactUpdateCompany(app, contactId, modifiedAt, contactCompany)

      client.execute(instance, UpdateCompany(contactId, modifiedAt, contactCompany)) must beContactWithId(contactId).await
    }

    "update contact's picture" in new Context {
      givenContactUpdatePicture(app, contactId, modifiedAt, contactPicture)

      client.execute(instance, UpdatePicture(contactId, modifiedAt, contactPicture)) must beContactWithId(contactId).await
    }.pendingUntilFixed("Documentation states PictureDTO but server expects string")

    "update contact's address" in new Context {
      givenContactUpdateAddress(app, contactId, modifiedAt, addressId, contactAddress)

      client.execute(instance, UpdateAddress(contactId, modifiedAt, addressId, contactAddress)) must beContactWithId(contactId).await
    }

    "update contact's email" in new Context {
      givenContactUpdateEmail(app, contactId, modifiedAt, emailId, contactEmail)

      client.execute(instance, UpdateEmail(contactId, modifiedAt, emailId, contactEmail)) must beContactWithId(contactId).await
    }

    "update cotnact's phone" in new Context {
      givenContactUpdatePhone(app, contactId, modifiedAt, phoneId, contactPhone)

      client.execute(instance, UpdatePhone(contactId, modifiedAt, phoneId, contactPhone)) must beContactWithId(contactId).await
    }

    "update contact's url" in new Context {
      givenContactUpdateUrl(app, contactId, modifiedAt, urlId, contactUrl)

      client.execute(instance, UpdateUrl(contactId, modifiedAt, urlId, contactUrl)) must beContactWithId(contactId).await
    }

    "update contact's date" in new Context {
      givenContactUpdateDate(app, contactId, modifiedAt, dateId, contactDate)

      client.execute(instance, UpdateDate(contactId, modifiedAt, dateId, contactDate)) must beContactWithId(contactId).await
    }

    "update contact's email subscription" in new Context {
      failure("Not implemented in the HUB")
    }.pendingUntilFixed

    "get activities for a given contact" in new Context {
      givenActivitiesForContact(app, contactId, cursor, activity)

      client.execute(instance, GetContactActivities(contactId, cursor = Some(cursor))) must haveSameIds(activity).await
    }

    "create activity for contact" in new Context {
      givenAppWithContactExist(app, contactId)

      import activity._
      client.execute(instance, CreateContactActivity(contactId, createdAt, activityLocationUrl, activityDetails, activityInfo)) must haveActivityResult.await
    }

    "get activity by ID" in new Context {
      givenAppWithActivitiesById(app, Activity(id = activityId, createdAt = now, activityInfo = authRegister))

      client.execute(app.instanceId, GetActivityById(activityId)) must beAnActivityWith(activityId).await
    }

    "get list of all activity types" in new Context {
      val types = Seq(ActivityType.`auth/login`, ActivityType.`music/album-fan`)
      givenAppActivityTypes(app, types: _*)

      client.execute(app.instanceId, GetActivityTypes()) must haveTypes(types).await
    }

    "create activity for contact using contact's user session" in new Context {
      givenAppWithContactExist(app, "dac62df7-7852-41fc-af0a-6b443745a5ab")
      val userSessionToken = getValidUserSessionToken

      val command = CreateActivity(userSessionToken, ActivityCreationData(createdAt = now, activityInfo = AuthRegister("iunt", "preAc", "ACTIVE")))

      client.execute(instance, command) must haveActivityResult.await
    }

    "get all activities" in new Context {
      givenAppWithActivitiesBulk(app, activity)

      client.execute(instance, GetActivities()) must haveSameIds(activity).await
    }

    "get all activities with paging" in new Context {
      givenAppWithActivitiesBulk(app, pagingAllActivities: _*)

      val firstPageResult = Await.result(client.execute(instance, GetActivities(pageSize = PageSizes.`25`)), Duration("1 second"))
      firstPageResult must haveSameIds(pagingFirstPage: _*)

      firstPageResult.nextPageCommand match {
        case Some(cmd) => client.execute(instance, cmd) must haveSameIds(paigngSecondPage: _*).await()
        case None => failure("Didn't get the second page")
      }
    }.pendingUntilFixed("PageSize is not implemented in the server")

    "get site's URL" in new Context {
      givenAppWithSite(app, url)

      client.execute(instance, Site) must haveSiteUrl(url).await
    }

    "create 'Notification' message to all users of the application" in new Context {
      val notification = new NotificationCreationData("title", "content", NotificationType.BusinessTips)

      failure("not implemented on server side")
    }.pendingUntilFixed


    "get insights (activity summary) for a contact" in new Context {
      givenAppWithUserActivities(app, contactId, summary)

      client.execute(instance, InsightActivitySummary(Some(contactId))) must haveActivityOfType(typ = summaryAactivityType, total = 1).await
    }

    "get insights (activity summary) for all contacts" in new Context {
      givenAppWithActivities(app, summary)

      client.execute(instance, InsightActivitySummary()) must haveActivityOfType(typ = summaryAactivityType, total = 1).await
    }

    "signal service done" in new Context {
      givenServiceProviderAndCaller(callerApp, providerApp)

      client.execute(instance, ServiceDone(aServiceData(callerApp.appId))) must not(throwA).await
    }
  }

  step(shutdownEnv())
}

