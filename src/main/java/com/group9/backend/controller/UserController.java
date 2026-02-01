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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group9.backend.model.Tables.User;
import com.group9.backend.persistence.UserDao;
import com.group9.backend.persistence.UserDao.GameRankInfo;
import com.group9.backend.service.UserService;
import com.group9.backend.service.VideoGameService.FullGameInfo;

@Controller
@RequestMapping("/user")
public class UserController extends AbstractController {
    
    @Autowired
    private UserDao dao;

    @Autowired
    private UserService service;
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @NonNull User user) {
        LOGGER.log(Level.INFO, "POST /user {0}", user);
        return handleCreate(() -> dao.save(user));
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable long id) {
        LOGGER.log(Level.INFO, "GET /user/{0}", id);
        return handleGet(() -> dao.get(id));
    }

    @GetMapping("/topGames/{id}/{sort}")
    @ResponseBody
    public ResponseEntity<Collection<GameRankInfo>> getUser(@PathVariable long id,@PathVariable String sort) {
        LOGGER.log(Level.INFO, "GET /user/topGames/{0}?{1}", new Object[]{id,sort});
        return handleCollection(() -> dao.getFavorites(id,sort));
    }

    @GetMapping("/recommended/{id}/{num}")
    @ResponseBody
    public ResponseEntity<Collection<FullGameInfo>> getUser(@PathVariable long id,@PathVariable int num) {
        LOGGER.log(Level.INFO, "GET /user/topGames/{0}?{1}", new Object[]{id,num});
        return handleCollection(() -> service.recommended(id, num));
    }
    
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Collection<User>> getUsersByEmail(@RequestParam("email") @NonNull String email) {
        LOGGER.log(Level.INFO, "GET /user/search?email={0}", email);
        return handleCollection(() -> dao.getByEmail(email));
    }
    
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<User> login(
        @RequestParam("username") @NonNull String username,
        @RequestParam("password") @NonNull String password) {
        LOGGER.log(Level.INFO, "POST /user/login username={0}, password={1}", new Object[]{username, password});
        
        return handleOptional(() -> dao.login(username, password), HttpStatus.OK, HttpStatus.UNAUTHORIZED);
    }
    
    @PutMapping
    public ResponseEntity<Void> updateUser(@RequestBody @NonNull User user) {
        LOGGER.log(Level.INFO, "PUT /user {0}", user);
        return handleUpdate(() -> dao.update(user));
    }
    
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@RequestBody @NonNull User user) {
        LOGGER.log(Level.INFO, "DELETE /user {0}", user);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    
}
