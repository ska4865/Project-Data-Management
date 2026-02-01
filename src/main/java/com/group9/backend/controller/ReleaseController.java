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
import org.springframework.web.bind.annotation.ResponseBody;

import com.group9.backend.model.Tables.Release;
import com.group9.backend.persistence.ReleaseDao;

@Controller
@RequestMapping("/release")
public class ReleaseController extends AbstractController {
    
    @Autowired
    private ReleaseDao dao;
    
    @PostMapping
    public ResponseEntity<Release> createRelease(@RequestBody @NonNull Release release) {
        LOGGER.log(Level.INFO, "POST /release {0}", release);
        return handleCreate(() -> dao.save(release));
    }
    
    
    @GetMapping("/{vID}/{pID}")
    @ResponseBody
    public ResponseEntity<Release> getRelease(@PathVariable long vID,@PathVariable long pID) {
        LOGGER.log(Level.INFO, "GET /release/{0}/{1}", new Object[]{vID,pID});
        return handleGet(() -> dao.get(vID, pID));
    }

    @GetMapping("/platform/{pID}")
    @ResponseBody
    public ResponseEntity<Collection<Release>> getReleasesByPlatform(@PathVariable long pID) {
        LOGGER.log(Level.INFO, "GET /release/platform/{0}", pID);
        return handleCollection(() -> dao.getByPlatform(pID));
    }


    @GetMapping("/videoGame/{vID}")
    @ResponseBody
    public ResponseEntity<Collection<Release>> getReleasesByVideoGame(@PathVariable long vID) {
        LOGGER.log(Level.INFO, "GET /release/videoGame/{0}", vID);
        return handleCollection(() -> dao.getByGame(vID));
    }

    
    
    @PutMapping
    public ResponseEntity<Void> updateRelease(@RequestBody @NonNull Release release) {
        LOGGER.log(Level.INFO, "PUT /release {0}", release);
        return handleUpdate(() -> dao.update(release));
    }
    
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> deleteRelease(@RequestBody @NonNull Release release) {
        LOGGER.log(Level.INFO, "DELETE /release {0}", release);
        return handleUpdate(() -> dao.delete(release));
    }
}
