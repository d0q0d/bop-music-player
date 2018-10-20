package order.android.com.Bop.mvp.usecase;

import java.util.List;

import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.util.Constants;
import rx.Observable;

public class GetSongs extends UseCase<GetSongs.RequestValues,GetSongs.ResponseValue>{

    private final Repository mRepository;

    public GetSongs(Repository repository){
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        String action = requestValues.getAction();
        switch (action) {

            case Constants.NAVIGATE_ALLSONG:
                return new ResponseValue(mRepository.getAllSongs());
            case Constants.NAVIGATE_QUEUE:
                return new ResponseValue(mRepository.getQueueSongs());
            default:
                throw new RuntimeException("wrong action type");
        }
    }
    public static final class RequestValues implements UseCase.RequestValues {

        private final String action;

        public RequestValues(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
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
