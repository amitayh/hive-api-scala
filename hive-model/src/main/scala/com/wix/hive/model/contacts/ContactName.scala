package com.wix.hive.model.contacts

case class ContactName(prefix: Option[String] = None, first: Option[String] = None, middle: Option[String] = None,
                       last: Option[String] = None, suffix: Option[String] = None)
