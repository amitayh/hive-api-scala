package com.wix.hive.infrastructure

import java.net.URLEncoder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.ISO8601Utils
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, containing, equalTo, equalToJson, givenThat, matching, postRequestedFor, urlMatching, verify => wiremockVerify}
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.activities._
import com.wix.hive.commands.contacts._
import com.wix.hive.commands.insights.InsightActivitySummary
import com.wix.hive.commands.labels.{GetLabels, GetLabelById}
import com.wix.hive.commands.services.{EmailProviders, SendEmail, ServiceDone}
import com.wix.hive.commands.sites.{Site, GetSitePages}
import org.joda.time.DateTime
import org.skyscreamer.jsonassert.JSONCompareMode

import scala.runtime.BoxedUnit

trait SimplicatorHive {
  private val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule)
  private val appIdHeader = "x-wix-application-id"
  private val instanceIdHeader = "x-wix-instance-id"

  private lazy val responseConverter = new DeserializableByHiveClientAdapter

  case class Match(url: String, containInBody: Seq[_] = Nil, method: RequestMethod = RequestMethod.GET, statusCode: Option[Int] = None)

  def expect[T](app: AppDef, cmd: HiveCommand[T])(respondWith: T = ()): Unit = {
    val matchParams = getMatchParameters(cmd)

    val statusCode: Int = matchParams.statusCode.getOrElse(200)
    val response = responseConverter.convertToDeserializableByHiveClient(respondWith)

    val rule = responseForUrl(matchParams.url, app, response, method = matchParams.method, statusCode)

    addBodyContainingToRule(rule, matchParams)

    givenThat(rule)
  }


  def verify[T](app: AppDef, cmd: HiveCommand[T], times: Int = 1): Unit = {
    val url = getMatchParameters(cmd).url

    wiremockVerify(times, postRequestedFor(versionedUrlMatcher(url))
      .withHeader(appIdHeader, equalTo(app.appId))
      .withHeader(instanceIdHeader, equalTo(app.instanceId))
      .withRequestBody(equalToJson(mapper.writeValueAsString(cmd.body), JSONCompareMode.LENIENT)))
  }

  private def addBodyContainingToRule[T](rule: MappingBuilder, matchParams: Match) {
    matchParams.containInBody.collect {
      case s: String => s
      case Some(s: String) => s
      case d: DateTime => ISO8601Utils.format(d.toDate, true)
    } foreach { s => rule.withRequestBody(containing(s))}
  }


  private def getMatchParameters[T](cmd: HiveCommand[T]): Match = {
    cmd match {
      case c: CreateContact => Match("/contacts", method = RequestMethod.POST)
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
      case c: GetContactActivities => val cursorPortion = c.cursor.fold("")(cur => s".*$cur"); Match(s"/contacts/${c.contactId}/activities$cursorPortion")
      case c: GetActivities => Match("/activities.*")
      case c: GetActivityById => Match(s"/activities/${c.id}")
      case c: CreateContactActivity => Match(s"/contacts/${c.contactId}/activities", method = RequestMethod.POST)
      case c: GetActivityTypes => Match("/activities/types")
      case c: CreateActivity => Match(s"/activities.*userSessionToken=${c.userSessionToken}", Seq(c.activity.activityLocationUrl, c.activity.activityDetails, c.activity), RequestMethod.POST)
      case c: InsightActivitySummary => val contactIdPortion = c.contactId.fold("")(con => s"/$con"); Match(s"/insights/contacts$contactIdPortion/activities/summary")
      case c: SendEmail => Match("/services/actions/email", method = RequestMethod.POST, statusCode = Some(202))
      case c: ServiceDone => Match("/services/actions/done", method = RequestMethod.POST)
      case c: EmailProviders.type => Match("/services/actions/email/providers")
      case c: GetSitePages.type => Match("/sites/site/pages")
      case c: Site.type => Match("/sites/site")
      case c: GetLabelById => Match(s"/labels/${c.id}")
      case c: GetLabels => Match("/labels")
    }
  }

  private def versionedUrlMatcher(url: String) = urlMatching(s"/v1$url?.*")

  private implicit class MappingBuilderImplicits(builder: MappingBuilder) {
    val base64Regex = "[A-Za-z0-9+/_-]*"

    def withStandardHeaders(app: AppDef): MappingBuilder = {
      builder.withHeader(appIdHeader, equalTo(app.appId)).
        withHeader(instanceIdHeader, equalTo(app.instanceId)).
        withHeader("x-wix-timestamp", matching(".*")).
        withHeader("x-wix-signature", matching(base64Regex))
    }
  }

  private def responseForUrl(url: String, app: AppDef, response: Any = "", method: RequestMethod = RequestMethod.GET, statusCode: Int = 200) = {
    val body = response match {
      case b: BoxedUnit => ""
      case other => mapper.writeValueAsString(other)
    }

    new MappingBuilder(method, versionedUrlMatcher(url))
      .withStandardHeaders(app)
      .willReturn(aResponse().withBody(body).withStatus(statusCode))
  }

  private def urlEncode(str: String): String = URLEncoder.encode(str, "UTF-8")

  @deprecated("Use `verify`")
  def verifyServiceDone(app: AppDef, done: ServiceDone) = {
    this.verify(app, done)
  }

  @deprecated("Use `verify`")
  def verifySendEmail(app: AppDef, email: SendEmail) = {
    this.verify(app, email)
  }
}