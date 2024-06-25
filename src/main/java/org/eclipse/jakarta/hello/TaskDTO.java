package org.eclipse.jakarta.hello;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record TaskDTO(
        @NotEmpty(message = "The URL field is mandatory and must not be empty.")
        @Pattern(regexp = "^(http|https)://.*$", message = "The input provided is not a valid URL. Please provide a valid URL.")
        String url
) {}