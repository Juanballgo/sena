package com.sena.dto;

import com.sena.model.Semillero;
import com.sena.model.Usuario;
import java.util.List;

public class SemilleroConUsuariosDTO {
    private Semillero semilleroInfo;
    private List<Usuario> usuariosAsignados;

    public SemilleroConUsuariosDTO(Semillero semilleroInfo, List<Usuario> usuariosAsignados) {
        this.semilleroInfo = semilleroInfo;
        this.usuariosAsignados = usuariosAsignados;
    }

    public Semillero getSemilleroInfo() { return semilleroInfo; }
    public List<Usuario> getUsuariosAsignados() { return usuariosAsignados; }
}