package com.ecocraft.controller;

import com.ecocraft.entity.Producto;
import com.ecocraft.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El producto con ID " + id + " no existe"));
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Producto> listarPorCategoria(@PathVariable String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        Producto nuevo = productoRepository.save(producto);
        return ResponseEntity.status(201).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar. El producto con ID " + id + " no existe"));

        existente.setNombre(producto.getNombre());
        existente.setDescripcion(producto.getDescripcion());
        existente.setPrecio(producto.getPrecio());
        existente.setStock(producto.getStock());
        existente.setCategoria(producto.getCategoria());
        existente.setImagenUrl(producto.getImagenUrl());

        return ResponseEntity.ok(productoRepository.save(existente));
    }

    @PatchMapping("/{id}/descontar-stock")
    public ResponseEntity<Producto> descontarStock(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer cantidad = payload.getOrDefault("cantidad", 1);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El producto con ID " + id + " no existe"));

        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Stock actual: " + producto.getStock());
        }

        producto.setStock(producto.getStock() - cantidad);
        return ResponseEntity.ok(productoRepository.save(producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar. El producto con ID " + id + " no existe"));

        productoRepository.delete(producto);
        return ResponseEntity.ok(Map.of("mensaje", "Producto '" + producto.getNombre() + "' eliminado correctamente"));
    }
}