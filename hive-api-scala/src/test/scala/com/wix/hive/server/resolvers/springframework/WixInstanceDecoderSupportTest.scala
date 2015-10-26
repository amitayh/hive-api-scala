package com.wix.hive.server.resolvers.springframework

import java.net.InetSocketAddress
import java.util.UUID
import javax.servlet.ServletException

import com.wix.hive.drivers.InstanceEncoderSupport
import com.wix.hive.server.instance.WixInstance
import org.joda.time.{DateTime, DateTimeZone}
import org.junit.Assert.{assertTrue, fail}
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers._
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

/**
 * Using a JUnit here in order to use Spring's testkit, which does not work well with Specs2
 * The testkit allows integration tests for Spring MVC controllers and configuration
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[WixInstanceSpringConfig]))
@WebAppConfiguration
class WixInstanceDecoderSupportTest extends InstanceEncoderSupport {

  @Autowired
  var webApplicationContext: WebApplicationContext = _

  var mockMvc: MockMvc = _

  var instance: WixInstance = _

  @Before
  def setUp(): Unit = {
    mockMvc = MockMvcBuilders
      .webAppContextSetup(webApplicationContext)
      .build

    instance = WixInstance(
      instanceId = UUID.randomUUID(),
      signedAt = DateTime.now.withZone(DateTimeZone.UTC),
      userId = Some(UUID.randomUUID().toString),
      permissions = Set("OWNER"),
      userIp = InetSocketAddress.createUnresolved("5.102.254.181", 62834),
      premiumPackageId = Some("Premium1"),
      demoMode = false,
      ownerId = UUID.randomUUID())
  }

  @Test
  def testDecodeInstancePassedFromHttpHeader(): Unit = {
    val request = get("/handle")
      .header(WixInstanceConfig.HeaderName, signAndEncodeInstance(instance, WixInstanceConfig.SecretKey))

    mockMvc
      .perform(request)
      .andExpect(status.isOk)
  }

  @Test
  def testDecodeInstancePassedFromQueryParam(): Unit = {
    val request = get("/handle")
      .param(WixInstanceConfig.QueryParamName, signAndEncodeInstance(instance, WixInstanceConfig.SecretKey))

    mockMvc
      .perform(request)
      .andExpect(status.isOk)
  }

  @Test
  def testFailWhenInstanceIsMissingFromRequest(): Unit = {
    val request = get("/handle")

    try {
      mockMvc.perform(request)
      fail("Exception was not thrown")
    }
    catch {
      case e: ServletException =>
        assertTrue(e.getRootCause.isInstanceOf[UnableToExtractInstanceException])
    }
  }

}

object WixInstanceConfig {
  val HeaderName = "X-Wix-Instance"
  val QueryParamName = "instance"
  val SecretKey = "11111111-1111-1111-1111-111111111111"
}

@Configuration
class WixInstanceSpringConfig extends WebMvcConfigurationSupport with WixInstanceDecoderSupport {

  override def wixInstanceHeaderName: String = WixInstanceConfig.HeaderName

  override def wixInstanceQueryParamName: String = WixInstanceConfig.QueryParamName

  override def wixInstanceSecretKey: String = WixInstanceConfig.SecretKey

  @Bean
  def controller = new WixInstanceSampleController

}

@Controller
class WixInstanceSampleController {

  @RequestMapping(value = Array("/handle"))
  def handle(instance: WixInstance) = new ResponseEntity(HttpStatus.OK)

}
