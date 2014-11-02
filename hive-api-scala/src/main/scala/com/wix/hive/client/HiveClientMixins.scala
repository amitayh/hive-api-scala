package com.wix.hive.client

//trait ForInstance extends HiveClient {
//  def forInstance(instanceId: String) = this.execute(instanceId, _)
//}
//
//trait Activities {
//  self: ForInstance =>
//
//  def getActivityById(id: String): Future[Activity] = self.forInstance(GetActivityById(id))
//
//  def getActivityTypes: Future[ActivityTypes] = self.execute(GetActivityTypes())
//
//  def createActivity(userSessionToken: String, activity: ActivityCreationData) = self.execute(CreateActivity(userSessionToken, activity))
//
//  def getActivities(activityTypes: Seq[String] = Nil, until: Option[DateTime] = None, from: Option[DateTime] = None,
//                    scope: ActivityScope = ActivityScope.site, cursor: Option[String] = None,
//                    pageSize: PageSizes = PageSizes.`25`) =
//    self.execute(GetActivities(activityTypes, until, from, scope, cursor, pageSize))
//}
//
//trait Contacts {
//  self: HiveClient =>
//
//  def createContact(name: Option[ContactName] = None, picture: Option[String] = None, company: Option[Company] = None,
//                    tags: Seq[String] = Nil, emails: Seq[ContactEmail] = Nil, phone: Seq[ContactPhone] = Nil,
//                    addresses: Seq[Address] = Nil, urls: Seq[ContactUrl] = Nil, dates: Seq[ImportantDate] = Nil,
//                    notes: Seq[Note] = Nil, custom: Seq[CustomField] = Nil) =
//    self.execute(CreateContact(name, picture, company, tags, emails, phone, addresses, urls, dates, notes, custom))
//
//  def getContactById(id: String) = self.execute(GetContactById(id))
//
//  def getContacts(tag: Seq[String] = Nil, email: Option[String] = None, phone: Option[String] = None,
//                  firstName: Option[String] = None, lastName: Option[String] = None, cursor: Option[String] = None, pageSize: Option[PageSizes] = None) =
//    self.execute(GetContacts(tag, email, phone, firstName, lastName, cursor, pageSize))
//}