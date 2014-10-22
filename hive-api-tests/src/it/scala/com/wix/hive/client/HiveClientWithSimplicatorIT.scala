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
  skipAll

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

  def versionedUrlMatcher(url: String) = urlMatching(s"v1$url")

  override def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit = {
    val contactJson = mapper.writeValueAsString(respondsWith)

    givenThat(get(urlMatching("/contacts/.*")).
      //withHeader("x-wix-application-id", equalTo(myself.appId)).
      //withHeader("x-wix-instance-id", equalTo(myself.instanceId)).
      //withHeader("x-wix-timestamp", matching("/^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$/")).
      //withHeader("x-wix-signature", matching("*")).
      willReturn(aResponse().withBody(contactJson).withStatus(200)))

    //    import myself._
    //
    //    val smId = randomGuid[SMCollection]
    //    givenAppsDefined(myself)
    //    givenMetaSiteWith(smId, appId.asGuid[WixApp], instanceId.asId[WixAppInstance])
    //    givenMetaSiteWith(ContactsReadPersonal)(appId.asGuid[WixApp], instanceId.asId[WixAppInstance])
    //    givenContactServerWith(smId, respondsWith.id.asGuid[ContactDto])(contact.copy(id = respondsWith.id))
  }
}