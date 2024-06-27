package com.example.maestrodelamultiplicacion_v2.ui.gallery;

public class Estadisticas {

    String fechaPartida;
    String tablaJugada;
    String infoPartida;

    public Estadisticas() {
    }

    public Estadisticas(String fechaPartida, String tablaJugada, String infoPartida) {
        this.fechaPartida = fechaPartida;
        this.tablaJugada = tablaJugada;
        this.infoPartida = infoPartida;
    }

    public String getFechaPartida() {
        return fechaPartida;
    }

    public void setFechaPartida(String fechaPartida) {
        this.fechaPartida = fechaPartida;
    }

    public String getTablaJugada() {
        return tablaJugada;
    }

    public void setTablaJugada(String tablaJugada) {
        this.tablaJugada = tablaJugada;
    }

    public String getInfoPartida() {
        return infoPartida;
    }

    public void setInfoPartida(String infoPartida) {
        this.infoPartida = infoPartida;
    }
}
