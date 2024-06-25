package org.eclipse.jakarta.hello;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class TaskDTO {
    @NotEmpty(message = "The URL field is mandatory and must not be empty.")
    @Pattern(regexp = "^(http|https)://.*$", message = "The input provided is not a valid URL. Please provide a valid URL.")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
