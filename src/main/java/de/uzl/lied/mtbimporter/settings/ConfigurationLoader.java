package de.uzl.lied.mtbimporter.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;

/**
 * Class that enables the use of environment variables in the settings.yaml file.
 */
public class ConfigurationLoader {
    private final ObjectMapper objectMapper;
    private final StringSubstitutor stringSubstitutor;

    public ConfigurationLoader() {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.stringSubstitutor = new StringSubstitutor(StringLookupFactory.INSTANCE.environmentVariableStringLookup());
    }

    /**
     *
     * @param <T> POJO representing the yaml file.
     * @param config stream of config file.
     * @param cls Class of POJO representing the yaml file.
     * @return Initalized object of POJO with values from yaml and environment
     */
    public <T> T loadConfiguration(InputStream config, Class<T> cls) {
        try {
            String contents = this.stringSubstitutor
                    .replace(new String(ByteStreams.toByteArray(config), StandardCharsets.UTF_8));

            return this.objectMapper.readValue(contents, cls);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
