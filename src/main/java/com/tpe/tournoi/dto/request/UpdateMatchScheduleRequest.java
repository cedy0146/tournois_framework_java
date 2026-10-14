package com.tpe.tournoi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateMatchScheduleRequest {

    @NotNull(message = "La date est obligatoire")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @NotNull(message = "L'heure est obligatoire")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime heure;

    @NotBlank(message = "Le terrain est obligatoire")
    private String terrain;

    public UpdateMatchScheduleRequest() {
    }

    public UpdateMatchScheduleRequest(LocalDate date, LocalTime heure, String terrain) {
        this.date = date;
        this.heure = heure;
        this.terrain = terrain;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }
}
