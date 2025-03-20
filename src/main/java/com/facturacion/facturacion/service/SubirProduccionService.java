package com.facturacion.facturacion.service;

import com.facturacion.facturacion.models.ReporteProduccion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SubirProduccionService {

    @Autowired
    private ReporteProduccionService reporteProduccionService;

    public Cell copagoCell;
    public Cell convenioCell;
    public Cell empresaCell;
    public Cell grupoCell;
    public Cell nombreCell;
    public Cell facturadoCell;
    public Cell referenciaCell;
    public Cell descripcionCell;

    public boolean isValidCopago(){
        return (copagoCell != null && copagoCell.getCellType() == CellType.NUMERIC && copagoCell.getNumericCellValue()==0);
    }

    public boolean isValidReferencia(){
        return (referenciaCell != null && referenciaCell.getCellType() == CellType.NUMERIC && referenciaCell.getNumericCellValue()==0);
    }

    public boolean isValidConvenio(){
        if (convenioCell!= null && convenioCell.getCellType() == CellType.STRING && convenioCell.getStringCellValue().contains("senasa".toUpperCase())) {
            return true;
        }
        return false;
    }

    private String[] conveniosSenana={"P&P HTA","P&P HTA + DM","P&P DM"};
    public boolean isValidEmpresa(){
        return empresaCell != null && empresaCell.getCellType() == CellType.STRING && (Arrays.asList(conveniosSenana).contains(empresaCell.getStringCellValue()));
    }

    private String[] grupos={"CONSULTAS AMBULATORIAS","LABORATORIO CLINICO","PROCEDIMIENTOS"};
    public boolean isValidGrupo(){
        return grupoCell!= null && grupoCell.getCellType() == CellType.STRING && Arrays.asList(grupos).contains(grupoCell.getStringCellValue());
    }


    private String[] procedimientosValidos={"ELECTROCARDIOGRAMA", "PRENATAL"};
    public boolean isValidProcedimiento(){
        if (grupoCell.getStringCellValue().equals("PROCEDIMIENTOS") && Arrays.asList(procedimientosValidos).contains( descripcionCell.getStringCellValue()) &&
                facturadoCell.getNumericCellValue()==270 && isValidCopago()) {
            return true;
        }
        return false;
    }


    public boolean isValidGrupoConsulta(){
        if (grupoCell.getStringCellValue().equals("CONSULTAS AMBULATORIAS") && facturadoCell.getNumericCellValue()==400 && isValidCopago()) {
            return true;
        }
        return false;
    }

    public boolean isValiReferencia(){
        if (referenciaCell!= null && referenciaCell.getCellType() == CellType.NUMERIC && referenciaCell.getNumericCellValue()==0) {
            return true;
        }
        return false;
    }

    public boolean isValidDescripcion(){
        if (descripcionCell!= null && descripcionCell.getCellType() == CellType.NUMERIC && descripcionCell.getNumericCellValue()==0) {
            return true;
        }
        return false;
    }

    //Generate
    public ResponseEntity<?> generate(MultipartFile file, int mes, int anio) {

        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                ReporteProduccion entity = new ReporteProduccion();

                for (Cell cell : sheet.getRow(0)) {
                    switch (cell.getStringCellValue().toLowerCase()) {
                        case "copago":
                            copagoCell = row.getCell(cell.getColumnIndex());
                        case "convenio":
                            convenioCell = row.getCell(cell.getColumnIndex());
                        case "empresa":
                            empresaCell = row.getCell(cell.getColumnIndex());
                        case "grupo":
                            grupoCell = row.getCell(cell.getColumnIndex());
                        case "nombre":
                            nombreCell = row.getCell(cell.getColumnIndex());
                        case "facturado":
                            facturadoCell = row.getCell(cell.getColumnIndex());
                        case "referencia":
                            referenciaCell = row.getCell(cell.getColumnIndex());
                        case "descripcion":
                            descripcionCell = row.getCell(cell.getColumnIndex());
                    }
                }

                if(isValidCopago() && isValidConvenio() && isValidConvenio() && isValidEmpresa() && isValidGrupo() && isValidProcedimiento() && isValidGrupoConsulta()
                && isValiReferencia() && isValidDescripcion()){
                    reporteProduccionService.save(entity);
                }
            }
        } catch (OutOfMemoryError e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(reporteProduccionService.list());
    }
}