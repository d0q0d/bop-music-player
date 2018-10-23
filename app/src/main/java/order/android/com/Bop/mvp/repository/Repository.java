package order.android.com.Bop.mvp.repository;

import java.io.File;
import java.util.List;

import order.android.com.Bop.api.model.ArtistInfo;
import order.android.com.Bop.mvp.model.Album;
import order.android.com.Bop.mvp.model.Artist;
import order.android.com.Bop.mvp.model.FolderInfo;
import order.android.com.Bop.mvp.model.Playlist;
import order.android.com.Bop.mvp.model.Song;
import rx.Observable;


public interface Repository {

    Observable<ArtistInfo> getArtistInfo(String artist);

    Observable<File> downloadLrcFile(String title, String artist, long duration);


    //form local

    Observable<List<Album>> getAllAlbums();

    Observable<Album> getAlbum(long id);

    Observable<List<Album>> getAlbums(String paramString);

    Observable<List<Song>> getSongsForAlbum(long albumID);

    Observable<List<Album>> getAlbumsForArtist(long artistID);

    Observable<List<Artist>> getAllArtists();

    Observable<Artist> getArtist(long artistID);

    Observable<List<Artist>> getArtists(String paramString);

    Observable<List<Song>> getSongsForArtist(long artistID);


    Observable<List<Playlist>> getPlaylists(boolean defaultIncluded);

    Observable<List<Song>> getSongsInPlaylist(long playlistID);

    Observable<List<Song>> getQueueSongs();

    Observable<List<Song>> getAllSongs();

    Observable<List<FolderInfo>> getFoldersWithSong();

    Observable<List<Song>> searchSongs(String searchString);


    Observable<List<Song>> getSongsInFolder(String path);

    Observable<List<Object>> getSearchResult(String queryString);
}
