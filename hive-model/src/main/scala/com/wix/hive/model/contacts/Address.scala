package com.wix.hive.model.contacts

case class Address(tag: String, id: Option[Int] = None, address: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None, region: Option[String] = None,
                   country: Option[String] = None, postalCode: Option[String] = None)

case class ContactAddress(tag: String, address: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None, region: Option[String] = None,
                   country: Option[String] = None, postalCode: Option[String] = None)


case class ActivityAddress(tag: String,
                   address: Option[String],
                   neighborhood: Option[String],
                   city: Option[String],
                   region: Option[String],
                   postalCode: Option[String],
                   country: Option[String])
