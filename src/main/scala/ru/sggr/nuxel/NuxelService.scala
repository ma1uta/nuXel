package ru.sggr.nuxel

import java.io.InputStream
import java.util.List

import jxl.Workbook

import scala.collection.JavaConversions.{seqAsJavaList, asScalaBuffer}


/**
 * Library entry point
 */
abstract class NuxelService private {
  /*Nah, i should rewrite this block... input stream could be trashed here =P */
  def addValidator(validator: BeanValidator): NuxelService = {
    validators = validator :: validators
    this
  }

  def extractBeans: List[Bean] = {
    validateBeans(getBeans(Workbook.getWorkbook(is)))
  }

  protected var validators: scala.List[BeanValidator]

  protected val is: InputStream //input stream

  private def validateBeans(beans: List[Bean]): List[Bean] =
    if (validators.size > 0)
      for {
        validator <- validators
        bean: Bean <- beans
      } yield if (validator.validate(bean).isEmpty) new Bean {
        override def oe = bean.oe

        override def name = bean.name

        override def sequence = bean.sequence

        override def errors = validator.validate(bean) :: bean.errors
      } else bean
    else
      beans


  private def getBeans(workbook: Workbook): List[Bean] = {
    val sheet = workbook.getSheet(0)

    def cellContent(x: Int, n: Int) =
      sheet.getRow(x)(n).getContents
    (for {
      row <- 0 until sheet.getRows
    } yield new Bean {
        override val name: String = cellContent(row, Columns.Name.id)
        override val oe: String = cellContent(row, Columns.Sequence.id)
        override val sequence: String = cellContent(row, Columns.OE.id)
      }) tail

  }

  private object Columns extends Enumeration {
    //request column format
    type Columns = Value
    val Num, Name, Sequence, Steps, OE = Value
  }

}

object NuxelService {

  def getInstance(inputStream: InputStream): NuxelService = {
    new NuxelService {
      override val is: InputStream = inputStream
      override protected var validators: scala.List[BeanValidator] = scala.List[BeanValidator]()
    }
  }

}
