package com.wix.hive.model.sites

import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime

case class SiteSettings(title: String,
                        publishStatus: PublishStatus = PublishStatus(isPublished = false),
                        siteMembersAutoApprove: Boolean = false,
                        disableCookies: Boolean = false,
                        indexableBySearchEngines: Boolean = false,
                        mobileOptimized: Boolean = false,
                        contactFormEmail: Option[String] = None,
                        @JsonProperty("package") packagePurchased: Option[String] = None,
                        mailbox: Option[String] = None,
                        favIcon: Option[String] = None,
                        logo: Option[Logo] = None,
                        domainInfo: Seq[DomainInfo] = Nil,
                        locale: Option[Locale] = None,
                        locationInfo: Seq[LocationInfo] = Nil,
                        category: Option[SiteCategory] = None,
                        goals: Seq[String] = Nil)

case class PublishStatus(isPublished: Boolean, lastPublishDate: Option[DateTime] = None)

case class Logo(logo: Option[String] = None, squareLogo: Option[String] = None)

case class DomainInfo(domain: String, googleAnalyticsId: Option[String] = None, isPrimary: Boolean)

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
                        parking: Option[Boolean] = None,
                        paymentMethods: Seq[String] = Nil,
                        workingHours: Seq[WorkDay] = Nil)

case class Address(description: Option[String] = None,
                   street: Option[String] = None,
                   city: Option[String] = None,
                   country: Option[String] = None,
                   state: Option[String] = None,
                   zip: Option[String] = None,
                   geoLocation: Option[GeoLocation] = None)

case class GeoLocation(lat: Double, long: Double)

case class SiteCategory(primary: Option[String] = None, secondary: Option[String] = None)

case class WorkDay(dayOfWeek: Option[String] = None, timeFrames: Seq[TimeFrame] = Nil)

case class TimeFrame(start: DateTime, end: DateTime)
