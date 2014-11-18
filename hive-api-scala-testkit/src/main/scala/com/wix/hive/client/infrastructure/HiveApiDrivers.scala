package com.wix.hive.client.infrastructure

import java.util.UUID

import com.wix.hive.commands.contacts._
import com.wix.hive.model.activities.Activity
import com.wix.hive.model.activities.ActivityType.ActivityType
import com.wix.hive.model.contacts.{Contact, ContactName}
import com.wix.hive.model.insights.ActivitySummary
import org.joda.time.DateTime

/**
 * User: maximn
 * Date: 11/17/14
 */
trait HiveApiDrivers {
  def givenContactFetchById(myself: AppDef, respondsWith: Contact): Unit

  def givenAppWithContacts(app: AppDef, respondsWith: Contact*): Unit

  def givenContactCreatedById(app: AppDef, contact: ContactData, respondWithContactId: String): Unit

  def givenContactUpsertByPhoneAndEmail(app: AppDef, phone: Option[String], email: Option[String], contactId: String)

  def verifyUpsertContactWithId(app: AppDef, phone: Option[String], email: Option[String], contactId: String): Unit

  def givenContactAddAddress(app: AppDef, contactId: String, modifiedAt: DateTime, address: AddressDTO): Unit

  def givenContactAddEmail(app: AppDef, contactId: String, modifiedAt: DateTime, email: ContactEmailDTO): Unit

  def givenContactAddPhone(app: AppDef, contactId: String, modifiedAt: DateTime, phone: ContactPhoneDTO): Unit

  def givenContactAddUrl(app: AppDef, contactId: String, modifiedAt: DateTime, url: ContactUrlDTO): Unit

  def givenContactAddDate(app: AppDef, contactId: String, modifiedAt: DateTime, date: ContactDateDTO): Unit

  def givenContactUpdateName(app: AppDef, contactId: String, modifiedAt: DateTime, name: ContactName): Unit

  def givenContactUpdateCompany(app: AppDef, contactId: String, modifiedAt: DateTime, company: CompanyDTO): Unit

  def givenContactUpdatePicture(app: AppDef, contactId: String, modifiedAt: DateTime, picture: PictureDTO): Unit

  def givenContactUpdateAddress(app: AppDef, contactId: String, modifiedAt: DateTime, addressId:String, address: AddressDTO): Unit

  def givenContactUpdateEmail(app: AppDef, contactId: String, modifiedAt: DateTime, emailId: String, email: ContactEmailDTO): Unit

  def givenContactUpdatePhone(app: AppDef, contactId: String, modifiedAt: DateTime, phoneId: String, phone: ContactPhoneDTO): Unit

  def givenContactUpdateUrl(app: AppDef, contactId: String, modifiedAt: DateTime, urlId: String, url: ContactUrlDTO): Unit

  def givenContactUpdateDate(app: AppDef, contactId: String, modifiedAt: DateTime, dateId: String, date: ContactDateDTO): Unit

  def givenActivitiesForContact(app: AppDef, contactId: String, cursor: String, activities: Activity*): Unit



  def givenAppWithActivitiesById(myself: AppDef, activities: Activity*): Unit

  def givenAppWithActivitiesBulk(myself: AppDef, activities: Activity*): Unit

  def givenAppActivityTypes(app: AppDef, types: ActivityType*): Unit

  def givenAppWithContactExist(app: AppDef, contactId: String): Unit

  def getValidUserSessionToken: String

  def verifyActivityCreated(appDef: AppDef): Unit

  def givenAppWithSite(appDef: AppDef, url: String): Unit

  def givenAppWithUserActivities(app: AppDef, contactId: String, responseWith: ActivitySummary): Unit

  def givenAppWithActivities(app: AppDef, responseWith: ActivitySummary): Unit
}

