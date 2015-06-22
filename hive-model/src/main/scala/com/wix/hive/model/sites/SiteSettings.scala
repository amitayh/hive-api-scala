package com.wix.hive.model.sites

case class SiteSettings(locale: Locale = Locale(),
                        locationInfo: Seq[LocationInfo] = Nil)

case class Locale(language: Option[String] = None,
                  currency: Option[String] = None,
                  dateFormat: Option[String] = None,
                  startWeek: Option[String] = None,
                  timeZone: Option[String] = None)

case class LocationInfo(name: Option[String] = None,
                        email: Option[String] = None,
                        phone: Option[String] = None,
                        fax: Option[String] = None,
                        address: Option[Address] = None,
                        accessibility: Option[Boolean] = None,
                        parking: Option[Boolean] = None)

case class Address(description: Option[String] = None,
                   street: Option[String] = None,
                   city: Option[String] = None,
                   country: Option[String] = None,
                   state: Option[String] = None,
                   zip: Option[String] = None,
                   geoLocation: Option[GeoLocation] = None)

case class GeoLocation(lat: Double, long: Double)