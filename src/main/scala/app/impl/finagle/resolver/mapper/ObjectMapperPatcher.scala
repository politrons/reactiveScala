package app.impl.finagle.resolver.mapper

import java.math.{BigDecimal, BigInteger}

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

/**
  * Factory for a Jackson [[ObjectMapper]]. Contains functionality to create a patched [[ObjectMapper]] that handles
  * deserialization of [[BigDecimal]]s and [[BigInteger]]s safely by restricting the length of the value. The factory
  * also allows for patching an existing ObjectMapper.
  *
  * Some background: The default Jackson deserializers for BigDecimal and BigInteger are not restricted in the size of
  * the numbers the process. This can cause a high CPU load when doing further computations with these numbers, which
  * can be exploited to perform a (D)DoS attack.
  */
object ObjectMapperPatcher {

  /**
    * Create a patched [[ObjectMapper]] instance that deserializes [[BigDecimal]]s and [[BigInteger]]s safely.
    *
    * @return A patched ObjectMapper
    */
  def patchedMapper: ObjectMapper = {
    val res = createPatchedMapper(new ObjectMapper with ScalaObjectMapper)
    res.registerModule(DefaultScalaModule)
    res
  }

  /**
    * Patch a given [[ObjectMapper]] by supplying safe deserializers for [[BigDecimal]]s and [[BigInteger]]s.
    *
    * @param mapper The ObjectMapper to be patched
    * @return The patched ObjectMapper
    */
  def patchMapper(mapper: ObjectMapper): ObjectMapper = {
    createPatchedMapper(mapper)
  }

  private def createPatchedMapper(mapper: ObjectMapper) = {
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    mapper.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII)
    mapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)

    val module = new SimpleModule
    module.addDeserializer(classOf[BigDecimal], new BigDecimalDeserializer)
    module.addDeserializer(classOf[BigInteger], new BigIntegerDeserializer)

    mapper.registerModule(module)
  }
}
