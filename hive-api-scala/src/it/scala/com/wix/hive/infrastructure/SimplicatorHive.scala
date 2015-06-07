package com.wix.hive.infrastructure

import java.net.URLEncoder

import com.fasterxml.jackson.databind.util.ISO8601Utils
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, containing, equalTo, equalToJson, givenThat, matching, urlMatching, verify => wiremockVerify}
import com.github.tomakehurst.wiremock.client.{MappingBuilder, RequestPatternBuilder, VerificationException}
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.activities._
import com.wix.hive.commands.batch.ProcessBatch
import com.wix.hive.commands.contacts._
import com.wix.hive.commands.insights.InsightActivitySummary
import com.wix.hive.commands.labels.{GetLabelById, GetLabels}
import com.wix.hive.commands.redirects.GetRedirects
import com.wix.hive.commands.services.email.SendSingle
import com.wix.hive.commands.services.{EmailProviders, SendEmail, ServiceDone}
import com.wix.hive.commands.sites.{GetSitePages, Site}
import com.wix.hive.json.JacksonObjectMapper.mapper
import com.wix.hive.model.WixAPIErrorException
import org.joda.time.DateTime
import org.skyscreamer.jsonassert.JSONCompareMode

import scala.runtime.BoxedUnit

trait SimplicatorHive {
  private val appIdHeader = "x-wix-application-id"
  private val instanceIdHeader = "x-wix-instance-id"

  private lazy val responseConverter = new DeserializableByHiveClientAdapter

  case class Match(url: String, containInBody: Seq[_] = Nil, method: RequestMethod = RequestMethod.GET, statusCode: Option[Int] = None)

  def expectError(app: AppDef, cmd: HiveCommand[_])(respondWith: WixAPIErrorException): Unit = {
    nonTypedExpect(app, cmd)(respondWith)
  }

  def expectCustom(app: AppDef, cmd: HiveCommand[_])(respondWith: String): Unit = {
    nonTypedExpect(app, cmd)(respondWith)
  }

  def expect[T](app: AppDef, cmd: HiveCommand[T])(respondWith: T = ()): Unit = {
    nonTypedExpect(app, cmd)(respondWith)
  }

  @throws[VerificationException]
  def verify[T](app: AppDef, cmd: HiveCommand[T], times: Int = 1): Unit = {
    val params = getMatchParameters(cmd)
    val url = params.url
    val method = params.method

    val patternBuilder = new RequestPatternBuilder(method, versionedUrlMatcher(url))
      .withHeader(appIdHeader, equalTo(app.appId))
      .withHeader(instanceIdHeader, equalTo(app.instanceId))

    if (method == RequestMethod.POST || method == RequestMethod.PUT)
      patternBuilder.withRequestBody(equalToJson(mapper.writeValueAsString(cmd.body), JSONCompareMode.LENIENT))

    wiremockVerify(times, patternBuilder)
  }

