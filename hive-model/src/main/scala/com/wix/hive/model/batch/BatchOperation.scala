package com.wix.hive.model.batch

/**
 * @author viliusl
 * @since 29/04/15
 */
case class BatchOperation(
  id: String,
  method: String,
  relativeUrl: String,
  headers: Set[(String, String)],
  body: Option[String])
