package com.gbtec.emailmanager.ws.core.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.gbtec.emailmanager.api.core.service.ICrudService;

/**
 * Controlador REST base con operaciones CRUD genéricas.
 * <p>
 *   <b>POST</b>   {@code /api/[entities]}        → insert <br>
 *   <b>PUT</b>    {@code /api/[entities]/[id]}   → update <br>
 *   <b>DELETE</b> {@code /api/[entities]/[id]}   → delete <br>
 *   <b>GET</b>    {@code /api/[entities]}        → listAll <br>
 *   <b>GET</b>    {@code /api/[entities]/[id]}   → findById <br>
 */   
public abstract class AbstractCrudController<S extends ICrudService<Integer>> {

    protected abstract S getService();

    /**
     * GET / — lista todos los registros (REST puro, sin filtro).
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> findAll() {
        return ResponseEntity.ok(getService().query(Map.of(), List.of()));
    }

    /**
     * GET /{id} — obtiene un registro por su PK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Integer id,
                                                         @RequestParam String pkColumn) {
        List<Map<String, Object>> rows = getService().query(Map.of(pkColumn, id), List.of());
        if (rows.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(rows.get(0));
    }

    /**
     * POST / — inserta un nuevo registro.
     * Body: mapa de atributos.
     *
     * Aquí aceptamos el objeto directamente.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> insert(@RequestBody Map<String, Object> attributes) {
        return ResponseEntity.ok(getService().insert(attributes));
    }

    /**
     * PUT /{id} — actualiza un registro.
     *
     * Aquí usamos la PK en la URL.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id,
                                                       @RequestBody Map<String, Object> attributes) {
        return ResponseEntity.ok(getService().update(id, attributes));
    }

    /**
     * DELETE /{id} — elimina un registro.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        getService().delete(id);
        return ResponseEntity.noContent().build();
    }
}
