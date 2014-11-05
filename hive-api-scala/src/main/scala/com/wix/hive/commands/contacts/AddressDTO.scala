package com.wix.hive.commands.contacts

case class AddressDTO(tag: String, address: Option[String] = None, neighborhood: Option[String] = None, city: Option[String] = None,
                      region: Option[String] = None, country: Option[String] = None, postalCode: Option[String] = None)
