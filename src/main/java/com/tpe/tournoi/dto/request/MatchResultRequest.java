package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.*;

public class MatchResultRequest {

    @NotNull(message = "Le score domicile est obligatoire")
    @Min(value = 0, message = "Le score domicile ne peut pas être négatif")
    @Max(value = 99, message = "Le score domicile ne peut pas dépasser 99")
    private Integer scoreDomicile;

    @NotNull(message = "Le score visiteur est obligatoire")
    @Min(value = 0, message = "Le score visiteur ne peut pas être négatif")
    @Max(value = 99, message = "Le score visiteur ne peut pas dépasser 99")
    private Integer scoreVisiteur;

    public MatchResultRequest() {
    }

    public MatchResultRequest(Integer scoreDomicile, Integer scoreVisiteur) {
        this.scoreDomicile = scoreDomicile;
        this.scoreVisiteur = scoreVisiteur;
    }

    public Integer getScoreDomicile() {
        return scoreDomicile;
    }

    public void setScoreDomicile(Integer scoreDomicile) {
        this.scoreDomicile = scoreDomicile;
    }

    public Integer getScoreVisiteur() {
        return scoreVisiteur;
    }

    public void setScoreVisiteur(Integer scoreVisiteur) {
        this.scoreVisiteur = scoreVisiteur;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer scoreDomicile;
        private Integer scoreVisiteur;

        public Builder scoreDomicile(Integer scoreDomicile) {
            this.scoreDomicile = scoreDomicile;
            return this;
        }

        public Builder scoreVisiteur(Integer scoreVisiteur) {
            this.scoreVisiteur = scoreVisiteur;
            return this;
        }

        public MatchResultRequest build() {
            return new MatchResultRequest(scoreDomicile, scoreVisiteur);
        }
    }
}
