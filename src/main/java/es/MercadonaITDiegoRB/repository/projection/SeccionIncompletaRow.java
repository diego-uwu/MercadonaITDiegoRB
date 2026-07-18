package es.MercadonaITDiegoRB.repository.projection;

public interface SeccionIncompletaRow {

    String getSeccion();

    Integer getHorasNecesarias();

    Integer getHorasAsignadas();

    Integer getHorasRestantes();
}
