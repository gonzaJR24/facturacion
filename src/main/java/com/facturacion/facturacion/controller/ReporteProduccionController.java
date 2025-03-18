package com.facturacion.facturacion.controller;

import com.facturacion.facturacion.models.ReporteProduccion;
import com.facturacion.facturacion.service.ReporteProduccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produccion")
public class ReporteProduccionController {

    @Autowired
    private ReporteProduccionService service;

    @GetMapping
    public List<ReporteProduccion> list(){
        return service.list();
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody ReporteProduccion reporteProduccion){
        service.save(reporteProduccion);
        return ResponseEntity.status(201).body(reporteProduccion);
    }

    @GetMapping("/find/{id}")
    public ReporteProduccion findById(@PathVariable int id){
        return service.findById(id);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable int id, ReporteProduccion reporteProduccion){
        service.edit(id, reporteProduccion);
        return ResponseEntity.status(204).body("modified element");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable int id){
        service.delete(id);
        return ResponseEntity.status(200).body("deleted successfully");
    }

    @DeleteMapping("/deleteAllElements")
    public ResponseEntity<?>deleteAll(){
        service.deleteAll();
        return ResponseEntity.status(200).body("all elements removed");
    }

}
