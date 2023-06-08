package es.uma.smartmeter.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Medicion {
    private double intensidad;
    private double voltaje;
    private double potencia;
    private Date fecha;
    private final String fechaString;

    public Medicion(double intensidad, double voltaje){
        this.fechaString=new SimpleDateFormat("MM/DD/YY HH:MM:SS").format(this.fecha);
    }
    public Medicion(double potencia, String fecha){
        this.potencia=potencia;
        this.fechaString=fecha;
    }
}
