package com.wix.hive.server.resolvers

import com.wix.hive.server.resolvers.Spring.{ArgumentResolversRegistrar, WixInstanceArgumentResolver}
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

import scala.collection.JavaConversions._

class ArgumentResolversRegistrarTest extends SpecificationWithJUnit with Mockito {

  "ArgumentResolversRegistrar" should {

    trait Context extends Scope {
      val adapter = new RequestMappingHandlerAdapter
      val oldResolver = mock[WixInstanceArgumentResolver]
      adapter.setArgumentResolvers(List(oldResolver))

      val newResolver = mock[WixInstanceArgumentResolver]
      val registrar = new ArgumentResolversRegistrar(adapter, newResolver)

      registrar.register()
    }

    "register new argument resolvers" in new Context {
      adapter.getArgumentResolvers.get(0) must equalTo(newResolver)
    }

    "keep old argument resolvers" in new Context {
      adapter.getArgumentResolvers.get(1) must equalTo(oldResolver)
    }

  }

}
