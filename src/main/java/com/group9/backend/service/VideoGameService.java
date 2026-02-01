package com.group9.backend.service;
import com.group9.backend.persistence.VideoGameDao;
import com.group9.backend.persistence.AbstractDao.DaoException;
import com.group9.backend.persistence.VideoGameDao.GameInfo;
import com.group9.backend.persistence.RatingDao;
import com.group9.backend.model.Tables.Rating;
import com.group9.backend.persistence.PlaysDao;

import java.util.ArrayList;
import java.util.Collection;

import org.postgresql.util.PGInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoGameService {

    public record FullGameInfo(
        long id,
        String title,
        String platform,
        String developer,
        String publisher,
        String releaseDate,
        String genre,
        String esrb,
        double price,
        PGInterval playTime,
        Double rating
    ) { };

    @Autowired
    private VideoGameDao gameDao;
    
    @Autowired
    private RatingDao ratingDao;

    @Autowired
    private PlaysDao playsDao;

    /**
     * 
     * @param by Column to search by, must be in ["title", "pub.name", "dev.name", "gen.name", "plat.name", "release_date"]
     * @param term Term to search by
     * @param sort Column to sort by, same conditions as @param by defaults to title, release_date
     * @param order Sort order
     * @param user_id User to get results for
     * @return
     */
    public Collection<FullGameInfo> gameSearch(String by, String term, String sort, String order, Long user_id) throws DaoException {
        
        Collection<FullGameInfo> gamesList = new ArrayList<>();

        ArrayList<VideoGameDao.GameInfo> gameInfos = new ArrayList<>(gameDao.getBy(by, term, sort, order));
        for(int i = 0; i < gameInfos.size(); i++) {
            GameInfo gameInfo = gameInfos.get(i);
            long id = gameInfo.id();
            String title = gameInfo.title();
            String dev = gameInfo.developer();
            String pub = gameInfo.publisher();
            String release = gameInfo.releaseDate();
            String genre = gameInfo.genre();
            String esrb = gameInfo.esrb();
            double price = gameInfo.price();
            String platform = gameInfo.platform();
            
            // TODO: is this supposed to be the user's own rating, or the game's average rating?
            Double rating = ratingDao.get(user_id, id).map(Rating::rating).orElse(null);
            // Double rating = ratingDao.getGameAverage(id).orElse(null);
            
            PGInterval playTime = playsDao.getPlayTime(user_id, id).get();

            
            
            FullGameInfo game = new FullGameInfo(id, title, platform, dev, pub, release, genre, esrb, price, playTime, rating);
            gamesList.add(game);
        }


        return gamesList;
    }
    
}
