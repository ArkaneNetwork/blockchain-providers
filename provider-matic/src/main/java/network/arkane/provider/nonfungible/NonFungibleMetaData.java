package network.arkane.provider.nonfungible;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Data
public class NonFungibleMetaData {
    private JsonNode properties;

    public String getProperty(String propertyName) {
        return properties.has(propertyName)
               ? (properties.get(propertyName).isContainerNode() ? properties.get(propertyName).toString() : properties.get(propertyName).asText())
               : null;
    }

    public void setProperty(String propertyName,
                            String value) {
        ((ObjectNode) properties).put(propertyName, value);
    }

    public String getName() {
        return getProperty("name");
    }

    public String getDescription() {
        return getProperty("description");
    }

    public String getImage() {
        return getProperty("image");
    }

    public Optional<String> getBackgroundColor() {
        return Stream.of(
                getProperty("backgroundColor"),
                getProperty("background_color")
                        ).filter(StringUtils::isNotBlank)
                     .findFirst();
    }

    public Optional<String> getAnimationUrl() {
        return Stream.of(
                getProperty("animationUrl"),
                getProperty("animation_url")
                        ).filter(StringUtils::isNotBlank)
                     .findFirst();
    }

    public Optional<String> getExternalUrl() {
        return Stream.of(
                getProperty("externalUrl"),
                getProperty("external_url")
                        ).filter(StringUtils::isNotBlank)
                     .findFirst();
    }

    public Optional<Long> getTokenTypeId() {
        return Optional.ofNullable(getProperty("tokenTypeId")).map(Long::parseLong);
    }

    public void setName(String name) {
        setProperty("name", name);
        ;
    }

    public void setDescription(String description) {
        setProperty("description", description);
    }

    public void setImage(String image) {
        setProperty("image", image);
    }
}
