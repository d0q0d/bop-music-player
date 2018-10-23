package order.android.com.Bop.api;

import order.android.com.Bop.api.model.ArtistInfo;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


public interface LastFmApiService {

    String BASE_PARAMETERS_ARTIST = "?method=artist.getinfo&api_key=fdb3a51437d4281d4d64964d333531d4&format=json";

    @GET(BASE_PARAMETERS_ARTIST)
    Observable<ArtistInfo> getArtistInfo(@Query("artist") String artist);
}
