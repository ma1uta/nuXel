package ru.sggr.nuxel

import java.io.InputStream
import java.util.List

import org.apache.poi.hssf.usermodel.HSSFWorkbook

import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.WorkbookFactory

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
    validateBeans(getBeans(is))
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


  private def getBeans(is: InputStream): List[Bean] = {
    
    val sheet = WorkbookFactory.create(is).getSheetAt(0)
    
    def cellContent(x: Int, n: Int) = {
        if (sheet.getRow(n).getCell(x) != null){
            sheet.getRow(n).getCell(x).getCellType match {
                case Cell.CELL_TYPE_NUMERIC => sheet.getRow(n).getCell(x).getNumericCellValue().toString
                case Cell.CELL_TYPE_STRING => sheet.getRow(n).getCell(x).getStringCellValue()
                case _ => ""
            }
        } else {
        ""
        }
    }

    (for {
    row <- 0 until sheet.getPhysicalNumberOfRows 
    } yield new Bean {
        override val name: String = cellContent(Columns.Name.id, row)
        override val oe: String = cellContent(Columns.OE.id, row)             
        override val sequence: String = cellContent(Columns.Sequence.id, row) 
    }).tail.filter ( x => !x.name.isEmpty && !x.oe.isEmpty && !x.sequence.isEmpty)
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
