package com.example.maestrodelamultiplicacion_v2.ui.gallery;

public class Multiplicacion {

    int factor1;
    int factor2;
    int resultado;

    //Constructor
    public Multiplicacion() {
    }

    public Multiplicacion(int factor1, int factor2) {
        this.factor1 = factor1;
        this.factor2 = factor2;
    }

    public Multiplicacion(int factor1, int factor2, int resultado) {
        this.factor1 = factor1;
        this.factor2 = factor2;
        this.resultado = resultado;
    }

    //Getter y setter

    public int getFactor1() {
        return factor1;
    }

    public void setFactor1(int factor1) {
        this.factor1 = factor1;
    }

    public int getFactor2() {
        return factor2;
    }

    public void setFactor2(int factor2) {
        this.factor2 = factor2;
    }

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }
}
