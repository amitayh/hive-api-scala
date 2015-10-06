package com.wix.hive.model.activities

import com.wix.accord.dsl._

/**
 * Created by Uri_Keinan on 10/6/15.
 */
case class NameValueProperty(name: String, value: String)

object NameValueProperty {
  implicit val nameValuePropertyValidator = validator[NameValueProperty] { p =>
    p.name is notNull
    p.value is notNull
  }
}
