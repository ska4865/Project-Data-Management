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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group9.backend.model.Tables.Platform;
import com.group9.backend.model.Tables.UserPlatforms;
import com.group9.backend.persistence.UserPlatformsDao;

@Controller
@RequestMapping("/userPlatforms")
public class UserPlatformsController extends AbstractController {
    
    @Autowired
    private UserPlatformsDao dao;
    
    @PostMapping
    public ResponseEntity<UserPlatforms> createUserPlatforms(@RequestBody @NonNull UserPlatforms userPlatforms) {
        LOGGER.log(Level.INFO, "POST /userPlatforms {0}", userPlatforms);
        return handleCreate(() -> dao.save(userPlatforms));
    }
    

    @GetMapping("/user/{uID}")
    @ResponseBody
    public ResponseEntity<Collection<Platform>> getOwnedPlatforms(@PathVariable long uID) {
        LOGGER.log(Level.INFO, "GET /userPlatforms/user/{0}", uID);
        return handleCollection(() -> dao.getByUser(uID));
    }

    @GetMapping("/platform/{pID}")
    @ResponseBody
    public ResponseEntity<Collection<UserPlatforms>> getUserPlatformssByVideoGame(@PathVariable long pID) {
        LOGGER.log(Level.INFO, "GET /userPlatforms/platform/{0}", pID);
        return handleCollection(() -> dao.getByPlatform(pID));
    }
    
    
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> deleteUserPlatforms(@RequestBody @NonNull UserPlatforms userPlatforms) {
        LOGGER.log(Level.INFO, "DELETE /userPlatforms {0}", userPlatforms);
        return handleUpdate(() -> dao.delete(userPlatforms));
    }
}
