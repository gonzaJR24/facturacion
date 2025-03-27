package com.facturacion.facturacion.controller;

import com.facturacion.facturacion.service.SubirProduccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class SubirProduccionController {
    @Autowired
    private SubirProduccionService produccionService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file){
        produccionService.generate(file);
        return ResponseEntity.status(201).body("object created successfully");
    }
}
