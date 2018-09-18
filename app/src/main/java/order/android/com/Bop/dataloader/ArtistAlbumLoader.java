package order.android.com.Bop.dataloader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;
import order.android.com.Bop.mvp.model.Album;
import rx.Observable;
import rx.Subscriber;



public class ArtistAlbumLoader {

    public static Observable<List<Album>> getAlbumsForArtist(final Context context, final long artistID) {
        return Observable.create(new Observable.OnSubscribe<List<Album>>() {
            @Override
            public void call(Subscriber<? super List<Album>> subscriber) {
                List<Album> albumList = new ArrayList<Album>();
                Cursor cursor = makeAlbumForArtistCursor(context, artistID);

                if (cursor != null) {
                    if (cursor.moveToFirst())
                        do {
                            Album album = new Album(cursor.getLong(0), cursor.getString(1), cursor.getString(2), artistID, cursor.getInt(3), cursor.getInt(4));
                            albumList.add(album);
                        }
                        while (cursor.moveToNext());
                }
                if (cursor != null){
                    cursor.close();
                }
                subscriber.onNext(albumList);
                subscriber.onCompleted();
            }
        });
    }


    private static Cursor makeAlbumForArtistCursor(Context context, long artistID) {

        if (artistID == -1)
            return null;

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.Albums.getContentUri("external", artistID), new String[]{"_id", "album", "artist", "numsongs", "minyear"}, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);

        return cursor;
    }
}
