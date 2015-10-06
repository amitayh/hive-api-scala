package com.wix.hive.model.activities

import com.wix.accord._
import com.wix.accord.specs2.ResultMatchers
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * Created by Uri_Keinan on 10/6/15.
 */
class ActivityInfoValidationTest extends SpecificationWithJUnit with ResultMatchers {

  trait ctx extends Scope {
    val notEmpty = "notEmpty"

  }
  "NameValueProperty" should {

    "fail on all null values" in new ctx {
      validate(NameValueProperty(name = null, value = null)) must failWith("name" -> "is a null", "value" -> "is a null")
    }

    "succeed when non values" in new ctx {
      validate(NameValueProperty(name = notEmpty, value = notEmpty)) must succeed
    }


  }

}
