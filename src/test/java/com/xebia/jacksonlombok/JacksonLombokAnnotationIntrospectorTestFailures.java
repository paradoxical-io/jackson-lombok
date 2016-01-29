package com.xebia.jacksonlombok;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JacksonLombokAnnotationIntrospectorTestFailures {
    public static final String INVALID_JSON = "{\"name\":\"foobar\",\"empty\":\"\",\"value\":42}";

    private static final ObjectMapper mapperWithExtention = new ObjectMapper().setAnnotationIntrospector(new JacksonLombokAnnotationIntrospector());

    private static final ImmutablePojo multiArgPojo = new ImmutablePojo("foobar", "", 42, 25);

    @Test(expected = JsonMappingException.class)
    public void testJacksonUnableToDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(multiArgPojo);
        mapper.readValue(json, ImmutablePojo.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testJacksonUnableToDeserializeInvalidJson() throws IOException {
        ImmutablePojo output = mapperWithExtention.readValue(INVALID_JSON, ImmutablePojo.class);
        assertThat(output, is(multiArgPojo));
    }
}
