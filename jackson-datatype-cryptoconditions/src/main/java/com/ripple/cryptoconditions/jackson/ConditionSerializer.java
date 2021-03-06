package com.ripple.cryptoconditions.jackson;

/*-
 * ========================LICENSE_START=================================
 * Crypto-Conditions Jackson
 * %%
 * Copyright (C) 2018 Ripple Labs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.google.common.io.BaseEncoding;
import com.ripple.cryptoconditions.Condition;
import com.ripple.cryptoconditions.CryptoConditionWriter;
import com.ripple.cryptoconditions.der.DerEncodingException;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;


/**
 * Jackson serializer {@link Condition} using configurable encodings.
 */
public class ConditionSerializer extends StdScalarSerializer<Condition> {

  private final Encoding encoding;

  /**
   * No-args Constructor.
   */
  public ConditionSerializer() {
    this(Encoding.BASE64);
  }

  /**
   * Required-args Constructor.
   *
   * @param encoding The {@link Encoding} to use for serialization and deserialization of conditions and fulfillments.
   */
  public ConditionSerializer(final Encoding encoding) {
    super(Condition.class, false);
    this.encoding = Objects.requireNonNull(encoding, "Encoding must not be null!");
  }

  @Override
  public void serialize(Condition condition, JsonGenerator gen, SerializerProvider provider)
      throws IOException {

    try {
      switch (encoding) {
        case HEX: {
          gen.writeString(
              BaseEncoding.base16().encode(CryptoConditionWriter.writeCondition(condition))
          );
          break;
        }
        case BASE64: {
          gen.writeString(
              SerializerUtils.encodeBase64(Base64.getEncoder(), condition)
          );
          break;
        }
        case BASE64_WITHOUT_PADDING: {
          gen.writeString(
              SerializerUtils.encodeBase64(Base64.getEncoder().withoutPadding(), condition)
          );
          break;
        }
        case BASE64URL: {
          gen.writeString(
              SerializerUtils.encodeBase64(Base64.getUrlEncoder(), condition)
          );
          break;
        }
        case BASE64URL_WITHOUT_PADDING: {
          gen.writeString(
              SerializerUtils.encodeBase64(Base64.getUrlEncoder().withoutPadding(), condition)
          );
          break;
        }
        default: {
          throw new RuntimeException("Unhandled Encoding!");
        }
      }
    } catch (DerEncodingException e) {
      throw new RuntimeException(e);
    }
  }


}
