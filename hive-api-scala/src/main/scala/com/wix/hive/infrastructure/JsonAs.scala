package com.wix.hive.infrastructure

import java.io.{ByteArrayInputStream, InputStream}

import com.wix.hive.json.JacksonObjectMapper

import scala.reflect.ClassTag

object JsonAs {

  def apply[R: ClassTag](r: InputStream): R = {
    val classOfR = implicitly[ClassTag[R]].runtimeClass.asInstanceOf[Class[R]]

    if (classOf[scala.runtime.Nothing$] == classOfR || classOf[Unit] == classOfR) null.asInstanceOf[R]
    else JacksonObjectMapper.mapper.readValue(r, classOfR)
  }

  def apply[T: ClassTag](json: String): T = apply[T](new ByteArrayInputStream(json.getBytes))
}
