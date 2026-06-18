package model;

import java.io.Serializable;
import java.util.Objects;

public class Pies implements Serializable {
    private static final long serialVersionUID = 1L;
    private String rasa;
    private double waga;

    public Pies(String rasa, double waga) {
        this.rasa = rasa;
        this.waga = waga;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pies pies = (Pies) o;
        return Double.compare(pies.waga, waga) == 0 && Objects.equals(rasa, pies.rasa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rasa, waga);
    }

    @Override
    public String toString() {
        return "Pies{rasa='" + rasa + "', waga=" + waga + "}";
    }
}