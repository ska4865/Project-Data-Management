package com.group9.backend.controller;

import java.util.Collection;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group9.backend.model.Tables.Friends;
import com.group9.backend.persistence.FriendsDao;

@Controller
@RequestMapping("/friends")
public class FriendsController extends AbstractController {
    
    @Autowired
    private FriendsDao dao;
    
    @PostMapping
    public ResponseEntity<Friends> createFriends(@RequestBody @NonNull Friends friends) {
        LOGGER.log(Level.INFO, "POST /friends {0}", friends);
        return handleOptional(() -> dao.save(friends), HttpStatus.CREATED, HttpStatus.CONFLICT);
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Collection<Friends>> getFriends(@PathVariable long id) {
        LOGGER.log(Level.INFO, "GET /friends/{0}");
        return handleCollection(() -> dao.getByID(id));
    }

    @GetMapping("/withName/{id}")
    @ResponseBody
    public ResponseEntity<Collection<FriendsDao.FriendsInfo>> getFriendsWithName(@PathVariable long id) {
        LOGGER.log(Level.INFO, "GET /friends/{0}");
        return handleCollection(() -> dao.getByIDWithName(id));
    }

    // @PostMapping("/exists")
    // @ResponseBody
    // public ResponseEntity<Boolean> getFriends(@RequestBody @NonNull Friends friends) {
    //     LOGGER.log(Level.INFO, "GET /friends/exists {0}",friends);
    //     try {
    //         return new ResponseEntity<>(service.CheckExistence(friends), HttpStatus.OK);
    //     } catch (Exception e) {
    //         LOGGER.log(Level.SEVERE, "checkExistence: {0}", e.getMessage());
    //         return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    //     }
    // }

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Collection<Friends>> getAll() {
        LOGGER.log(Level.INFO, "GET /friends/{0}");
        return handleCollection(() -> dao.getAll());
    }
    
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> deleteFriends(@RequestBody @NonNull Friends friends) {
        LOGGER.log(Level.INFO, "DELETE /friends {0}", friends);
        return handleUpdate(() -> dao.delete(friends));
    }
}
