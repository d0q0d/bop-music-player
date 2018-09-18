package order.android.com.Bop.mvp.usecase;

import java.util.List;

import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.mvp.repository.Repository;
import rx.Observable;


public class GetPlaylistSongs extends UseCase<GetPlaylistSongs.RequestValues,GetPlaylistSongs.ResponseValue>{

    private Repository mRepository;

    public GetPlaylistSongs(Repository repository) {
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new ResponseValue(mRepository.getSongsInPlaylist(requestValues.getPlaylistID()));
    }

    public static final class RequestValues implements UseCase.RequestValues{

        private long playlistID;

        public RequestValues(long playlistID) {
            this.playlistID = playlistID;
        }

        public long getPlaylistID() {
            return playlistID;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<List<Song>> mListObservable;

        public ResponseValue(Observable<List<Song>> listObservable) {
            mListObservable = listObservable;
        }

        public Observable<List<Song>> getSongList(){
            return mListObservable;
        }
    }
}
