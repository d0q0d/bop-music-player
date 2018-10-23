package order.android.com.Bop.dataloader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import order.android.com.Bop.mvp.model.FolderInfo;
import rx.Observable;
import rx.Subscriber;


public class FolderLoader {

    public static Observable<List<FolderInfo>> getFoldersWithSong(final Context context) {
        final List<FolderInfo> folderInfos = new ArrayList<FolderInfo>();
        final String num_of_songs = "num_of_songs";
        final String[] projection = new String[]{MediaStore.Files.FileColumns.DATA,
                "count(" + MediaStore.Files.FileColumns.PARENT + ") as " + num_of_songs};

        final String selection = " is_music=1 AND title != '' " + " ) " + " group by ( "
                + MediaStore.Files.FileColumns.PARENT;

        return Observable.create(new Observable.OnSubscribe<List<FolderInfo>>() {
            @Override
            public void call(Subscriber<? super List<FolderInfo>> subscriber) {
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Files.getContentUri("external"), projection, selection, null, null);

                if (cursor != null) {
                    int index_data = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    int index_num_of_songs = cursor.getColumnIndex(num_of_songs);

                    while (cursor.moveToNext()) {

                        int songCount = cursor.getInt(index_num_of_songs);

                        String filepath = cursor.getString(index_data);

                        String folderpath = filepath.substring(0, filepath.lastIndexOf(File.separator));

                        String foldername = folderpath.substring(folderpath.lastIndexOf(File.separator) + 1);

                        folderInfos.add(new FolderInfo(foldername, folderpath, songCount));
                    }
                }

                if (cursor != null) {
                    cursor.close();
                }
                subscriber.onNext(folderInfos);
            }
        });
    }
}
