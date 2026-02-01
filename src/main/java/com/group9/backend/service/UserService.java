package com.group9.backend.service;
import com.group9.backend.persistence.VideoGameDao;
import com.group9.backend.persistence.VideoGameDevelopedDao;
import com.group9.backend.persistence.AbstractDao.DaoException;
import com.group9.backend.persistence.UserDao.GameRankInfo;
import com.group9.backend.persistence.VideoGameDao.GameInfo;
import com.group9.backend.persistence.VideoGameDao.GameWithPlayInfo;
import com.group9.backend.service.VideoGameService.FullGameInfo;
import com.group9.backend.persistence.RatingDao;
import com.group9.backend.persistence.ReleaseDao;
import com.group9.backend.persistence.UserDao;
import com.group9.backend.persistence.UserPlatformsDao;
import com.group9.backend.model.Tables.Friends;
import com.group9.backend.model.Tables.GameGenre;
import com.group9.backend.model.Tables.Platform;
import com.group9.backend.model.Tables.Release;
import com.group9.backend.model.Tables.VideoGameDeveloped;
import com.group9.backend.persistence.ContributorDao;
import com.group9.backend.persistence.FriendsDao;
import com.group9.backend.persistence.GameGenreDao;
import com.group9.backend.persistence.GenreDao;
import com.group9.backend.persistence.PlaysDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    @Autowired
    private VideoGameDao gameDao;
    
    @Autowired
    private RatingDao ratingDao;

    @Autowired
    private PlaysDao playsDao;

    @Autowired
    private FriendsDao friendsDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPlatformsDao userPlatformsDao;

    @Autowired
    private ReleaseDao releaseDao;

    @Autowired
    private GameGenreDao gameGenreDao;

    @Autowired
    private GenreDao genreDao;

    @Autowired
    private VideoGameDevelopedDao videoGameDevelopedDao;

    @Autowired
    private ContributorDao ContributorDao;






    private boolean playable(long game_id, long user_id) throws DaoException{
        
        
        Collection<Platform> platforms = new ArrayList<>(userPlatformsDao.getByUser(user_id));
        Collection<Long> pID = new ArrayList<>();
        for (Platform platform : platforms) {
            pID.add(platform.platformId());
        }

        Collection<Release> r = releaseDao.getByGame(game_id);

        for (Release release : r) {
            if(pID.contains(release.platformId())){
                return true;
            }
        }
        return false;

    }

    public static <E> Optional<E> getRandom (Collection<E> e) {

        return e.stream()
                .skip((int) (e.size() * Math.random()))
                .findFirst();
    }

    public Collection<FullGameInfo> recommended(Long user_id, int numGames) throws DaoException {
        
        ArrayList<Long> gamesList = new ArrayList<>();

        Collection<Friends> friends = new ArrayList<>(friendsDao.getByID(user_id));

        for (Friends f : friends) {
            long id = f.friendeeUserId()==user_id?f.frienderUserId():f.frienderUserId();
            int i =0;
            for(UserDao.GameRankInfo g: userDao.getFavorites(id, "both")){
                i +=1;
                
                if(playable(id,user_id) && !gamesList.contains(g.id())){
                    gamesList.add(g.id());
                }

                if(i>=3){
                    break;
                }

            }

            if(gamesList.size()>(numGames-5)/2){
                break;
            }
            
        }

        Collection<Long> genres = new ArrayList<>();
        Collection<Long> developers = new ArrayList<>();

        Collection<GameRankInfo> favs= userDao.getFavorites(user_id, "both");

        Iterator<GameRankInfo> iter = favs.iterator();
        
        for(int i=0; i<10 && i<favs.size();i++){
            GameRankInfo game = iter.next();
            
            for (GameGenre g : gameGenreDao.getByVideoGame(game.id())) {
                genres.add(g.genreId());
            }

            for (VideoGameDeveloped g : videoGameDevelopedDao.getByGame(game.id())) {
                developers.add(g.contributorId());
            }

                
        }


        

        for(long genre: genres){
            String g = genreDao.get(genre).get().name();
            Collection<GameInfo> games = gameDao.getBy("gen.name", g, "def", "desc");
            
            for(GameInfo game: games){
                if(playable(game.id(), user_id) && ratingDao.getGameAverage(game.id()).get()>2.5 && !gamesList.contains(game.id())){
                    gamesList.add(game.id());
                    if(gamesList.size()>=(numGames-5)){
                        break;
                    }
                }
            }

        }

        for(long devs: developers){
            String d = ContributorDao.get(devs).get().name();
            Collection<GameInfo> games = gameDao.getBy("dev.name", d, "def", "desc");
            
            for(GameInfo game: games){
                if(playable(game.id(), user_id) && ratingDao.getGameAverage(game.id()).get()>2.5 && !gamesList.contains(game.id())){
                    gamesList.add(game.id());
                    if(gamesList.size()>=(numGames-5)){
                        break;
                    }
                }
            }

        }


        for (GameWithPlayInfo game : gameDao.top20In90Days()) {
            if(playable(game.videoGame().id(), user_id) && !gamesList.contains(game.videoGame().id())){
                gamesList.add(game.videoGame().id());
                if(gamesList.size()>=numGames){
                    break;
                }
            }

        }


        Collection<FullGameInfo> finalList = new ArrayList<>();
        for(int j =0; j<gamesList.size();j++){
            long i = gamesList.get(j);
            GameInfo gameInfo = gameDao.getBy("vg.video_game_id",Long.toString(i), "def", "desc").iterator().next();
            long id = gameInfo.id();
            String title = gameInfo.title();
            String dev = gameInfo.developer();
            String pub = gameInfo.publisher();
            String release = gameInfo.releaseDate();
            String genre = gameInfo.genre();
            String esrb = gameInfo.esrb();
            double price = gameInfo.price();
            String platform = gameInfo.platform();
            Double rating = ratingDao.getGameAverage(id).get();

            FullGameInfo game = new FullGameInfo(id, title, platform, dev, pub, release, genre, esrb, price,null, rating);
            finalList.add(game);
        }

        return finalList;
    }
    
}
