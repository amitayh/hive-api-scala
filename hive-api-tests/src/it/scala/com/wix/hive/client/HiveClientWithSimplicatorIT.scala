package com.wix.hive.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.{MappingBuilder, WireMock}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.wix.hive.commands.contacts.GetContacts
import com.wix.hive.model.{ActivityInfo, ActivityDetails, Activity, Contact}
import com.github.tomakehurst.wiremock.client.WireMock._
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

  override def beforeTest(): Unit = WireMock.reset()
}

trait HubSimplicator extends HiveApiDrivers {
  val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule)

  def versionedUrlMatcher(url: String) = urlMatching(s"/v1$url")

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

    givenThat(get(versionedUrlMatcher(s"/contacts/${respondsWith.id}.*")).
      withStandardHeaders(myself).
      willReturn(aResponse().withBody(contactJson)))
  }


  override def givenAppWithActivities(myself: AppDef, activities: Activity*): Unit = {
    case class ActivityAsInHubServer(id: String, createdAt: DateTime, activityType: String, activityLocationUrl: Option[String], activityDetails: Option[ActivityDetails], activityInfo: ActivityInfo)


    activities foreach {
      case activity: Activity => {
        import activity._
        val activityJson = mapper.writeValueAsString(ActivityAsInHubServer(id, createdAt, activityInfo.name.toString, activityLocationUrl, activityDetails, activityInfo))

        givenThat(get(versionedUrlMatcher(s"/activities/${activity.id}.*")).
          withStandardHeaders(myself).
          willReturn(aResponse().withBody(activityJson)))
      }
    }
  }
}