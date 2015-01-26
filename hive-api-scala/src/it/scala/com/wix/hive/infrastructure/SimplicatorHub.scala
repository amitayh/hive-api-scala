package com.wix.hive.infrastructure

import java.net.URLEncoder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.ISO8601Utils
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.tomakehurst.wiremock.client.{ValueMatchingStrategy, MappingBuilder}
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.contacts._
import com.wix.hive.commands.services.{ServiceDone, Providers, SendEmail}
import com.wix.hive.commands.sites.SitePages
import com.wix.hive.model.activities.ActivityType.ActivityType
import com.wix.hive.model.activities._
import com.wix.hive.model.contacts.{PagingContactsResult, Contact, ContactName}
import com.wix.hive.model.insights.ActivitySummary
import com.wix.hive.model.sites.{SiteData, SiteStatus}
import org.joda.time.DateTime
import org.skyscreamer.jsonassert.JSONCompareMode

trait SimplicatorHub extends WiremockEnvironment with HiveApiDrivers {
  val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule)

  private def versionedUrlMatcher(url: String) = urlMatching(s"/v1$url?.*")

  private val appIdHeader = "x-wix-application-id"
  private val instanceIdHeader = "x-wix-instance-id"


  implicit class MappingBuilderImplicits(builder: MappingBuilder) {
    val base64Regex = "[A-Za-z0-9+/_-]*"

    def withStandardHeaders(app: AppDef): MappingBuilder = {
      builder.withHeader(appIdHeader, equalTo(app.appId)).
        withHeader(instanceIdHeader, equalTo(app.instanceId)).
        withHeader("x-wix-timestamp", matching(".*")).
        withHeader("x-wix-signature", matching(base64Regex))
    }
  }

  val contactCreatedAt = new DateTime(2012, 1, 1, 1, 1)

  private def aContact(id: String) = Contact(id, createdAt = contactCreatedAt)

  def expectGetContactById(app: AppDef)(respondsWith: Contact): Unit = {
    givenThat(responseForUrl(s"/contacts/${respondsWith.id}", app, respondsWith))
  }

  @deprecated
  override def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit = {
    val contactJson = mapper.writeValueAsString(respondsWith)

    givenThat(get(versionedUrlMatcher(s"/contacts/${respondsWith.id}")).
      withStandardHeaders(myself).
      willReturn(aResponse().withBody(contactJson)))
  }

  def givenAppWithContacts(app: AppDef, respondsWith: Contact*): Unit = ???

  @deprecated
  def givenAppWithContacts(app: AppDef)(respondWith: PagingContactsResult): Unit = {
    givenThat(responseForUrl("/contacts", app, respondWith))
  }

  def expectGetContacts(app: AppDef)(respondWith: PagingContactsResult): Unit = {
    givenThat(responseForUrl("/contacts", app, respondWith))
  }


  def givenContactCreatedById(app: AppDef, contact: ContactData, respondWithContactId: String): Unit = {
    givenThat(responseForUrl("/contacts", app, CreatedContact(respondWithContactId), RequestMethod.POST)
      .withRequestBody(containing(contact.emails.head.email)))
  }

  def expectUpsertContact(app: AppDef, command: UpsertContact)(respondWith: UpsertContactResponse): Unit = {
    val rule = responseForUrl("/contacts", app, respondWith, RequestMethod.PUT)

    Seq(command.email, command.phone)
      .collect { case Some(t) => rule.withRequestBody(containing(t))}

    givenThat(rule)
  }

  @deprecated
  override def givenContactUpsertByPhoneAndEmail(app: AppDef, phone: Option[String], email: Option[String], contactId: String): Unit = {
    givenThat(responseForUrl("/contacts", app, UpsertContactResponse(contactId), RequestMethod.PUT).
      withRequestBody(containing(phone.get))
      .withRequestBody(containing(email.get)))
  }

  private def urlEncode(str: String): String = {
    URLEncoder.encode(str, "UTF-8")
  }

  @deprecated
  override def givenContactAddAddress(app: AppDef, contactId: String, modifiedAt: DateTime, address: AddressDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/address.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(address.tag)))
  }


  def expectAddAddress(app: AppDef, cmd: AddAddress)(respondWith: Contact) = {
    import cmd._
    val expected = responseForUrl(s"/contacts/$contactId/address.*${urlEncode(modifiedAt.toString)}", app, respondWith, RequestMethod.POST)

    Seq(address.address, address.city, address.country, address.neighborhood, address.postalCode, address.region, address.tag)
      .collect { case Some(f) => expected.withRequestBody(containing(f.toString))}

    givenThat(expected)
  }

  def expectAddEmail(app: AppDef, cmd: AddEmail)(respondWith: Contact): Unit = {
    import cmd._

    val rule = responseForUrl(s"/contacts/$contactId/email.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)

    Seq(email.email, email.tag)
      .collect { case value: String => rule.withRequestBody(containing(value))}

    givenThat(rule)
  }

  @deprecated
  override def givenContactAddEmail(app: AppDef, contactId: String, modifiedAt: DateTime, email: ContactEmailDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/email.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(email.email)))
  }

  @deprecated
  override def givenContactAddPhone(app: AppDef, contactId: String, modifiedAt: DateTime, phone: ContactPhoneDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/phone.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(phone.phone)))
  }

  def expectAddPhone(app: AppDef, cmd: AddPhone)(respondWith: Contact): Unit = {
    import cmd._
    val rule = responseForUrl(s"/contacts/$contactId/phone.*${urlEncode(modifiedAt.toString)}", app, respondWith, RequestMethod.POST)

    Seq(phone.phone, phone.tag) foreach { v => rule.withRequestBody(containing(v))}

    givenThat(rule)
  }


  @deprecated
  override def givenContactAddUrl(app: AppDef, contactId: String, modifiedAt: DateTime, url: ContactUrlDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/url.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(url.url)))
  }

  def expectedAddUrl(app: AppDef, cmd: AddUrl)(respondWith: Contact): Unit = {
    import cmd._
    val rule = responseForUrl(s"/contacts/$contactId/url.*${urlEncode(modifiedAt.toString)}", app, respondWith, RequestMethod.POST)

    Seq(urlToAdd.tag, urlToAdd.url) foreach { s => rule.withRequestBody(containing(s))}

    givenThat(rule)
  }

  @deprecated
  override def givenContactAddDate(app: AppDef, contactId: String, modifiedAt: DateTime, date: ContactDateDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/date.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(ISO8601Utils.format(date.date.toDate, true))))
  }

  def expectAddDate(app: AppDef, cmd: AddDate)(respondWith: Contact): Unit = {
    import cmd._

    val rule = responseForUrl(s"/contacts/$contactId/date.*${urlEncode(modifiedAt.toString)}", app, respondWith, RequestMethod.POST)

    Seq(cmd.date.tag, ISO8601Utils.format(date.date.toDate, true)) foreach { s => rule.withRequestBody(containing(s))}

    givenThat(rule)
  }


  @deprecated
  override def givenContactUpdateName(app: AppDef, contactId: String, modifiedAt: DateTime, name: ContactName): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/name.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(name.first.get)))
  }

  def expectUpdateName(app: AppDef, cmd: UpdateName)(respondWith: Contact): Unit = {
    import cmd._

    val rule = responseForUrl(s"/contacts/$contactId/name.*${urlEncode(modifiedAt.toString)}", app, respondWith, RequestMethod.PUT)

    Seq(name.first, name.last, name.middle, name.prefix, name.suffix)
      .collect { case Some(s) => rule.withRequestBody(containing(s))}

    givenThat(rule)
  }

  @deprecated
  override def givenContactUpdateCompany(app: AppDef, contactId: String, modifiedAt: DateTime, company: CompanyDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/company.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(company.role.get)))
  }

  def expectUpdateCompany(app: AppDef, cmd: UpdateCompany)(respondWith: Contact): Unit = {
    import cmd._

    val rule = responseForUrl(s"/contacts/$contactId/company.*${urlEncode(modifiedAt.toString)}", app, respondWith, RequestMethod.PUT)

    Seq(company.name, company.role) collect { case Some(s) => rule.withRequestBody(containing(s))}

    givenThat(rule)
  }

  override def givenContactUpdatePicture(app: AppDef, contactId: String, modifiedAt: DateTime, picture: PictureDTO): Unit = {
    throw new RuntimeException("Until HUB is fixed")
    givenThat(responseForUrl(s"/contacts/$contactId/picture.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(picture.picture)))
  }

  @deprecated
  override def givenContactUpdateAddress(app: AppDef, contactId: String, modifiedAt: DateTime, addressId: String, address: AddressDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/address/$addressId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(address.tag)))
  }

  def expectUpdateAddress(app: AppDef, cmd: UpdateAddress)(respondWith: Contact): Unit = {
    import cmd._

    val rule = responseForUrl(s"/contacts/$contactId/address/$addressId.*${urlEncode(modifiedAt.toString)}", app, respondWith, RequestMethod.PUT)
    Seq(address.address, address.city, address.country, address.neighborhood, address.postalCode, address.region, address.tag)
      .collect { case Some(s) => rule.withRequestBody(containing(s.toString))}

    givenThat(rule)
  }

  override def givenContactUpdateEmail(app: AppDef, contactId: String, modifiedAt: DateTime, emailId: String, email: ContactEmailDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/email/$emailId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(email.email)))
  }

  override def givenContactUpdatePhone(app: AppDef, contactId: String, modifiedAt: DateTime, phoneId: String, phone: ContactPhoneDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/phone/$phoneId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(phone.phone)))
  }

  override def givenContactUpdateUrl(app: AppDef, contactId: String, modifiedAt: DateTime, urlId: String, url: ContactUrlDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/url/$urlId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(url.url)))
  }

  override def givenContactUpdateDate(app: AppDef, contactId: String, modifiedAt: DateTime, dateId: String, date: ContactDateDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/date/$dateId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(date.tag)))
  }

  override def givenActivitiesForContact(app: AppDef, contactId: String, cursor: String, activities: Activity*): Unit = {
    val resp = PagingActivitiesResultAsInHubServer(1, None, None, activities.map(new ActivityAsInHubServer(_)))

    givenThat(responseForUrl(s"/contacts/$contactId/activities.*$cursor", app, resp, RequestMethod.GET))
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

    val createActivityUsingContactSessionUrl = s"/activities.*userSessionToken=$getValidUserSessionToken"
    val createActivityUsingContactId = s"/contacts/$contactId/activities"

    givenThat(post(versionedUrlMatcher(s"($createActivityUsingContactId|$createActivityUsingContactSessionUrl)"))
      .withStandardHeaders(app).
      willReturn(aResponse().withBody(responseJson)))
  }

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

  private def responseForUrl(url: String, app: AppDef, response: AnyRef = "", method: RequestMethod = RequestMethod.GET, statusCode: Int = 200) = {
    new MappingBuilder(method, versionedUrlMatcher(url))
      .withStandardHeaders(app)
      .willReturn(aResponse().withBody(mapper.writeValueAsString(response)).withStatus(statusCode))
  }

  case class ActivityAsInHubServer(id: String, createdAt: DateTime, activityLocationUrl: Option[String], activityDetails: Option[ActivityDetails], activityInfo: ActivityInfo) {
    def this(a: Activity) = this(a.id, a.createdAt, a.activityLocationUrl, a.activityDetails, a.activityInfo)

    val activityType = activityInfo.activityType.toString
  }

  case class PagingActivitiesResultAsInHubServer(pageSize: Int, previousCursor: Option[String], nextCursor: Option[String], results: Seq[ActivityAsInHubServer])

  override def givenServiceProviderAndCaller(caller: AppDef, provider: AppDef): Unit = {
    givenThat(responseForUrl("/services/actions/done", provider, "", RequestMethod.POST))
  }

  val sendEmailUrl = "/services/actions/email"

  def expectSendEmail(app: AppDef): Unit = {
    givenThat(responseForUrl(sendEmailUrl, app, method = RequestMethod.POST, statusCode = 202))
  }

  def verifySendEmail(app: AppDef, email: SendEmail) = {
    verifyCommand(sendEmailUrl, app, email)
  }

  val serviceDoneUrl = "/services/actions/done"

  def expectServiceDone(app: AppDef) = {
    givenThat(responseForUrl(serviceDoneUrl, app, method = RequestMethod.POST))
  }

  def verifyServiceDone(app: AppDef, done: ServiceDone) = {
    verifyCommand(serviceDoneUrl, app, done)
  }

  private def verifyCommand(url: String, app: AppDef, cmd: HiveCommand[_]) = {
    verify(postRequestedFor(versionedUrlMatcher(url))
      .withHeader(appIdHeader, equalTo(app.appId))
      .withHeader(instanceIdHeader, equalTo(app.instanceId))
      .withRequestBody(equalToJson(mapper.writeValueAsString(cmd.body), JSONCompareMode.LENIENT)))
  }


  def expectEmailProviders(app: AppDef)(respondWith: Providers): Unit = {
    givenThat(responseForUrl("/services/actions/email/providers", app, respondWith))
  }

  def expectSiteWithPages(app: AppDef)(respondWith: SitePages): Unit = {
    givenThat(responseForUrl("/sites/site/pages", app, respondWith))
  }
}
