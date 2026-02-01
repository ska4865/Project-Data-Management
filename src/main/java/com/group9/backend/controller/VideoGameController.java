package com.group9.backend.controller;

import java.util.Collection;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group9.backend.persistence.VideoGameDao;
import com.group9.backend.persistence.VideoGameDao.*;
import com.group9.backend.service.VideoGameService;

@Controller
@RequestMapping("/game")
public class VideoGameController extends AbstractController {
    
    @Autowired
    private VideoGameDao dao;
    @Autowired
    private VideoGameService service;
    
    
    @GetMapping("/gamesearch")
    @ResponseBody
    public ResponseEntity<Collection<VideoGameService.FullGameInfo>> gameSearch(@RequestParam("by") @NonNull String by, @RequestParam("term") @NonNull String term, @RequestParam("sort") @NonNull String sort, @RequestParam("order") @NonNull String order, @RequestParam("user_id") @NonNull Long user_id) {
        LOGGER.log(Level.INFO, "GET /game/gamesearch?by={0}&term={1}&sort={2}&order={3}&user_id={4}", new Object[] {by, term, sort, order, user_id});
        try {
            return new ResponseEntity<>(service.gameSearch(by, term, sort, order, user_id), HttpStatus.OK);
        } catch (Exception e) {
            log(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/top20In90Days")
    @ResponseBody
    public ResponseEntity<Collection<GameWithPlayInfo>> top20In90Days() {
        LOGGER.log(Level.INFO, "GET /game/top20In90Days");
        return handleCollection(dao::top20In90Days);
    }
    
    @GetMapping("/top5NewReleases")
    @ResponseBody
    public ResponseEntity<Collection<GameWithPlayInfo>> top5NewReleases() {
        LOGGER.log(Level.INFO, "GET /game/top5NewReleases");
        return handleCollection(dao::top5NewReleases);
    }
    
    @GetMapping("/top20AmongFriends")
    @ResponseBody
    public ResponseEntity<Collection<GameWithPlayInfo>> top20AmongFriends(@RequestParam long userId) {
        LOGGER.log(Level.INFO, "GET /game/top20AmongFriends?userId={}", userId);
        return handleCollection(() -> dao.top20AmongFriends(userId));
    }
    
}