  private def nonTypedExpect(app: AppDef, cmd: HiveCommand[_])(respondWith: Any = ()): Unit = {
    val matchParams = getMatchParameters(cmd)

    val response = responseConverter.convertToDeserializableByHiveClient(cmd)(respondWith)
    val statusCode: Int = respondWith match {
      case e: WixAPIErrorException => e.errorCode
      case _ => matchParams.statusCode.getOrElse(200)
    }

    val rule = responseForUrl(matchParams.url, app, response, method = matchParams.method, statusCode)

    addBodyContainingToRule(rule, matchParams)

    givenThat(rule)
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
      case c: AddEmail => Match(s"/contacts/${c.contactId}/email.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.email.email, c.email.tag), method = RequestMethod.POST)
      case c: GetContactById => Match(s"/contacts/${c.id}")
      case c: GetContacts => Match("/contacts\\?")
      case c: UpsertContact => Match("/contacts", containInBody = Seq(c.email, c.phone), method = RequestMethod.PUT)
      case c: AddAddress => Match(s"/contacts/${c.contactId}/address.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.address.address, c.address.city, c.address.country, c.address.neighborhood, c.address.postalCode, c.address.region, c.address.tag), RequestMethod.POST)
      case c: AddPhone => Match(s"/contacts/${c.contactId}/phone.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.phone.phone, c.phone.tag), RequestMethod.POST)
      case c: AddCustomField => Match(s"/contacts/${c.contactId}/custom.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.customField.field, c.customField.value), RequestMethod.POST)
      case c: AddUrl => Match(s"/contacts/${c.contactId}/url.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.urlToAdd.tag, c.urlToAdd.url), RequestMethod.POST)
      case c: AddDate => Match(s"/contacts/${c.contactId}/date.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.date.tag, c.date.date), RequestMethod.POST)
      case c: UpdateName => Match(s"/contacts/${c.contactId}/name.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.name.first, c.name.last, c.name.middle, c.name.prefix, c.name.suffix), RequestMethod.PUT)
      case c: UpdateCompany => Match(s"/contacts/${c.contactId}/company.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.company.name, c.company.role), RequestMethod.PUT)
      case c: UpdateAddress => Match(s"/contacts/${c.contactId}/address/${c.addressId}.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.address.address, c.address.city, c.address.country, c.address.neighborhood, c.address.postalCode, c.address.region, c.address.tag), RequestMethod.PUT)
      case c: UpdateEmail => Match(s"/contacts/${c.contactId}/email/${c.emailId}.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.email.email, c.email.tag), RequestMethod.PUT)
      case c: UpdatePhone => Match(s"/contacts/${c.contactId}/phone/${c.phoneId}.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.phone.phone, c.phone.tag), RequestMethod.PUT)
      case c: UpdateUrl => Match(s"/contacts/${c.contactId}/url/${c.urlId}.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.urlToUpdate.tag, c.urlToUpdate.url), RequestMethod.PUT)
      case c: UpdateDate => Match(s"/contacts/${c.contactId}/date/${c.dateId}.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.date.date, c.date.tag), RequestMethod.PUT)
      case c: UpdateCustomField => Match(s"/contacts/${c.contactId}/custom/${c.customFieldId}.*${urlEncode(c.modifiedAtOption.get.toString)}", Seq(c.customField.field, c.customField.value), RequestMethod.PUT)
      case c: GetContactActivities => val cursorPortion = c.cursor.fold("")(cur => s".*$cur"); Match(s"/contacts/${c.contactId}/activities$cursorPortion")
      case c: GetActivities => Match("/activities.*")
      case c: GetActivityById => Match(s"/activities/${c.id}")
      case c: CreateContactActivity => Match(s"/contacts/${c.contactId}/activities", method = RequestMethod.POST)
      case c: GetActivityTypes => Match("/activities/types")
      case c: CreateActivity => Match(s"/activities.*userSessionToken=${c.userSessionToken}", Seq(c.activity.activityLocationUrl, c.activity.activityDetails, c.activity), RequestMethod.POST)
      case c: InsightActivitySummary => val contactIdPortion = c.contactId.fold("")(con => s"/$con"); Match(s"/insights/contacts$contactIdPortion/activities/summary")
      case c: SendEmail => Match("/services/actions/email", method = RequestMethod.POST, statusCode = Some(202))
      case c: SendSingle => Match("/services/actions/email/single", method = RequestMethod.POST, statusCode = Some(202))
      case c: ServiceDone => Match("/services/actions/done", method = RequestMethod.POST)
      case c: EmailProviders.type => Match("/services/actions/email/providers")
      case c: GetSitePages.type => Match("/sites/site/pages")
      case c: Site.type => Match("/sites/site")
      case c: GetLabelById => Match(s"/labels/${c.id}")
      case c: GetLabels => Match("/labels")
      case c: GetRedirects.type => Match("/redirects")
      case c: ProcessBatch => Match("/batch", method = RequestMethod.POST)
    }
  }

  private def versionedUrlMatcher(url: String) = urlMatching(s"/v1$url.*")

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
      case s: String => s
      case b: BoxedUnit => ""
      case other => mapper.writeValueAsString(other)
    }

    new MappingBuilder(method, versionedUrlMatcher(url))
      .withStandardHeaders(app)
      .willReturn(aResponse().withBody(body).withStatus(statusCode))
  }

  private def urlEncode(str: String): String = URLEncoder.encode(str, "UTF-8")
}