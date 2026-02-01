package com.group9.backend.controller;

import java.util.Collection;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group9.backend.model.Tables.Platform;
import com.group9.backend.persistence.PlatformDao;

@Controller
@RequestMapping("/platform")
public class PlatformController extends AbstractController {
    
    @Autowired
    private PlatformDao dao;
    
    @PostMapping
    public ResponseEntity<Platform> createPlatform(@RequestBody @NonNull Platform platform) {
        LOGGER.log(Level.INFO, "POST /platform {0}", platform);
        return handleCreate(() -> dao.save(platform));
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Platform> getPlatform(@PathVariable long id) {
        LOGGER.log(Level.INFO, "GET /platform/{0}", id);
        return handleGet(() -> dao.get(id));
    }

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Collection<Platform>> getAll() {
        LOGGER.log(Level.INFO, "GET all /platform");
        return handleCollection(() -> dao.getAll());
    }
    
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Collection<Platform>> getPlatformsByEmail(@RequestParam("name") @NonNull String name) {
        LOGGER.log(Level.INFO, "GET /platform/search?name={0}", name);
        return handleCollection(() -> dao.getByName(name));
    }
    
    
    @PutMapping
    public ResponseEntity<Void> updatePlatform(@RequestBody @NonNull Platform platform) {
        LOGGER.log(Level.INFO, "PUT /platform {0}", platform);
        return handleUpdate(() -> dao.update(platform));
    }
    
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> deletePlatform(@RequestBody @NonNull Platform platform) {
        LOGGER.log(Level.INFO, "DELETE /platform {0}", platform);
        return handleUpdate(() -> dao.delete(platform));
    }
}
