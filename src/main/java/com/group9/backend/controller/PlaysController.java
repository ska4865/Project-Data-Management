package com.group9.backend.controller;

import java.util.Collection;
import java.util.logging.Level;

import org.postgresql.util.PGInterval;
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

import com.group9.backend.model.Tables.Plays;
import com.group9.backend.persistence.PlaysDao;

@Controller
@RequestMapping("/plays")
public class PlaysController extends AbstractController {
    
    @Autowired
    private PlaysDao dao;
    
    
    @PostMapping("/{userId}/{videoGameId}/start")
    public ResponseEntity<Plays> start(@PathVariable long userId, @PathVariable long videoGameId) {
        LOGGER.log(Level.INFO, "POST /plays/{0}/{1}", new Object[]{userId, videoGameId});
        return handleCreate(() -> dao.start(userId, videoGameId));
    }
    
    
    @PostMapping
    @ResponseBody
    public ResponseEntity<Plays> createPlays(@RequestBody @NonNull Plays plays) {
        LOGGER.log(Level.INFO, "POST /plays {0}", plays);
        return handleCreate(() -> dao.save(plays));
    }
    
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Plays> getPlayss(@PathVariable long id) {
        LOGGER.log(Level.INFO, "GET /plays/{0}", id);
        return handleGet(() -> dao.get(id));
    }

    @GetMapping("/user/{uID}")
    @ResponseBody
    public ResponseEntity<Collection<Plays>> getPlayssByUser(@PathVariable long uID) {
        LOGGER.log(Level.INFO, "GET /plays/user/{0}", uID);
        return handleCollection(() -> dao.getByUser(uID));
    }

    @GetMapping("/playTime/{uID}/{vID}")
    @ResponseBody
    public ResponseEntity<PGInterval> getUserAvg(@PathVariable long uID,@PathVariable long vID) {
        LOGGER.log(Level.INFO, "GET /plays/user/playTime/{0}/{1}", new Object[]{uID,vID});
        return handleGet(() -> dao.getPlayTime(uID, vID));
    }

    @GetMapping("/videoGame/{vID}")
    @ResponseBody
    public ResponseEntity<Collection<Plays>> getPlayssByVideoGame(@PathVariable long vID) {
        LOGGER.log(Level.INFO, "GET /plays/videoGame/{0}", vID);
        return handleCollection(() -> dao.getByGame(vID));
    }

    
    @PutMapping
    public ResponseEntity<Void> updatePlays(@RequestBody @NonNull Plays plays) {
        LOGGER.log(Level.INFO, "PUT /plays {0}", plays);
        return handleUpdate(() -> dao.update(plays));
    }
    
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> deletePlays(@RequestBody @NonNull Plays plays) {
        LOGGER.log(Level.INFO, "DELETE /plays {0}", plays);
        return handleUpdate(() -> dao.delete(plays));
    }
}
