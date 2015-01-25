package ru.sggr.nuxel


trait BeanValidator {
  def validate(bean: Bean): String
}

object BeanValidator {
  implicit def function2validator( fun :(Bean) => String ) : BeanValidator = {
    new BeanValidator {
      override def validate(bean: Bean)= fun(bean)
    }
  }
}
