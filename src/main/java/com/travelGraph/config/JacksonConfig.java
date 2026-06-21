package com.travelGraph.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;

/**
 * Serializa todo Long/long como String no JSON.
 * JavaScript não representa inteiros acima de 2^53 com precisão, então
 * IDs gerados pelo Neo4j (Snowflake, 18-19 dígitos) precisam viajar como
 * string para evitar arredondamento no round-trip front ↔ API.
 */
@JsonComponent
public class JacksonConfig extends SimpleModule {
    public JacksonConfig() {
        addSerializer(Long.class, ToStringSerializer.instance);
        addSerializer(Long.TYPE, ToStringSerializer.instance);
    }
}
