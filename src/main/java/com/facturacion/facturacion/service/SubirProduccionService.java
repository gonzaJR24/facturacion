package com.facturacion.facturacion.service;

import com.facturacion.facturacion.models.ReporteProduccion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class SubirProduccionService {

    @Autowired
    private ReporteProduccionService reporteProduccionService;

    // Configuración de procesamiento
    private static final int BATCH_SIZE = 1000;
    private static final String[] CONVENIOS_VALIDOS = {"P&P HTA", "P&P HTA + DM", "P&P DM"};

    public ResponseEntity<?> generate(MultipartFile file) {
        List<ReporteProduccion> batch = new ArrayList<>(BATCH_SIZE);
        int totalProcessed = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // Mapeo dinámico de columnas
            Map<String, Integer> columnMap = buildColumnMap(headerRow);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Saltar cabecera

                ReporteProduccion entity = processRow(row, columnMap);
                if (entity != null) {
                    batch.add(entity);
                    totalProcessed++;

                    // Procesamiento por lotes
                    if (batch.size() >= BATCH_SIZE) {
                        saveBatch(batch);
                        batch.clear();
                    }
                }
            }

            // Guardar último lote
            if (!batch.isEmpty()) {
                saveBatch(batch);
            }

            return ResponseEntity.ok("Procesados " + totalProcessed + " registros");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error en registro #" + (totalProcessed + 1) + ": " + e.getMessage());
        }
    }

    private void saveBatch(List<ReporteProduccion> batch) {
        // Versión optimizada para tu Repository
        reporteProduccionService.getRepository().saveAll(batch); // Acceso directo al repository
        System.gc(); // Sugerencia de GC después de cada lote
    }

    private Map<String, Integer> buildColumnMap(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : headerRow) {
            map.put(cell.getStringCellValue().toLowerCase(), cell.getColumnIndex());
        }
        return map;
    }

    private ReporteProduccion processRow(Row row, Map<String, Integer> columnMap) {
        try {
            String convenio = getCellValue(row, columnMap.get("convenio"));
            String empresa = getCellValue(row, columnMap.get("empresa"));

            if (isValid(convenio, empresa)) {
                ReporteProduccion entity = new ReporteProduccion();
                entity.setNombre(String.join(" ",
                        getCellValue(row, columnMap.get("nombres")),
                        getCellValue(row, columnMap.get("paterno")),
                        getCellValue(row, columnMap.get("materno"))
                ));
                entity.setProveedor(getCellValue(row, columnMap.get("proveedor")));
                entity.setEmpresa(empresa);
                return entity;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getCellValue(Row row, Integer colIndex) {
        if (colIndex == null) return "";
        Cell cell = row.getCell(colIndex);
        return cell != null ? cell.toString() : "";
    }

    private boolean isValid(String convenio, String empresa) {
        return convenio != null && convenio.toUpperCase().contains("SENASA") &&
                empresa != null && Arrays.asList(CONVENIOS_VALIDOS).contains(empresa);
    }
}