package com.razzzil.jsonbean.testmodule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.razzzil.jsonbean.validation.annotation.JsonSchemed;
import com.razzzil.jsonbean.validation.constant.SchemaVersion;
import com.razzzil.jsonbean.validation.model.jsonschema.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonSchemed(schema = SchemaVersion.DRAFT_6,
        description = "Test Form with all variants of usage",
        title = "ValidationTestForm")
public class ValidationTestForm {

    public static final String LOCAL_DATE_TIME_FORMAT = "dd-MM-yyyy'T'HH:mm:ss";

   // @NotNull(message = "validation.ValidationTestForm.testEmail.NotNull")
    @NotBlank(message = "validation.ValidationTestForm.testEmail.NotBlank")
    @Email(regexp = "^[a-zA-Z0-9._%+-]{2,}@[a-zA-Z0-9.-]{2,}\\.[a-z]{2,}$", message = "validation.ValidationTestForm.testEmail.Email")
    @Size(min = 5, max = 10, message = "validation.ValidationTestForm.testEmail.Size")
    private String testEmail;

    @NotNull(message = "validation.ValidationTestForm.testPositiveInteger.NotNull")
    @Positive(message = "validation.ValidationTestForm.testPositiveInteger.Positive")
    @Min(value = 1, message = "validation.ValidationTestForm.testPositiveInteger.Min")
    @Max(value = 10, message = "validation.ValidationTestForm.testPositiveInteger.Max")
    private Integer testPositiveInteger;

    @NotNull(message = "validation.ValidationTestForm.testPositiveOrZeroInteger.NotNull")
    @PositiveOrZero(message = "validation.ValidationTestForm.testPositiveOrZeroInteger.PositiveOrZero")
    private Integer testPositiveOrZeroInteger;

    @NotNull(message = "validation.ValidationTestForm.testNegativeInteger.NotNull")
    @Negative(message = "validation.ValidationTestForm.testNegativeInteger.Negative")
    private Integer testNegativeInteger;

    @NotNull(message = "validation.ValidationTestForm.testNegativeOrZeroInteger.NotNull")
    @NegativeOrZero(message = "validation.ValidationTestForm.testNegativeOrZeroInteger.NegativeOrZero")
    private Integer testNegativeOrZeroInteger;

    @NotNull(message = "validation.ValidationTestForm.testDigitsString.NotNull")
    @Digits(integer = 2, fraction = 2, message = "validation.ValidationTestForm.testDigitsString.Digits")
    @DecimalMin(value = "1", inclusive = true, message = "validation.ValidationTestForm.testDigitsString.DecimalMin")
    @DecimalMax(value = "10", inclusive = false, message = "validation.ValidationTestForm.testDigitsString.DecimalMax")
    private String testDigitsString;

    @NotNull(message = "validation.ValidationTestForm.testPast.NotNull")
    @Past(message = "validation.ValidationTestForm.testPast.Past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime testPast;

    @NotNull(message = "validation.ValidationTestForm.testPastOrPresent.NotNull")
    @Past(message = "validation.ValidationTestForm.testPast.testPastOrPresent")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime testPastOrPresent;

    @NotNull(message = "validation.ValidationTestForm.testFuture.NotNull")
    @Future(message = "validation.ValidationTestForm.testFuture.Future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime testFuture;

    @NotNull(message = "validation.ValidationTestForm.testFutureOrPresent.NotNull")
    @FutureOrPresent(message = "validation.ValidationTestForm.testFutureOrPresent.FutureOrPresent")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime testFutureOrPresent;

    @NotNull(message = "validation.ValidationTestForm.testListString.NotNull")
    @NotEmpty(message = "validation.ValidationTestForm.testListString.NotEmpty")
    @Size(min = 5, max = 10, message = "validation.ValidationTestForm.testListString.Size")
    private List<String> testListString;

    @Null(message = "validation.ValidationTestForm.nullableString.Null")
    private String nullableString;

    @NotNull(message = "validation.ValidationTestForm.patternString.NotNull")
    @NotBlank(message = "validation.ValidationTestForm.patternString.NotBlank")
    @Pattern(regexp = "^((?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[.#?!@$%^&*-]).{8,}$)|null", message = "validation.ValidationTestForm.patternString.Pattern")
    private String patternString;

    @NotNull(message = "validation.ValidationTestForm.testAssertTrue.NotNull")
    @AssertTrue(message = "validation.ValidationTestForm.testAssertTrue.AssertTrue")
    private Boolean testAssertTrue;

    @NotNull(message = "validation.ValidationTestForm.testAssertFalse.NotNull")
    @AssertFalse(message = "validation.ValidationTestForm.testAssertFalse.AssertFalse")
    private Boolean testAssertFalse;

    private Type type;

    private PersonDto personDto;
}
