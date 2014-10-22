package com.wix.hive.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.wix.hive.model.Contact
import com.github.tomakehurst.wiremock.client.WireMock._
import org.apache.log4j.BasicConfigurator

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
    BasicConfigurator.configure();
    WireMock.configureFor("localhost", serverPort)
    wireMockServer.start()
  }

  override def shutdownEnv(): Unit = wireMockServer.shutdown()
}

trait HubSimplicator extends HiveApiDrivers {
  val mapper = new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule)

  def versionedUrlMatcher(url: String) = urlMatching(s"/v1$url")

  override def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit = {
    val contactJson = mapper.writeValueAsString(respondsWith)
    val base64Regex = "[A-Za-z0-9+/_-]*"

    givenThat(get(versionedUrlMatcher("/contacts/.*")).
      withHeader("x-wix-application-id", equalTo(myself.appId)).
      withHeader("x-wix-instance-id", equalTo(myself.instanceId)).
      withHeader("x-wix-timestamp", matching(".*")).
      withHeader("x-wix-signature", matching(base64Regex)).
      willReturn(aResponse().withBody(contactJson).withStatus(200)))
  }
}