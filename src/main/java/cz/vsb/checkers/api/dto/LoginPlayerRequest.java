package cz.vsb.checkers.api.dto;

import jakarta.validation.constraints.NotBlank;
//ne prazdne = chyba
public record LoginPlayerRequest(@NotBlank String name) {
}
