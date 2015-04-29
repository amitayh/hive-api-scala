package com.wix.hive.model.batch

import com.wix.accord.dsl._
import com.wix.accord.{BaseValidator, Validator}

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

object BatchOperation {
  private def in[T](values: String*) = {
    import com.wix.accord.ViolationBuilder._
    new BaseValidator[T](values.contains, _ -> s"must be one of [${values.mkString(", ")}]")
  }

  implicit val BatchOperationValidator: Validator[BatchOperation] = validator[BatchOperation] { c =>
    c.id is notEmpty
    (c.method is notEmpty) and (c.method is in("GET", "POST", "PUT", "DELETE"))
    c.relativeUrl is notEmpty
    c.headers.each is valid
  }

  implicit val HeaderValidator: Validator[(String, String)] = validator[(String, String)] { c =>
    c._1 is notEmpty
    c._2 is notEmpty
  }
}