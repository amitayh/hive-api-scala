package com.wix.hive.model.contacts

case class Address(tag: String, id: Option[Int] = None, address: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None, region: Option[String] = None,
                   country: Option[String] = None, postalCode: Option[String] = None)
