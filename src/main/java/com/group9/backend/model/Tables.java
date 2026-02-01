package com.group9.backend.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tables {
    public static record User(
        long id,
        String username,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String email,
        Timestamp dob,
        String firstName,
        String lastName,
        Timestamp lastAccessDate,
        Timestamp creationDate
    ) {}
    public static record Friends(
        long frienderUserId,
        long friendeeUserId
    ) {}
    public static record Platform(
        long platformId,
        String name
    ) {}
    public static record UserPlatforms(
        long userId,
        long platformId
    ) {}
    public static record VideoGame(
        long id,
        String title,
        String ersb
    ) {}
    public static record Release(
        long platformId,
        long videoGameId,
        double price,
        Timestamp releaseDate
    ) {}
    public static record Plays(
        long sessionId,
        long userId,
        long videoGameId,
        Timestamp start,
        Timestamp finish
    ) {}
    public static record Rating(
        long userId,
        long videoGameId,
        double rating
    ) {}
    public static record Collection(
        long userId,
        long collectionId,
        String name
    ) {}
    public static record CollectionGames(
        long userId,
        long collectionId,
        long videoGameId
    ) {}
    public static record Contributor(
        long contributorId,
        String name
    ) {}
    public static record VideoGameDeveloped(
        long videoGameId,     
        long contributorId
        
    ) {}
    public static record VideoGamePublished(
        long contributorId,
        long videoGameId 
    ) {}
    public static record Genre(
        long genreId,
        String name
    ) {};
    public static record GameGenre(
        long genreId,
        long videoGameId
    ) {}
}