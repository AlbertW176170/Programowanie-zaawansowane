package model;

import java.io.Serializable;
import java.util.Objects;

public class Kot implements Serializable {
    private static final long serialVersionUID = 1L;
    private String imie;
    private int wiek;

    public Kot(String imie, int wiek) {
        this.imie = imie;
        this.wiek = wiek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kot kot = (Kot) o;
        return wiek == kot.wiek && Objects.equals(imie, kot.imie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imie, wiek);
    }

    @Override
    public String toString() {
        return "Kot{imie='" + imie + "', wiek=" + wiek + "}";
    }
}