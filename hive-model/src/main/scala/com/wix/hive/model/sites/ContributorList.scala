package com.wix.hive.model.sites

/**
 * @author maximn
 * @since 08-Sep-2015
 */
case class ContributorList(contributors: Seq[Contributor])

case class Contributor(id: String, roles: Seq[String])