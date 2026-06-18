package model;

import java.io.Serializable;
import java.util.Objects;

public class Samochod implements Serializable {
    private static final long serialVersionUID = 1L;
    private String marka;
    private int rokProdukcji;

    public Samochod(String marka, int rokProdukcji) {
        this.marka = marka;
        this.rokProdukcji = rokProdukcji;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Samochod samochod = (Samochod) o;
        return rokProdukcji == samochod.rokProdukcji && Objects.equals(marka, samochod.marka);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marka, rokProdukcji);
    }

    @Override
    public String toString() {
        return "Samochod{marka='" + marka + "', rok=" + rokProdukcji + "}";
    }
}