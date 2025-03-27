package com.facturacion.facturacion.service;

import com.facturacion.facturacion.models.ReporteProduccion;
import com.facturacion.facturacion.repository.ReporteProduccionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteProduccionService {

    @Autowired
    private ReporteProduccionRepository repository;

    public void save(ReporteProduccion reporteProduccion){
        repository.save(reporteProduccion);
    }

    public void delete(int id){
        repository.deleteById(id);
    }

    public void deleteAll(){
        repository.deleteAll();
    }

    public List<ReporteProduccion>list(){
        return repository.findAll();
    }

    public ReporteProduccion findById(int id){
        return repository.findById(id).orElseThrow(()->new RuntimeException("ID:"+id+" no se encuentra"));
    }

    public void edit(int id, ReporteProduccion reporteProduccion){
        ReporteProduccion existente=findById(id);
        BeanUtils.copyProperties(reporteProduccion, existente, "id");
        repository.save(existente);
    }

    public ReporteProduccionRepository getRepository() {
        return this.repository;
    }

}
