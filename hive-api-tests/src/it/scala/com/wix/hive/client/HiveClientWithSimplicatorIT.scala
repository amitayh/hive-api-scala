package com.wix.hive.client

import java.net.URLEncoder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.{MappingBuilder, WireMock}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.wix.hive.commands.contacts._
import com.wix.hive.model.activities.ActivityType.ActivityType
import com.wix.hive.model.activities._
import com.wix.hive.model.contacts.{ContactUrl, Contact}
import com.wix.hive.model.insights.ActivitySummary
import com.wix.hive.model.sites.{SiteData, SiteStatus}
import dispatch.url
import org.apache.log4j.BasicConfigurator
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: daniels
 * Date: 10/20/14
 */
class HiveClientWithSimplicatorIT extends BaseHiveClientIT with HubSimplicator {
  val serverPort = 8089

  override val baseUrl = s"http://localhost:$serverPort"
  val wireMockServer = new WireMockServer(new WireMockConfiguration().port(serverPort))

  override def initEnv(): Unit = {
    BasicConfigurator.configure()
    WireMock.configureFor("localhost", serverPort)
    wireMockServer.start()
  }

  override def shutdownEnv() = wireMockServer.shutdown()

  override def beforeTest(): Unit = {
    WireMock.reset()
    WireMock.resetAllScenarios()
  }
}

