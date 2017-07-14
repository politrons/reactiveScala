package app.impl.finagle.resolver.mapper

import java.io.IOException
import java.math.BigInteger

import com.fasterxml.jackson.core.{JsonParseException, JsonParser}
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

/**
  * JSON deserializer for [[BigInteger]]. Because de-serializing very large numbers might lead DDOS attacks, such
  * numbers are being rejected in an early stage.
  */
class BigIntegerDeserializer(maxDigits: Int) extends JsonDeserializer[BigInteger] {

  def this() {
    this(maxDigits = 1000)
  }

  @throws[IOException]
  override def deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): BigInteger = {
    val s: String = jsonParser.getText
    if (!hasProperNumberLength(s))
      throw new JsonParseException(jsonParser, s"JSON number contains more than $maxDigits digits")

    new BigInteger(s)
  }

  private def hasProperNumberLength(s: String): Boolean = {
    if (s.startsWith("-"))
      s.length - 1 <= maxDigits
    else
      s.length <= maxDigits
  }

}
