package com.group9.backend.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group9.backend.model.Tables.Collection;
import com.group9.backend.model.Tables.CollectionGames;
import com.group9.backend.persistence.CollectionDao;
import com.group9.backend.persistence.CollectionGamesDao;


@Controller
@RequestMapping("/collection")
public class CollectionController extends AbstractController {
    
    @Autowired
    private CollectionDao dao;
    @Autowired
    private CollectionGamesDao cgDao;
    
    @PostMapping
    @ResponseBody
    public ResponseEntity<Collection> createCollection(@RequestBody @NonNull Collection col) {
        LOGGER.log(Level.INFO, "POST /collection {0}", col);
        return handleCreate(() -> dao.save(col));
    }
    
    @PutMapping
    public ResponseEntity<Void> updateCollection(@RequestBody @NonNull Collection col) {
        LOGGER.log(Level.INFO, "PUT /collection {0}", col);
        return handleUpdate(() -> dao.update(col));
    }
    
    @GetMapping("/{userId}")
    @ResponseBody
    public ResponseEntity<java.util.Collection<Collection>> getUserCollection(@PathVariable long userId) {
        LOGGER.log(Level.INFO, "GET /collection/{0}", userId);
        return handleCollection(() -> dao.getByUser(userId));
    }
    
    @GetMapping("/{userId}/{collectionId}")
    @ResponseBody
    public ResponseEntity<Collection> getCollection(@PathVariable long userId, @PathVariable long collectionId) {
        LOGGER.log(Level.INFO, "GET /collection/{0}/{1}", new Object[]{userId, collectionId});
        return handleGet(() -> dao.get(userId, collectionId));
    }
    
    @GetMapping("/{userId}/{collectionId}/totalPlaytime")
    @ResponseBody
    public ResponseEntity<PGInterval> getTotalPlaytime(@PathVariable long userId, @PathVariable long collectionId) {
        LOGGER.log(Level.INFO, "GET /collection/{0}/{1}/totalPlaytime", new Object[]{userId, collectionId});
        return handleGet(() -> cgDao.getTotalPlaytime(userId, collectionId));
    }
    
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> deleteCollection(@RequestBody @NonNull Collection col) {
        LOGGER.log(Level.INFO, "DELETE /collection {0}", col);
        return handleUpdate(() -> dao.delete(col));
    }
    
    @GetMapping("/{userId}/{collectionId}/games")
    @ResponseBody
    public ResponseEntity<java.util.Collection<CollectionGames>> getGames(@PathVariable long userId, @PathVariable long collectionId) {
        LOGGER.log(Level.INFO, "GET /{0}/{1}", new Object[]{userId, collectionId});
        return handleCollection(() -> cgDao.getByCollection(userId, collectionId));
    }
    
    @PostMapping("/{userId}/{collectionId}/games")
    public ResponseEntity<CollectionGames> addGame(@PathVariable long userId, @PathVariable long collectionId, @RequestParam long gameId) {
        LOGGER.log(Level.INFO, "POST /{0}/{1}/games?gameId={2}", new Object[]{userId, collectionId, gameId});
        return handleCreate(() -> cgDao.save(new CollectionGames(userId, collectionId, gameId)));
    }
    
    @DeleteMapping("/{userId}/{collectionId}/games")
    public ResponseEntity<Void> deleteGame(@PathVariable long userId, @PathVariable long collectionId, @RequestParam long gameId) {
        LOGGER.log(Level.INFO, "POST /{0}/{1}/games?gameId={2}", new Object[]{userId, collectionId, gameId});
        return handleUpdate(() -> cgDao.delete(new CollectionGames(userId, collectionId, gameId)));
    }
    
}
