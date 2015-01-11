package com.wix.hive.commands.services

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.commands.HiveBaseCommand
import com.wix.hive.model.services.ServiceData


case class ServiceDone(serviceData: ServiceData) extends ServicesCommand {
  override def url: String = super.url + "/done"
  override def body: Option[AnyRef] = Some(serviceData)
}



