package nebula4scala.data.value

import scala.jdk.CollectionConverters._

import com.vesoft.nebula._
import com.vesoft.nebula.client.graph.data._
import com.vesoft.nebula.client.graph.exception.InvalidValueException

object ValueWrapper {

  final val __NULL__ : Int    = 0
  final val NaN: Int          = 1
  final val BAD_DATA: Int     = 2
  final val BAD_TYPE: Int     = 3
  final val ERR_OVERFLOW: Int = 4
  final val UNKNOWN_PROP: Int = 5
  final val DIV_BY_ZERO: Int  = 6
  final val OUT_OF_RANGE: Int = 7
  final val ENCODING          = "utf-8"
  final val TIMEZONE_OFFSET   = 0

  sealed trait NullType {
    def nullType: Int

    override def toString: String = this match {
      case Null(v) if v == __NULL__     => "NULL"
      case Null(v) if v == NaN          => "NaN"
      case Null(v) if v == BAD_DATA     => "BAD_DATA"
      case Null(v) if v == BAD_TYPE     => "BAD_TYPE"
      case Null(v) if v == ERR_OVERFLOW => "ERR_OVERFLOW"
      case Null(v) if v == UNKNOWN_PROP => "UNKNOWN_PROP"
      case Null(v) if v == DIV_BY_ZERO  => "DIV_BY_ZERO"
      case Null(v) if v == OUT_OF_RANGE => "OUT_OF_RANGE"
      case _                            => s"Unknown type: $nullType"
    }
  }

  final case class Null(nullType: Int) extends NullType

  implicit final class ValueWrapperNebulaClass(val valueWrapper: com.vesoft.nebula.client.graph.data.ValueWrapper)
      extends AnyVal {

    def asScala: ValueWrapper =
      ValueWrapper(valueWrapper.getValue, ENCODING, TIMEZONE_OFFSET)
  }

  implicit final class ValueWrapperClass(val valueWrapper: ValueWrapper) extends AnyVal {

    def asJava: com.vesoft.nebula.client.graph.data.ValueWrapper =
      new com.vesoft.nebula.client.graph.data.ValueWrapper(
        valueWrapper.value,
        valueWrapper.decodeType,
        valueWrapper.timezoneOffset
      )
  }
}

