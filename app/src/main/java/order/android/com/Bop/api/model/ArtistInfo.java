package order.android.com.Bop.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ArtistInfo {

    private static final String ARTIST = "artist";

    @Expose
    @SerializedName(ARTIST)
    public LastfmArtist mArtist;
}
