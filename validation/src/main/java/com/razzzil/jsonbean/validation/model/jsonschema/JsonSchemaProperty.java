package com.razzzil.jsonbean.validation.model.jsonschema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import static com.razzzil.jsonbean.validation.constant.SchemaConstants.DESCRIPTION_DELIMITER;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public abstract class JsonSchemaProperty {

    @JsonIgnore
    private Optional<StringJoiner> stringBuilderCandidate = Optional.empty();

    @JsonProperty("type")
    protected abstract Type getType();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("$ref")
    private String refs;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getDescription() {
        return stringBuilderCandidate
                .map(StringJoiner::toString)
                .orElse(null);
    }

    public void setDescription(String description) {
        this.stringBuilderCandidate = Optional.of(new StringJoiner(DESCRIPTION_DELIMITER).add(description));
    }

    public void appendDescription(String description) {
        if (this.stringBuilderCandidate.isPresent()) {
            this.stringBuilderCandidate.get().add(description);
        } else {
            this.stringBuilderCandidate = Optional.of(new StringJoiner(DESCRIPTION_DELIMITER).add(description));
        }
    }


}
