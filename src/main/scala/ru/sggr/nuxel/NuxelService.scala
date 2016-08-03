package ru.sggr.nuxel

import java.io.InputStream
import java.util.List

import org.apache.poi.hssf.usermodel.HSSFWorkbook

import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.util.CellRangeAddress

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

         override def num = bean.num

         override def errors = validator.validate(bean) :: bean.errors
       } else bean
     else
       beans


     private def getBeans(is: InputStream): List[Bean] = {

       val sheet = WorkbookFactory.create(is).getSheetAt(0)

       def cellContent(x: Int, n: Int, offset: (Int,Int) = (0,0)) = {
         def transformToPhys(x: Int, y: Int) = {
           val result = sheet.getMergedRegions().filter(region =>  
               region.getFirstRow <= y && region.getFirstColumn< x &&
               region.getLastRow >= y && region.getLastColumn >= x && 
               (region.getFirstRow < y || region.getFirstColumn < x)) map { 
                 ( region : CellRangeAddress) => 
                   (region.getLastColumn + (x-region.getFirstColumn),region.getLastRow+( y-region.getFirstRow))
               }
               if (!result.isEmpty) result (0) else (x,y)
         }

         ((x : Int, n: Int) => {
           if (sheet.getRow(n)!=null && sheet.getRow(n).getCell(x) != null){
             sheet.getRow(n).getCell(x).getCellType match {
               case Cell.CELL_TYPE_NUMERIC => sheet.getRow(n).getCell(x).getNumericCellValue().toString
               case Cell.CELL_TYPE_STRING => sheet.getRow(n).getCell(x).getStringCellValue()
               case _ => ""
             }
             } else {
               ""
             }
         }).tupled(transformToPhys(x+offset._1,n+offset._2))
       }

       val colrow = (for {
         rowNum <- 0 until sheet.getLastRowNum
         colNum <- 0 until { 
           if (sheet.getRow(rowNum) != null) 
             sheet.getRow(rowNum).getLastCellNum
           else 
             0
         }
         if (cellContent(colNum,rowNum).contains("№")
           && cellContent(colNum+1,rowNum).contains("азвание")
         && cellContent(colNum+2,rowNum).contains("оследовательность")
       )
       } yield (colNum,rowNum))
       if (!colrow.isEmpty){
         val offset = colrow.head
         val result = (for {
           row <- 0 until sheet.getPhysicalNumberOfRows 
           } yield new Bean {
             override val name: String = cellContent(Columns.Name.id, row, offset)
             override val oe: String = cellContent(Columns.OE.id, row, offset)             
             override val sequence: String = cellContent(Columns.Sequence.id, row, offset) 
             override val num: String = cellContent(Columns.Num.id, row, offset) 
           }).tail
         val idx = result.indexWhere(x => x.num.isEmpty || x.name.isEmpty);
         if (idx > 0) {
           result.splitAt(idx)._1
         } else {
           result
         }
       } else {
         scala.List.empty[Bean]
       }
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
