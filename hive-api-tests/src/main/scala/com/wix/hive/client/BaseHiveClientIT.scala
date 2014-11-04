package com.wix.hive.client

import java.util.UUID

import com.wix.hive.commands.activities._
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.commands.contacts._
import com.wix.hive.commands.insights.InsightActivitySummary
import com.wix.hive.commands.sites.Site
import com.wix.hive.model.activities.ActivityType.ActivityType
import com.wix.hive.model.activities._
import com.wix.hive.model.contacts.{Contact, ContactName, EmailStatus, PagingContactsResult}
import com.wix.hive.model.insights.{ActivitySummary, ActivityTypesSummary}
import com.wix.hive.model.notifications.{NotificationCreationData, NotificationType}
import com.wix.hive.model.sites.SiteData
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

    val app = AppDef.random
    val instance = app.instanceId

    val client = new HiveClient(app.appId, app.secret, baseUrl = baseUrl)

    def now = new DateTime()

    val contactId = "e5d81850-5dd8-407f-9acc-7ffd6c924ecf"
    val contact = Contact(contactId, new DateTime(2010,1,1,0,0))
    val anotherContactId = "c34a8709-6b14-4959-9db7-33a584daefad"
    val anotherContact = Contact(anotherContactId, new DateTime(2010,1,1,0,0))

    val contactName = ContactName(first = Some("First"), last = Some("Last"))
    val contactEmail = ContactEmailDTO(email = "maximn@wix.com", tag = "emailtag", emailStatus= EmailStatus.OptOut)
    val contactData = ContactData(name = Some(contactName), emails = Seq(contactEmail))

    val address = AddressDTO("tag-address-dto")

    val phone = "972-54-5556767"
    val email = "maximn@wix.com"
    val emailStatus = EmailStatus.OptOut
    
    val modifiedAt = new DateTime(2012, 2, 10, 10,10)

    val activityId = randomId

    val activity = Activity(id = "id", createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE"))
    val pagingFirstPage = (0 to 25).map((id: Int) => activity.copy(id = id.toString))
    val paigngSecondPage = (25 to 40).map((id: Int) => activity.copy(id = id.toString))
    val pagingAllActivities = pagingFirstPage ++ paigngSecondPage

    val summaryAactivityType = ActivityType.`auth/login`
    val summaryFrom = new DateTime(2010, 1, 1, 10, 10)
    val summary = ActivitySummary(Seq(ActivityTypesSummary(Some(summaryAactivityType), 1, summaryFrom)), 1, summaryFrom)

    implicit def value2BeMatcher[T](t: T): Matcher[T] = be_===(t)

    def beContactWithId(matcher: Matcher[String]): Matcher[Contact] = matcher ^^ { (_:Contact).id aka "contactId" }
    def beContactsWith(matcher: Matcher[Seq[Contact]]): Matcher[PagingContactsResult] = matcher ^^ { (_:PagingContactsResult).results aka "results"}

    def beCreatedContactWithId(matcher: Matcher[String]): Matcher[CreatedContact] = matcher ^^ { (_: CreatedContact).contactId aka "contactId" }

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

    def haveUpsertContactId(id: String): Matcher[UpsertContactResponse] =  be_===(id) ^^ { (_:UpsertContactResponse).contactId aka "contactId" }
  }

  "Hive client" should {

    "get contact by ID" in new Context {
      givenContactFetchById(app, contact)

      client.execute(instance, GetContactById(contactId)) must beContactWithId(contactId).await
    }

    "get contacts" in new Context {
      givenAppWithContacts(app, Seq(contact, anotherContact) :_*)

      client.execute(instance, GetContacts()) must beContactsWith(contain(allOf(beContactWithId(contactId), beContactWithId(anotherContactId)))).await
    }.pendingUntilFixed("""The server doesn't comply with the protocol, returns an array of Contacts instead of aPagingContactsResult""")

    "create a contact" in new Context {
      givenContactCreatedById(app, contactData ,contactId)

     client.execute(instance, CreateContact(name = Some(contactName) , emails = Seq(contactEmail))) must beCreatedContactWithId(contactId).await
    }

    "upsert a contact" in new Context {
      givenContactUpsertByPhoneAndEmail(app, Some(phone), Some(email), contactId)

      client.execute(instance, UpsertContact(Some(phone), Some(email))) must haveUpsertContactId(contactId).await

      verifyUpsertContactWithId(app, Some(phone), Some(email), contactId)
    }

    "add address to contact" in new Context {
      givenContactAddAddress(app, contactId, modifiedAt,address)

      client.execute(instance, AddAddress(contactId, modifiedAt, address)) must beContactWithId(contactId).await
    }
    
    "add email to contact" in new Context {
      givenEmailAddAddress(app, contactId, modifiedAt, contactEmail)
      
      client.execute(instance, AddEmail(contactId, modifiedAt, contactEmail)) must beContactWithId(contactId).await
    }

    "get activity by ID" in new Context {
      givenAppWithActivitiesById(app, Activity(id = activityId, createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE")))

      client.execute(app.instanceId, GetActivityById(activityId)) must beAnActivityWith(id = activityId).await
    }

    "get list of all activity types" in new Context {
      val types = Seq(ActivityType.`auth/login`, ActivityType.`music/album-fan`)
      givenAppActivityTypes(app, types: _*)

      client.execute(app.instanceId, GetActivityTypes()) must haveTypes(types).await
    }

    "create activity for contact" in new Context {
      givenAppWithContactExist(app, randomId)
      val userSessionToken = getValidUserSessionToken

      val command = CreateActivity(userSessionToken, ActivityCreationData(createdAt = now, activityInfo = AuthRegister("iunt", "preAc", "ACTIVE")))

      client.execute(instance, command) must haveActivityResult.await

      verifyActivityCreated(app)
    }

    "get all activities" in new Context {
      val allActivities = Activity(id = activityId, createdAt = now, activityInfo = AuthRegister("ini", "stream", "ACTIVE"))

      givenAppWithActivitiesBulk(app, allActivities)

      client.execute(instance, GetActivities()) must haveSameIds(allActivities).await
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
      val url = "http://somesite.com/wix"
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
  }

  step(shutdownEnv())
}

trait HiveApiDrivers {

  def randomId: String = UUID.randomUUID().toString

  def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit

  def givenAppWithContacts(app: AppDef, respondsWith: Contact*): Unit

  def givenContactCreatedById(app: AppDef, contact: ContactData, respondWithContactId: String): Unit

  def givenContactUpsertByPhoneAndEmail(app: AppDef, phone: Option[String], email: Option[String], contactId: String)
  def verifyUpsertContactWithId(app: AppDef, phone: Option[String], email: Option[String], contactId: String): Unit

  def givenContactAddAddress(app: AppDef, contactId: String, modifiedAt: DateTime, address: AddressDTO): Unit

  def givenEmailAddAddress(app: AppDef, contactId: String, modifiedAt: DateTime, email: ContactEmailDTO): Unit


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