trait HubSimplicator extends HiveApiDrivers {
  val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule)

  def versionedUrlMatcher(url: String) = urlMatching(s"/v1$url?.*")

  implicit class MappingBuilderImplicits(builder: MappingBuilder) {
    val base64Regex = "[A-Za-z0-9+/_-]*"

    def withStandardHeaders(app: AppDef): MappingBuilder =
      builder.withHeader("x-wix-application-id", equalTo(app.appId)).
        withHeader("x-wix-instance-id", equalTo(app.instanceId)).
        withHeader("x-wix-timestamp", matching(".*")).
        withHeader("x-wix-signature", matching(base64Regex))
  }

  val contactCreatedAt = new DateTime(2012,1,1,1,1)
  private def aContact(id: String) = Contact(id, createdAt = contactCreatedAt)

  override def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit = {
    val contactJson = mapper.writeValueAsString(respondsWith)

    givenThat(get(versionedUrlMatcher(s"/contacts/${respondsWith.id}")).
      withStandardHeaders(myself).
      willReturn(aResponse().withBody(contactJson)))
  }

  def givenAppWithContacts(app: AppDef, respondsWith: Contact*): Unit = {
    givenThat(responseForUrl("/contacts", app, respondsWith))
  }

  def givenContactCreatedById(app: AppDef, contact: ContactData, respondWithContactId: String): Unit = {
    givenThat(responseForUrl("/contacts", app, CreatedContact(respondWithContactId), RequestMethod.POST)
      .withRequestBody(containing(contact.emails.head.email)))
  }

  case class ActivityAsInHubServer(id: String, createdAt: DateTime, activityLocationUrl: Option[String], activityDetails: Option[ActivityDetails], activityInfo: ActivityInfo) {
    def this(a: Activity) = this(a.id, a.createdAt, a.activityLocationUrl, a.activityDetails, a.activityInfo)

    val activityType = activityInfo.activityType.toString
  }

  override def givenContactUpsertByPhoneAndEmail(app: AppDef, phone: Option[String], email: Option[String], contactId: String): Unit = {
    givenThat(responseForUrl("/contacts", app, UpsertContactResponse(contactId), RequestMethod.PUT).
      withRequestBody(containing(phone.get))
      .withRequestBody(containing(email.get)))
  }

  override def verifyUpsertContactWithId(app: AppDef, phone: Option[String], email: Option[String], contactId: String): Unit = ()

  private def urlEncode(str: String): String = {
    URLEncoder.encode(str, "UTF-8")
  }

  override def givenContactAddAddress(app: AppDef, contactId: String, modifiedAt: DateTime, address: AddressDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/${contactId}/address.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(address.tag)))
  }

  override def givenContactAddEmail(app: AppDef, contactId: String, modifiedAt: DateTime, email: ContactEmailDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/email.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
    .withRequestBody(containing(email.email)))
  }

  override def givenContactAddPhone(app: AppDef, contactId: String, modifiedAt: DateTime, phone: ContactPhoneDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/phone.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(phone.phone)))
  }

  override def givenContactAddUrl(app: AppDef, contactId: String, modifiedAt: DateTime, url: ContactUrlDTO): Unit ={
    givenThat(responseForUrl(s"/contacts/$contactId/url.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(url.url)))
  }

  override def givenContactAddDate(app: AppDef, contactId: String, modifiedAt: DateTime, date: ContactDateDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/date.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(date.date.toString)))
  }


  override def givenAppWithActivitiesById(myself: AppDef, activities: Activity*): Unit = {
    activities foreach {
      case activity: Activity =>
        import activity._
        val activityJson = mapper.writeValueAsString(new ActivityAsInHubServer(id, createdAt, activityLocationUrl, activityDetails, activityInfo))

        givenThat(get(versionedUrlMatcher(s"/activities/${activity.id}")).
          withStandardHeaders(myself).
          willReturn(aResponse().withBody(activityJson)))
    }
  }

  override def givenAppWithActivitiesBulk(myself: AppDef, activities: Activity*): Unit = {
    case class PagingActivitiesResultAsInHubServer(pageSize: Int, previousCursor: Option[String], nextCursor: Option[String], results: Seq[ActivityAsInHubServer])

    val resp = mapper.writeValueAsString(PagingActivitiesResultAsInHubServer(1, None, None, activities.map(new ActivityAsInHubServer(_))))

    givenThat(get(versionedUrlMatcher("/activities.*")).
      withStandardHeaders(myself).
      willReturn(aResponse().withBody(resp)))
  }


  override def getValidUserSessionToken: String = "user_tkn"

  override def givenAppActivityTypes(app: AppDef, types: ActivityType*): Unit = {
    val typesJson = mapper.writeValueAsString(ActivityTypes(types))

    givenThat(get(versionedUrlMatcher("/activities/types")).
      withStandardHeaders(app).
      willReturn(aResponse().withBody(typesJson)))
  }

  override def givenAppWithContactExist(app: AppDef, contactId: String): Unit = {
    val responseJson = mapper.writeValueAsString(ActivityCreatedResult("activityId", "contactId"))

    givenThat(post(versionedUrlMatcher(s"/activities.*userSessionToken=$getValidUserSessionToken"))
      .withStandardHeaders(app).
      willReturn(aResponse().withBody(responseJson)))
  }

  override def verifyActivityCreated(appDef: AppDef): Unit = ()

  override def givenAppWithSite(app: AppDef, url: String): Unit = {
    val responseJson = mapper.writeValueAsString(SiteData(url, SiteStatus.published))

    givenThat(get(versionedUrlMatcher("/sites/site"))
      .withStandardHeaders(app)
      .willReturn(aResponse().withBody(responseJson)))
  }

  override def givenAppWithUserActivities(app: AppDef, contactId: String, responseWith: ActivitySummary): Unit = {
    givenThat(responseForUrl(s"/insights/contacts/$contactId/activities/summary", app, responseWith))
  }


  override def givenAppWithActivities(app: AppDef, responseWith: ActivitySummary): Unit = {
    givenThat(responseForUrl("/insights/activities/summary", app, responseWith))
  }

  private def responseForUrl(url: String, app: AppDef, response: AnyRef, method: RequestMethod = RequestMethod.GET) = {
    new MappingBuilder(method, versionedUrlMatcher(url))
      .withStandardHeaders(app)
      .willReturn(aResponse().withBody(mapper.writeValueAsString(response)))
  }
}