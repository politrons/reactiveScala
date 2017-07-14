package app.impl.finagle.resolver.mapper

import java.io.IOException
import java.math.BigDecimal

import com.fasterxml.jackson.core.{JsonParseException, JsonParser}
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

/**
  * JSON deserializer for [[BigDecimal]]. Because de-serializing very large numbers might lead DDOS attacks, such
  * numbers are being rejected in an early stage.
  */
class BigDecimalDeserializer(maxDigits: Int) extends JsonDeserializer[BigDecimal] {


  private val maxValue = BigDecimal.TEN.pow(maxDigits)

  def this() {
    this(maxDigits = 1000)
  }

  override def deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): BigDecimal = {
    val bigDecimal = parseBigDecimal(jsonParser)

    //Because the string representation might still contain minus sign, decimal period and/or a potential
    //scientific notation (e.g. 1e10), the soundness of the big decimal being parsed needs to be validated in depth.
    if ((bigDecimal.abs.compareTo(maxValue) < 0) && bigDecimal.scale < maxDigits)
      bigDecimal
    else
      throw new JsonParseException(jsonParser, s"JSON number contains more than $maxDigits digits")
  }

  @throws[IOException]
  private def parseBigDecimal(jsonParser: JsonParser): BigDecimal = {
    val s = jsonParser.getText

    // If the number of characters of the big decimal being parsed is very big, then interpreting these characters as a
    // big decimal takes considerable time. Therefore, a JsonParseException is thrown if the length of the characters is
    // 'way too large'; more than maxDigits: one because there might be minus sign and one because there might be a
    // decimal separator (period).
    if (s.length > maxDigits + 2)
      throw new JsonParseException(jsonParser, s"JSON number contains more than $maxDigits digits")

    new BigDecimal(s)
  }
}