final case class ValueWrapper(value: Value, decodeType: String = "utf-8", timezoneOffset: Int = 0) {

  import ValueWrapper._

  private def descType(): String = value.getSetField match {
    case Value.NVAL  => "NULL"
    case Value.BVAL  => "BOOLEAN"
    case Value.IVAL  => "INT"
    case Value.FVAL  => "FLOAT"
    case Value.SVAL  => "STRING"
    case Value.DVAL  => "DATE"
    case Value.TVAL  => "TIME"
    case Value.DTVAL => "DATETIME"
    case Value.VVAL  => "VERTEX"
    case Value.EVAL  => "EDGE"
    case Value.PVAL  => "PATH"
    case Value.LVAL  => "LIST"
    case Value.MVAL  => "MAP"
    case Value.UVAL  => "SET"
    case Value.GVAL  => "DATASET"
    case Value.GGVAL => "GEOGRAPHY"
    case Value.DUVAL => "DURATION"
    case _           => throw new IllegalArgumentException(s"Unknown field id ${value.getSetField}")
  }

  def isEmpty: Boolean = value.getSetField == 0

  def isNull: Boolean = value.getSetField == Value.NVAL

  def isBoolean: Boolean = value.getSetField == Value.BVAL

  def isLong: Boolean = value.getSetField == Value.IVAL

  def isDouble: Boolean = value.getSetField == Value.FVAL

  def isString: Boolean = value.getSetField == Value.SVAL

  def isList: Boolean = value.getSetField == Value.LVAL

  def isSet: Boolean = value.getSetField == Value.UVAL

  def isMap: Boolean = value.getSetField == Value.MVAL

  def isTime: Boolean = value.getSetField == Value.TVAL

  def isDate: Boolean = value.getSetField == Value.DVAL

  def isDateTime: Boolean = value.getSetField == Value.DTVAL

  def isVertex: Boolean = value.getSetField == Value.VVAL

  def isEdge: Boolean = value.getSetField == Value.EVAL

  def isPath: Boolean = value.getSetField == Value.PVAL

  def isGeography: Boolean = value.getSetField == Value.GGVAL

  def isDuration: Boolean = value.getSetField == Value.DUVAL

  def asNull: NullType = value.getSetField match {
    case Value.NVAL => Null(value.getFieldValue.asInstanceOf[com.vesoft.nebula.NullType].getValue)
    case _ => throw new InvalidValueException(s"Cannot get field nullType because value's type is ${descType()}")
  }

  def asBoolean: Boolean = value.getSetField match {
    case Value.BVAL => value.getFieldValue.asInstanceOf[Boolean]
    case _ => throw new InvalidValueException(s"Cannot get field boolean because value's type is ${descType()}")
  }

  def asLong: Long = value.getSetField match {
    case Value.IVAL => value.getFieldValue.asInstanceOf[Long]
    case _          => throw new InvalidValueException(s"Cannot get field long because value's type is ${descType()}")
  }

  def asDouble: Double = value.getSetField match {
    case Value.FVAL => value.getFieldValue.asInstanceOf[Double]
    case _          => throw new InvalidValueException(s"Cannot get field double because value's type is ${descType()}")
  }

  def asString: String = value.getSetField match {
    case Value.SVAL => new String(value.getFieldValue.asInstanceOf[Array[Byte]], decodeType)
    case _          => throw new InvalidValueException(s"Cannot get field string because value's type is ${descType()}")
  }

  def asList: List[ValueWrapper] = value.getSetField match {
    case Value.LVAL => value.getLVal.getValues.asScala.map(v => new ValueWrapper(v, decodeType, timezoneOffset)).toList
    case _ => throw new InvalidValueException(s"Cannot get field type `list' because value's type is ${descType()}")
  }

  def asSet: Set[ValueWrapper] = value.getSetField match {
    case Value.UVAL => value.getUVal.getValues.asScala.map(v => new ValueWrapper(v, decodeType, timezoneOffset)).toSet
    case _ => throw new InvalidValueException(s"Cannot get field type `set' because value's type is ${descType()}")
  }

  def asMap: Map[String, ValueWrapper] = value.getSetField match {
    case Value.MVAL => {
      value.getMVal.getKvs.asScala.map { case (k, v) =>
        new String(k, decodeType) -> new ValueWrapper(v, decodeType, timezoneOffset)
      }.toMap
    }
    case _ => throw new InvalidValueException(s"Cannot get field type `map' because value's type is ${descType()}")
  }

  def asTime: TimeWrapper = value.getSetField match {
    case Value.TVAL =>
      val wrapper = new TimeWrapper(value.getTVal)
      wrapper.setDecodeType(decodeType).setTimezoneOffset(timezoneOffset)
      wrapper
    case _ => throw new InvalidValueException(s"Cannot get field time because value's type is ${descType()}")
  }

  def asDate: DateWrapper = value.getSetField match {
    case Value.DVAL => new DateWrapper(value.getDVal)
    case _          => throw new InvalidValueException(s"Cannot get field date because value's type is ${descType()}")
  }

  def asDateTime: DateTimeWrapper = value.getSetField match {
    case Value.DTVAL =>
      val wrapper = new DateTimeWrapper(value.getDtVal)
      wrapper.setDecodeType(decodeType).setTimezoneOffset(timezoneOffset)
      wrapper
    case _ => throw new InvalidValueException(s"Cannot get field datetime because value's type is ${descType()}")
  }

  def asNode: Node = value.getSetField match {
    case Value.VVAL =>
      val wrapper = new Node(value.getVVal)
      wrapper.setDecodeType(decodeType).setTimezoneOffset(timezoneOffset)
      wrapper
    case _ => throw new InvalidValueException(s"Cannot get field Node because value's type is ${descType()}")
  }

  def asRelationship: Relationship = value.getSetField match {
    case Value.EVAL =>
      val wrapper = new Relationship(value.getEVal)
      wrapper.setDecodeType(decodeType).setTimezoneOffset(timezoneOffset)
      wrapper
    case _ => throw new InvalidValueException(s"Cannot get field Relationship because value's type is ${descType()}")
  }

  def asPath: PathWrapper = value.getSetField match {
    case Value.PVAL =>
      val wrapper = new PathWrapper(value.getPVal)
      wrapper.setDecodeType(decodeType).setTimezoneOffset(timezoneOffset)
      wrapper
    case _ => throw new InvalidValueException(s"Cannot get field PathWrapper because value's type is ${descType()}")
  }

  def asGeography: GeographyWrapper = value.getSetField match {
    case Value.GGVAL =>
      val wrapper = new GeographyWrapper(value.getGgVal)
      wrapper.setDecodeType(decodeType).setTimezoneOffset(timezoneOffset)
      wrapper
    case _ =>
      throw new InvalidValueException(s"Cannot get field GeographyWrapper because value's type is ${descType()}")
  }

  def asDuration: DurationWrapper = value.getSetField match {
    case Value.DUVAL =>
      val wrapper = new DurationWrapper(value.getDuVal)
      wrapper.setDecodeType(decodeType).setTimezoneOffset(timezoneOffset)
      wrapper
    case _ =>
      throw new InvalidValueException(s"Cannot get field DurationWrapper because value's type is ${descType()}")
  }

  override def toString: String = {
    (
      isEmpty,
      isNull,
      isBoolean,
      isLong,
      isDouble,
      isString,
      isList,
      isSet,
      isMap,
      isTime,
      isDate,
      isDateTime,
      isVertex,
      isEdge,
      isPath,
      isGeography,
      isDuration
    ) match {
      case (true, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) => "__EMPTY__"
      case (_, true, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) => asNull.toString
      case (_, _, true, _, _, _, _, _, _, _, _, _, _, _, _, _, _) => asBoolean.toString
      case (_, _, _, true, _, _, _, _, _, _, _, _, _, _, _, _, _) => asLong.toString
      case (_, _, _, _, true, _, _, _, _, _, _, _, _, _, _, _, _) => asDouble.toString
      case (_, _, _, _, _, true, _, _, _, _, _, _, _, _, _, _, _) => "\"" + asString + "\""
      case (_, _, _, _, _, _, true, _, _, _, _, _, _, _, _, _, _) => asList.toString
      case (_, _, _, _, _, _, _, true, _, _, _, _, _, _, _, _, _) => asSet.toString
      case (_, _, _, _, _, _, _, _, true, _, _, _, _, _, _, _, _) => asMap.toString
      case (_, _, _, _, _, _, _, _, _, true, _, _, _, _, _, _, _) => asTime.toString
      case (_, _, _, _, _, _, _, _, _, _, true, _, _, _, _, _, _) => asDate.toString
      case (_, _, _, _, _, _, _, _, _, _, _, true, _, _, _, _, _) => asDateTime.toString
      case (_, _, _, _, _, _, _, _, _, _, _, _, true, _, _, _, _) => asNode.toString
      case (_, _, _, _, _, _, _, _, _, _, _, _, _, true, _, _, _) => asRelationship.toString
      case (_, _, _, _, _, _, _, _, _, _, _, _, _, _, true, _, _) => asPath.toString
      case (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, true, _) => asGeography.toString
      case (_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, true) => asDuration.toString
      case _                                                      => s"Unknown type: ${descType()}"
    }
  }
}
