package com.wix.hive.commands.services

import com.wix.hive.model.services.ServiceData


case class ServiceDone(serviceData: ServiceData) extends ServicesCommand[Unit] {
  override def url: String = super.url + "/done"
  override def body: Option[AnyRef] = Some(serviceData)
}
