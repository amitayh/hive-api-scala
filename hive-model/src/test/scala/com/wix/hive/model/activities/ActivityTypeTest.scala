package com.wix.hive.model.activities

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * Created by Uri_Keinan on 10/6/15.
 */
class ActivityTypeTest extends SpecificationWithJUnit {
  "ActivityType must be an enumeration" in new Scope {
    ActivityType must beAnInstanceOf[Enumeration]
    println("values: " + ActivityType.values)
    }

}
