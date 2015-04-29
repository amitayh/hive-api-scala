package com.wix.hive.model.batch

/**
 * @author viliusl
 * @since 29/04/15
 */
case class OperationResult(
  id: String,
  method: String,
  relativeUrl: String,
  responseCode: Int,
  body: Option[String])
