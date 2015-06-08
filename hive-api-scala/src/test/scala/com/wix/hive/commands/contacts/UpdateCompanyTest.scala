package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.SpecificationWithJUnit

class UpdateCompanyTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = be_===(s"/contacts/$contactId/company"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(company)
      )
    }
  }

  class Context extends ContextForModification {
    val company = CompanyDTO(Some("role in company"), Some("my name"))

    val cmd = UpdateCompany(contactId, company, modifiedAt)
  }

}