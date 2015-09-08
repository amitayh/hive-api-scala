package com.wix.hive.model.sites

/**
 * @author maximn
 * @since 08-Sep-2015
 */
case class ContributorList(contributors: Seq[Contributor])

object ContributorList {
  def singleUser(ownerId: String, role: String) = ContributorList(Seq(Contributor(ownerId, Seq(role))))
}

case class Contributor(id: String, roles: Seq[String])