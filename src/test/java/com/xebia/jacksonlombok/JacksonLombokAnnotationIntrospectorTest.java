package com.xebia.jacksonlombok;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Value;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Value
class Data {
    @JsonProperty
    private String name;
}

@Value
class ImmutablePojoSingle {
    @JsonProperty
    private String name;
}

@Value
class NestedImmutablePojoSingle {
    @JsonProperty
    private Data data;
}

@Value
class ImmutablePojo {
    @JsonProperty("new_name")
    private String name;
    @JsonProperty
    private String empty;
    int value;
    @JsonDeserialize(using = TestSupport.IntDeserializer.class)
    @JsonSerialize(using = TestSupport.IntSerializer.class)
    Integer specialInt;
}

@RunWith(Parameterized.class)
public class JacksonLombokAnnotationIntrospectorTest {

    public static final String JSON = "{\"empty\":\"\",\"value\":42,\"specialInt\":\"24\",\"new_name\":\"foobar\"}";
    public static final String SINGLE_JSON = "{" +
                                             "    \"name\" : \"data\"" +
                                             "}";

    public static final String NESTED_SINGLE_JSON = "{" +
                                                    "    \"data\" : {" +
                                                    "        \"name\": \"data\"" +
                                                    "    }" +
                                                    "}";
    //Has a different attribute order
    public static final String LEGACY_JSON = "{\"new_name\":\"foobar\",\"empty\":\"\",\"value\":42,\"specialInt\":\"24\"}";
    public static final String INVALID_JSON = "{\"name\":\"foobar\",\"empty\":\"\",\"value\":42}";

    private static final ObjectMapper mapperWithExtention = new ObjectMapper().setAnnotationIntrospector(new JacksonLombokAnnotationIntrospector());

    private static final ImmutablePojo multiArgPojo = new ImmutablePojo("foobar", "", 42, 25);

    private static final ImmutablePojoSingle singleArgPojo = new ImmutablePojoSingle("data");

    private static final NestedImmutablePojoSingle nestedSingleArgPojo = new NestedImmutablePojoSingle(new Data("data"));
    private final Object data;
    private final String json;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{ { multiArgPojo, JSON },
                                             { new LegacyPojo("foobar", "", 42, 25), LEGACY_JSON },
                                             { singleArgPojo, SINGLE_JSON },
                                             { nestedSingleArgPojo, NESTED_SINGLE_JSON } });
    }

    public JacksonLombokAnnotationIntrospectorTest(Object data, String json) {
        this.data = data;
        this.json = json;
    }

    @Test
    public void deserializes() throws IOException {
        final Object o = mapperWithExtention.readValue(json, data.getClass());

        assertThat(o, is(data));
    }

    @Test
    public void serializes() throws IOException {
        String serialized = mapperWithExtention.writeValueAsString(data);

        assertThat(serialized, is(equalToIgnoringWhiteSpace(json.replace(" ", ""))));
    }
}
