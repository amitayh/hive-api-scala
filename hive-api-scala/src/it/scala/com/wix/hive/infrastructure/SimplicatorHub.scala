package com.wix.hive.infrastructure

import java.net.URLEncoder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.ISO8601Utils
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.activities._
import com.wix.hive.commands.contacts._
import com.wix.hive.commands.insights.InsightActivitySummary
import com.wix.hive.commands.services.{EmailProviders, SendEmail, ServiceDone}
import com.wix.hive.commands.sites.GetSitePages
import com.wix.hive.model.activities.ActivityType.ActivityType
import com.wix.hive.model.activities._
import com.wix.hive.model.contacts.{Contact, ContactName, PagingContactsResult}
import com.wix.hive.model.insights.ActivitySummary
import com.wix.hive.model.sites.{SiteData, SiteStatus}
import org.joda.time.DateTime
import org.skyscreamer.jsonassert.JSONCompareMode

import scala.runtime.BoxedUnit

trait SimplicatorHub extends WiremockEnvironment with HiveApiDrivers {
  private val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule)

  private def versionedUrlMatcher(url: String) = urlMatching(s"/v1$url?.*")

  private def addContainingInBodyToRule(mappingBuilder: MappingBuilder)(s: String): Unit = mappingBuilder.withRequestBody(containing(s))

  private val appIdHeader = "x-wix-application-id"
  private val instanceIdHeader = "x-wix-instance-id"


  // Had to create the "asInHubServer" to add the 'activityType' field in order to be able to generate JSON like in the Hub
  protected case class PagingActivitiesResultAsInHubServer(pageSize: Int, previousCursor: Option[String], nextCursor: Option[String], results: Seq[ActivityAsInHubServer])

  protected case class ActivityAsInHubServer(id: String, createdAt: DateTime, activityLocationUrl: Option[String], activityDetails: Option[ActivityDetails], activityInfo: ActivityInfo) {
    def this(a: Activity) = this(a.id, a.createdAt, a.activityLocationUrl, a.activityDetails, a.activityInfo)

    val activityType = activityInfo.activityType.toString
  }

  private def pagingActivitiesResultConverter(r: PagingActivitiesResult): PagingActivitiesResultAsInHubServer =
    PagingActivitiesResultAsInHubServer(r.pageSize, r.previousCursor, r.nextCursor, r.results map (new ActivityAsInHubServer(_)))


  private implicit class MappingBuilderImplicits(builder: MappingBuilder) {
    val base64Regex = "[A-Za-z0-9+/_-]*"

    def withStandardHeaders(app: AppDef): MappingBuilder = {
      builder.withHeader(appIdHeader, equalTo(app.appId)).
        withHeader(instanceIdHeader, equalTo(app.instanceId)).
        withHeader("x-wix-timestamp", matching(".*")).
        withHeader("x-wix-signature", matching(base64Regex))
    }
  }

  val sendEmailUrl = "/services/actions/email"
  val serviceDoneUrl = "/services/actions/done"

  case class Match(url: String, containInBody: Seq[_] = Nil, method: RequestMethod = RequestMethod.GET, overrideResponse: Option[AnyRef] = None, statusCode: Option[Int] = None)

  def expect[T](app: AppDef, cmd: HiveCommand[T])(respondWith: T = ()): Unit = {
    val matchParams = getMatchParameters(cmd, respondWith)

    val statusCode: Int = matchParams.statusCode.getOrElse(200)
    val response = matchParams.overrideResponse.getOrElse(respondWith)

    val rule = responseForUrl(matchParams.url, app, response.asInstanceOf[AnyRef], method = matchParams.method, statusCode)

    addBodyContainingToRule(rule, matchParams)

    givenThat(rule)
  }


  private def addBodyContainingToRule[T](rule: MappingBuilder, matchParams: Match) {
    matchParams.containInBody.collect {
      case s: String => s
      case Some(s: String) => s
      case d: DateTime => ISO8601Utils.format(d.toDate, true)
    } foreach addContainingInBodyToRule(rule)
  }

  private def getMatchParameters[T](cmd: HiveCommand[T], respondWith: T): Match = {
    cmd match {
      case c: GetContactById => Match(s"/contacts/${c.id}")
      case c: AddAddress => Match(s"/contacts/${c.contactId}/address.*${urlEncode(c.modifiedAt.toString)}", Seq(c.address.address, c.address.city, c.address.country, c.address.neighborhood, c.address.postalCode, c.address.region, c.address.tag), method = RequestMethod.POST)
      case c: AddEmail => Match(s"/contacts/${c.contactId}/email.*${urlEncode(c.modifiedAt.toString)}", Seq(c.email.email, c.email.tag), method = RequestMethod.POST)
      case c: GetContactById => Match(s"/contacts/${c.id}")
      case c: GetContacts => Match("/contacts")
      case c: UpsertContact => Match("/contacts", containInBody = Seq(c.email, c.phone), method = RequestMethod.PUT)
      case c: AddAddress => Match(s"/contacts/${c.contactId}/address.*${urlEncode(c.modifiedAt.toString)}", Seq(c.address.address, c.address.city, c.address.country, c.address.neighborhood, c.address.postalCode, c.address.region, c.address.tag), RequestMethod.POST)
      case c: AddPhone => Match(s"/contacts/${c.contactId}/phone.*${urlEncode(c.modifiedAt.toString)}", Seq(c.phone.phone, c.phone.tag), RequestMethod.POST)
      case c: AddUrl => Match(s"/contacts/${c.contactId}/url.*${urlEncode(c.modifiedAt.toString)}", Seq(c.urlToAdd.tag, c.urlToAdd.url), RequestMethod.POST)
      case c: AddDate => Match(s"/contacts/${c.contactId}/date.*${urlEncode(c.modifiedAt.toString)}", Seq(c.date.tag, c.date.date), RequestMethod.POST)
      case c: UpdateName => Match(s"/contacts/${c.contactId}/name.*${urlEncode(c.modifiedAt.toString)}", Seq(c.name.first, c.name.last, c.name.middle, c.name.prefix, c.name.suffix), RequestMethod.PUT)
      case c: UpdateCompany => Match(s"/contacts/${c.contactId}/company.*${urlEncode(c.modifiedAt.toString)}", Seq(c.company.name, c.company.role), RequestMethod.PUT)
      case c: UpdateAddress => Match(s"/contacts/${c.contactId}/address/${c.addressId}.*${urlEncode(c.modifiedAt.toString)}", Seq(c.address.address, c.address.city, c.address.country, c.address.neighborhood, c.address.postalCode, c.address.region, c.address.tag), RequestMethod.PUT)
      case c: UpdateEmail => Match(s"/contacts/${c.contactId}/email/${c.emailId}.*${urlEncode(c.modifiedAt.toString)}", Seq(c.email.email, c.email.tag), RequestMethod.PUT)
      case c: UpdatePhone => Match(s"/contacts/${c.contactId}/phone/${c.phoneId}.*${urlEncode(c.modifiedAt.toString)}", Seq(c.phone.phone, c.phone.tag), RequestMethod.PUT)
      case c: UpdateUrl => Match(s"/contacts/${c.contactId}/url/${c.urlId}.*${urlEncode(c.modifiedAt.toString)}", Seq(c.urlToUpdate.tag, c.urlToUpdate.url), RequestMethod.PUT)
      case c: UpdateDate => Match(s"/contacts/${c.contactId}/date/${c.dateId}.*${urlEncode(c.modifiedAt.toString)}", Seq(c.date.date, c.date.tag), RequestMethod.PUT)
      case c: GetContactActivities => {val cursorPortion = c.cursor.fold("")(cur => s".*$cur"); Match(s"/contacts/${c.contactId}/activities$cursorPortion", overrideResponse = Some(pagingActivitiesResultConverter(respondWith.asInstanceOf[PagingActivitiesResult])))}
      case c: GetActivities => Match("/activities.*", overrideResponse = Some(pagingActivitiesResultConverter(respondWith.asInstanceOf[PagingActivitiesResult])))
      case c: GetActivityById => Match(s"/activities/${c.id}", overrideResponse = Some(new ActivityAsInHubServer(respondWith.asInstanceOf[Activity])))
      case c: CreateContactActivity => Match(s"/contacts/${c.contactId}/activities", method = RequestMethod.POST)
      case c: GetActivityTypes => Match("/activities/types")
      case c: CreateActivity => Match(s"/activities.*userSessionToken=${c.userSessionToken}", Seq(c.activity.activityLocationUrl, c.activity.activityDetails, c.activity), RequestMethod.POST)
      case c: InsightActivitySummary => {val contactIdPortion = c.contactId.fold("")(con => s"/$con"); Match(s"/insights/contacts$contactIdPortion/activities/summary")}
      case c: SendEmail => Match(sendEmailUrl, method = RequestMethod.POST, statusCode = Some(202))
      case c: ServiceDone => Match(serviceDoneUrl, method = RequestMethod.POST)
      case c: EmailProviders.type => Match("/services/actions/email/providers")
      case c: GetSitePages.type => Match("/sites/site/pages")
    }
  }


  def verifyServiceDone(app: AppDef, done: ServiceDone) = {
    verifyCommand(serviceDoneUrl, app, done)
  }

  def verifySendEmail(app: AppDef, email: SendEmail) = {
    verifyCommand(sendEmailUrl, app, email)
  }

  private def verifyCommand(url: String, app: AppDef, cmd: HiveCommand[_]) = {
    verify(postRequestedFor(versionedUrlMatcher(url))
      .withHeader(appIdHeader, equalTo(app.appId))
      .withHeader(instanceIdHeader, equalTo(app.instanceId))
      .withRequestBody(equalToJson(mapper.writeValueAsString(cmd.body), JSONCompareMode.LENIENT)))
  }


  @deprecated
  val contactCreatedAt = new DateTime(2012, 1, 1, 1, 1)

  @deprecated
  private def aContact(id: String) = Contact(id, createdAt = contactCreatedAt)


  @deprecated
  override def givenAppWithActivitiesBulk(myself: AppDef, activities: Activity*): Unit = {
    val resp = mapper.writeValueAsString(PagingActivitiesResultAsInHubServer(1, None, None, activities.map(new ActivityAsInHubServer(_))))

    givenThat(get(versionedUrlMatcher("/activities.*")).
      withStandardHeaders(myself).
      willReturn(aResponse().withBody(resp)))
  }


  @deprecated()
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

  @deprecated()
  override def givenActivitiesForContact(app: AppDef, contactId: String, cursor: String, activities: Activity*): Unit = {
    val resp = PagingActivitiesResultAsInHubServer(1, None, None, activities.map(new ActivityAsInHubServer(_)))

    givenThat(responseForUrl(s"/contacts/$contactId/activities.*$cursor", app, resp, RequestMethod.GET))
  }

  @deprecated
  override def getValidUserSessionToken: String = "user_tkn"

  @deprecated
  override def givenAppActivityTypes(app: AppDef, types: ActivityType*): Unit = {
    val typesJson = mapper.writeValueAsString(ActivityTypes(types))

    givenThat(get(versionedUrlMatcher("/activities/types")).
      withStandardHeaders(app).
      willReturn(aResponse().withBody(typesJson)))
  }


  @deprecated
  override def givenAppWithContactExist(app: AppDef, contactId: String): Unit = {
    val responseJson = mapper.writeValueAsString(ActivityCreatedResult("activityId", "contactId"))

    val createActivityUsingContactSessionUrl = s"/activities.*userSessionToken=$getValidUserSessionToken"
    val createActivityUsingContactId = s"/contacts/$contactId/activities"

    givenThat(post(versionedUrlMatcher(s"($createActivityUsingContactId|$createActivityUsingContactSessionUrl)"))
      .withStandardHeaders(app).
      willReturn(aResponse().withBody(responseJson)))
  }


  @deprecated
  override def givenAppWithSite(app: AppDef, url: String): Unit = {
    val responseJson = mapper.writeValueAsString(SiteData(url, SiteStatus.published))

    givenThat(get(versionedUrlMatcher("/sites/site"))
      .withStandardHeaders(app)
      .willReturn(aResponse().withBody(responseJson)))
  }


  @deprecated
  override def givenAppWithActivities(app: AppDef, responseWith: ActivitySummary): Unit = {
    givenThat(responseForUrl("/insights/activities/summary", app, responseWith))
  }


  private def responseForUrl(url: String, app: AppDef, response: AnyRef = "", method: RequestMethod = RequestMethod.GET, statusCode: Int = 200) = {
    val body = response match {
      case b: BoxedUnit => ""
      case other => mapper.writeValueAsString(other)
    }

    new MappingBuilder(method, versionedUrlMatcher(url))
      .withStandardHeaders(app)
      .willReturn(aResponse().withBody(body).withStatus(statusCode))
  }


  @deprecated
  override def givenServiceProviderAndCaller(caller: AppDef, provider: AppDef): Unit = {
    givenThat(responseForUrl("/services/actions/done", provider, "", RequestMethod.POST))
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
  def givenContactCreatedById(app: AppDef, contact: ContactData, respondWithContactId: String): Unit = {
    givenThat(responseForUrl("/contacts", app, CreatedContact(respondWithContactId), RequestMethod.POST)
      .withRequestBody(containing(contact.emails.head.email)))
  }

  @deprecated
  def givenAppWithContacts(app: AppDef)(respondWith: PagingContactsResult): Unit = {
    givenThat(responseForUrl("/contacts", app, respondWith))
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

  @deprecated()
  override def givenContactAddAddress(app: AppDef, contactId: String, modifiedAt: DateTime, address: AddressDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/address.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(address.tag)))
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

  @deprecated
  override def givenContactAddUrl(app: AppDef, contactId: String, modifiedAt: DateTime, url: ContactUrlDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/url.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(url.url)))
  }

  @deprecated
  override def givenContactAddDate(app: AppDef, contactId: String, modifiedAt: DateTime, date: ContactDateDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/date.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.POST)
      .withRequestBody(containing(ISO8601Utils.format(date.date.toDate, true))))
  }

  @deprecated
  override def givenContactUpdateDate(app: AppDef, contactId: String, modifiedAt: DateTime, dateId: String, date: ContactDateDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/date/$dateId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(date.tag)))
  }

  @deprecated
  override def givenContactUpdateUrl(app: AppDef, contactId: String, modifiedAt: DateTime, urlId: String, url: ContactUrlDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/url/$urlId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(url.url)))
  }

  @deprecated
  override def givenContactUpdatePhone(app: AppDef, contactId: String, modifiedAt: DateTime, phoneId: String, phone: ContactPhoneDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/phone/$phoneId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(phone.phone)))
  }

  @deprecated
  override def givenContactUpdateEmail(app: AppDef, contactId: String, modifiedAt: DateTime, emailId: String, email: ContactEmailDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/email/$emailId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(email.email)))
  }

  @deprecated
  override def givenContactUpdateAddress(app: AppDef, contactId: String, modifiedAt: DateTime, addressId: String, address: AddressDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/address/$addressId.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(address.tag)))
  }

  @deprecated
  override def givenContactUpdateName(app: AppDef, contactId: String, modifiedAt: DateTime, name: ContactName): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/name.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(name.first.get)))
  }

  @deprecated
  override def givenAppWithUserActivities(app: AppDef, contactId: String, responseWith: ActivitySummary): Unit = {
    givenThat(responseForUrl(s"/insights/contacts/$contactId/activities/summary", app, responseWith))
  }


  @deprecated
  override def givenContactUpdateCompany(app: AppDef, contactId: String, modifiedAt: DateTime, company: CompanyDTO): Unit = {
    givenThat(responseForUrl(s"/contacts/$contactId/company.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(company.role.get)))
  }

  @deprecated
  override def givenContactUpdatePicture(app: AppDef, contactId: String, modifiedAt: DateTime, picture: PictureDTO): Unit = {
    throw new RuntimeException("Until HUB is fixed")
    givenThat(responseForUrl(s"/contacts/$contactId/picture.*${urlEncode(modifiedAt.toString)}", app, aContact(contactId), RequestMethod.PUT)
      .withRequestBody(containing(picture.picture)))
  }

}
