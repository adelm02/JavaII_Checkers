package cz.vsb.checkers.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginPlayerRequest(@NotBlank String name) {
}
