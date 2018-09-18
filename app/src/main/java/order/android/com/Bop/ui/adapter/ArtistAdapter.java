package order.android.com.Bop.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.dataloader.ArtistSongLoader;
import order.android.com.Bop.mvp.model.Artist;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.BopUtil;
import order.android.com.Bop.util.NavigationUtil;
import order.android.com.Bop.util.PreferencesUtility;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemHolder> {

    private List<Artist> arraylist;
    private Activity mContext;
    private String action;
    private boolean isGrid;

    public ArtistAdapter(Activity context, List<Artist> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isArtistsInGrid();
    }

    public ArtistAdapter(Activity context, String action) {
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isArtistsInGrid();
        this.action = action;

    }

    public void setArtistList(List<Artist> arraylist) {
        this.arraylist = arraylist;
        notifyDataSetChanged();
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyceler_song, viewGroup, false);
        return new ItemHolder(v);
    }


    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {
        final Artist localItem = arraylist.get(i);

        itemHolder.name.setText(localItem.name);
        itemHolder.songCount.setText(BopUtil.makeLabel(mContext, R.plurals.Nsongs, localItem.songCount));
        setOnPopupMenuListener(itemHolder, i);

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    private void setOnPopupMenuListener(final ArtistAdapter.ItemHolder itemHolder, final int position) {
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu menu = new PopupMenu(mContext, v);
                int adapterPosition = itemHolder.getAdapterPosition();
                final Artist artist = arraylist.get(adapterPosition);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_artist_addto_queue:
                                getSongListIdByArtist(arraylist.get(position).id)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<long[]>() {
                                            @Override
                                            public void call(long[] ids) {
                                                MusicPlayer.addToQueue(mContext, ids, -1, BopUtil.IdType.NA);
                                            }
                                        });
                                break;
                            case R.id.popup_artist_addto_playlist:
                                getSongListIdByArtist(arraylist.get(position).id)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<long[]>() {
                                            @Override
                                            public void call(long[] ids) {
                                                BopUtil.showAddPlaylistDialog(mContext,ids);
                                            }
                                        });
                                break;
                            case R.id.popup_artist_delete:
                                switch (action) {
                                    case Constants.NAVIGATE_PLAYLIST_FAVOURATE:
                                        getSongListIdByArtist(artist.id)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Action1<long[]>() {
                                                    @Override
                                                    public void call(long[] ids) {
                                                        BopUtil.showDeleteFromFavourate(mContext,ids);
                                                    }
                                                });
                                        break;
                                    case Constants.NAVIGATE_PLAYLIST_RECENTPLAY:
                                        getSongListIdByArtist(artist.id)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Action1<long[]>() {
                                                    @Override
                                                    public void call(long[] ids) {
                                                        BopUtil.showDeleteFromRecentlyPlay(mContext,ids);
                                                    }
                                                });
                                        break;
                                    default:
                                        ArtistSongLoader.getSongsForArtist(mContext,arraylist.get(position).id)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Action1<List<Song>>() {
                                                    @Override
                                                    public void call(List<Song> songs) {
                                                        long[] ids = new long[songs.size()];
                                                        int i = 0;
                                                        for (Song song : songs) {
                                                            ids[i] = song.id;
                                                            i++;
                                                        }
                                                        if (ids.length == 1) {
                                                            BopUtil.showDeleteDialog(mContext, songs.get(0).title, ids,
                                                                    new MaterialDialog.SingleButtonCallback() {
                                                                        @Override
                                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                            arraylist.remove(position);
                                                                            notifyDataSetChanged();
                                                                        }
                                                                    });
                                                        } else {
                                                            String songCount = BopUtil.makeLabel(mContext,
                                                                    R.plurals.Nsongs, arraylist.get(position).songCount);
                                                            BopUtil.showDeleteDialog(mContext, songCount, ids,
                                                                    new MaterialDialog.SingleButtonCallback() {
                                                                        @Override
                                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                            arraylist.remove(position);
                                                                            notifyDataSetChanged();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                        break;
                                }
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_artist);
                menu.show();
            }
        });
    }


    private Observable<long[]> getSongListIdByArtist(long id) {
        return ArtistSongLoader.getSongsForArtist(mContext, id)
                .map(new Func1<List<Song>, long[]>() {
                    @Override
                    public long[] call(List<Song> songs) {
                        long[] ids = new long[songs.size()];
                        int i = 0;
                        for (Song song : songs) {
                            ids[i] = song.id;
                            i++;
                        }
                        return ids;
                    }
                });
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView artistImage;
        private ImageView popupMenu;
        private TextView name;

        private TextView songCount;

        public ItemHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.txt1);
            this.songCount = (TextView) view.findViewById(R.id.txt2);
            this.artistImage = (ImageView) view.findViewById(R.id.image);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu2);
            view.setOnClickListener(this);
        }

      @Override
        public void onClick(View v) {
          NavigationUtil.navigateToArtist(mContext, arraylist.get(getAdapterPosition()).id, arraylist.get(getAdapterPosition()).name,
                  new Pair<View, String>(artistImage, "transition_artist_art" + getAdapterPosition()));
      }


    }
}




