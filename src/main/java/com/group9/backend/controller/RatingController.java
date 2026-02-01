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

import com.group9.backend.model.Tables.Rating;
import com.group9.backend.persistence.RatingDao;

@Controller
@RequestMapping("/rating")
public class RatingController extends AbstractController {
    
    @Autowired
    private RatingDao dao;
    
    @PostMapping
    public ResponseEntity<Rating> createRating(@RequestBody @NonNull Rating rating) {
        LOGGER.log(Level.INFO, "POST /rating {0}", rating);
        return handleCreate(() -> dao.save(rating));
    }
    
    
    @GetMapping("/{uID}/{vID}")
    @ResponseBody
    public ResponseEntity<Rating> getRatings(@PathVariable long uID,@PathVariable long vID) {
        LOGGER.log(Level.INFO, "GET /rating/{0}/{1}", new Object[]{uID,vID});
        return handleGet(() -> dao.get(uID, vID));
    }

    @GetMapping("/user/{uID}")
    @ResponseBody
    public ResponseEntity<Collection<Rating>> getRatingsByUser(@PathVariable long uID) {
        LOGGER.log(Level.INFO, "GET /rating/user/{0}", uID);
        return handleCollection(() -> dao.getByUser(uID));
    }

    @GetMapping("/videoGame/{vID}")
    @ResponseBody
    public ResponseEntity<Collection<Rating>> getRatingsByVideoGame(@PathVariable long vID) {
        LOGGER.log(Level.INFO, "GET /rating/videoGame/{0}", vID);
        return handleCollection(() -> dao.getByGame(vID));
    }

    @GetMapping("/videoGame/avg/{vID}")
    @ResponseBody
    public ResponseEntity<Double> getVideoGameAvg(@PathVariable long vID) {
        LOGGER.log(Level.INFO, "GET /rating/videoGame/avg/{0}", vID);
        return handleGet(() -> dao.getGameAverage(vID));
    }
    
    
    @PutMapping
    public ResponseEntity<Void> updateRating(@RequestBody @NonNull Rating rating) {
        LOGGER.log(Level.INFO, "PUT /rating {0}", rating);
        return handleUpdate(() -> dao.update(rating));
    }
    
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> deleteRating(@RequestBody @NonNull Rating rating) {
        LOGGER.log(Level.INFO, "DELETE /rating {0}", rating);
        return handleUpdate(() -> dao.delete(rating));
    }
}
