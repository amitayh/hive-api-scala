package com.wix.hive.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.{MappingBuilder, WireMock}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.wix.hive.model._
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

  def versionedUrlMatcher(url: String) = urlMatching(s"/v1$url.*")

  implicit class MappingBuilderImplicits(builder: MappingBuilder) {
    val base64Regex = "[A-Za-z0-9+/_-]*"

    def withStandardHeaders(app: AppDef): MappingBuilder =
      builder.withHeader("x-wix-application-id", equalTo(app.appId)).
        withHeader("x-wix-instance-id", equalTo(app.instanceId)).
        withHeader("x-wix-timestamp", matching(".*")).
        withHeader("x-wix-signature", matching(base64Regex))
  }

  override def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit = {
    val contactJson = mapper.writeValueAsString(respondsWith)

    givenThat(get(versionedUrlMatcher(s"/contacts/${respondsWith.id}")).
      withStandardHeaders(myself).
      willReturn(aResponse().withBody(contactJson)))
  }


  case class ActivityAsInHubServer(id: String, createdAt: DateTime, activityLocationUrl: Option[String], activityDetails: Option[ActivityDetails], activityInfo: ActivityInfo) {
    def this(a: Activity) = this(a.id, a.createdAt, a.activityLocationUrl, a.activityDetails, a.activityInfo)
    val activityType = activityInfo.activityType.toString
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

  override def givenAppActivityTypes(app: AppDef, types: String*): Unit = {
    val typesJson = mapper.writeValueAsString(ActivityTypes(types))

    givenThat(get(versionedUrlMatcher("/activities/types")).
      withStandardHeaders(app).
      willReturn(aResponse().withBody(typesJson)))
  }

  override def givenAppWithContactExist(app: AppDef, contactId: String): Unit = {
    val responseJson = mapper.writeValueAsString(ActivityCreatedResult("activityId", "contactId"))

    givenThat(post(versionedUrlMatcher(s"/activities.*userSessionToken=$getValidUserSessionToken.*"))
      .withStandardHeaders(app).
      willReturn(aResponse().withBody(responseJson)))
  }

  override def verifyActivityCreated(appDef: AppDef): Unit = ()

}