package ru.sggr.nuxel

/**
 * Trait that represents single item from order
 */
trait Bean {
  def name: String
  def sequence: String
  def oe: String
  def errors : List[String] = List[String]()
}
