package cz.vsb.checkers.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SaveGameResultRequest(
        @NotBlank String whitePlayerName,
        @NotBlank String blackPlayerName,
        @NotBlank String winnerName,
        @Min(0) int totalMoves,
        @Min(0) long gameDurationMillis
) {
}
