package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import org.specs2.mutable.SpecificationWithJUnit

class UpdatePictureTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = be_===(s"/contacts/$contactId/picture"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(picture)
      )
    }
  }

  class Context extends ContextForModification{
    val picture = PictureDTO("some-pic-name")

    val cmd = UpdatePicture(contactId, picture, modifiedAt)
  }

}
