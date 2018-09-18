package order.android.com.Bop.ui.adapter;

import android.app.Activity;
import android.os.Handler;
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

import java.util.Collections;
import java.util.List;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.dataloader.AlbumSongLoader;
import order.android.com.Bop.dataloader.ArtistSongLoader;
import order.android.com.Bop.mvp.model.Album;
import order.android.com.Bop.mvp.model.Artist;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.util.BopUtil;
import order.android.com.Bop.util.NavigationUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchAdapter extends  RecyclerView.Adapter<SearchAdapter.ItemHolder>{

    private Activity mContext;
    private List searchResults = Collections.emptyList();

    public SearchAdapter(Activity context) {
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0: //song
            case 1: //album
            case 2: //artist
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyceler_song, viewGroup, false);
                return new ItemHolder(view);
            case 10: //text
                View v10 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_section_header, null);
                return new ItemHolder(v10);
            default:
                View v3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyceler_song, viewGroup, false);
                return new ItemHolder(v3);
        }
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                Song song = (Song) searchResults.get(position);
                itemHolder.title.setText(song.title);
                itemHolder.subtitle1.setText(song.artistName);
                setOnPopupMenuListener(itemHolder, 0, position);
                break;
            case 1:
                Album album = (Album) searchResults.get(position);
                itemHolder.title.setText(album.title);
                itemHolder.subtitle1.setText(album.artistName);
                setOnPopupMenuListener(itemHolder, 1, position);
                break;
            case 2:
                Artist artist = (Artist) searchResults.get(position);
                itemHolder.title.setText(artist.name);
                itemHolder.subtitle1.setText(BopUtil.makeLabel(mContext, R.plurals.Nalbums, artist.albumCount));
                setOnPopupMenuListener(itemHolder, 2, position);
                break;
            case 10:
                itemHolder.sectionHeader.setText((String) searchResults.get(position));
            case 3:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    private void setOnPopupMenuListener(final ItemHolder itemHolder, final int type, final int position) {
        switch (type) {
            case 0:
                setSongPopupMenu(itemHolder, position);
                break;
            case 1:
                setAlbumPopupMenu(itemHolder, position);
                break;
            case 2:
                setArtistPopupMenu(itemHolder, position);
                break;
        }
    }

    private void setSongPopupMenu(ItemHolder itemHolder, final int position) {
        final Song song = (Song) searchResults.get(position);
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_play_next:
                                MusicPlayer.playNext(mContext, new long[]{song.id}, -1, BopUtil.IdType.NA);
                                break;
                            case R.id.popup_song_addto_queue:
                                MusicPlayer.addToQueue(mContext, new long[]{song.id}, -1, BopUtil.IdType.NA);
                                break;
                            case R.id.popup_song_addto_playlist:
                                BopUtil.showAddPlaylistDialog(mContext, new long[]{song.id});
                                break;
                            case R.id.popup_song_delete:
                                BopUtil.showDeleteDialog(mContext, song.title, new long[]{song.id},
                                        new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                searchResults.remove(position);
                                                notifyItemRemoved(position);
                                            }
                                        });
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_song);
                menu.show();
            }
        });
    }

    private void setAlbumPopupMenu(ItemHolder itemHolder,final int position) {
        final Album album = (Album) searchResults.get(position);
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_album_addto_queue:
                                getSongListIdByAlbum(album.id)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<long[]>() {
                                            @Override
                                            public void call(long[] ids) {
                                                MusicPlayer.addToQueue(mContext, ids, -1, BopUtil.IdType.NA);
                                            }
                                        });
                                break;
                            case R.id.popup_album_addto_playlist:
                                getSongListIdByAlbum(album.id)
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
                                AlbumSongLoader.getSongsForAlbum(mContext,album.id)
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
                                                                    searchResults.remove(position);
                                                                    notifyDataSetChanged();
                                                                }
                                                            });
                                                } else {
                                                    String songCount = BopUtil.makeLabel(mContext,
                                                            R.plurals.Nsongs, album.songCount);
                                                    BopUtil.showDeleteDialog(mContext, songCount, ids,
                                                            new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                    searchResults.remove(position);
                                                                    notifyDataSetChanged();
                                                                }
                                                            });
                                                }
                                            }
                                        });
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_album);
                menu.show();
            }
        });
    }

    private void setArtistPopupMenu(ItemHolder itemHolder, final int position) {
        final Artist artist = (Artist) searchResults.get(position);
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_artist_addto_queue:
                                getSongListIdByArtist(artist.id)
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
                                getSongListIdByArtist(artist.id)
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
                                ArtistSongLoader.getSongsForArtist(mContext,artist.id)
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
                                                                    searchResults.remove(position);
                                                                    notifyDataSetChanged();
                                                                }
                                                            });
                                                } else {
                                                    String songCount = BopUtil.makeLabel(mContext,
                                                            R.plurals.Nsongs, artist.songCount);
                                                    BopUtil.showDeleteDialog(mContext, songCount, ids,
                                                            new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                    searchResults.remove(position);
                                                                    notifyDataSetChanged();
                                                                }
                                                            });
                                                }
                                            }
                                        });
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

    private Observable<long[]> getSongListIdByAlbum(long albumId) {
        return AlbumSongLoader.getSongsForAlbum(mContext, albumId)
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

    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof Song)
            return 0;
        if (searchResults.get(position) instanceof Album)
            return 1;
        if (searchResults.get(position) instanceof Artist)
            return 2;
        if (searchResults.get(position) instanceof String)
            return 10;
        return 3;
    }

    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image;
        private TextView title;
        private TextView subtitle1;
        private ImageView popupMenu;

        private TextView sectionHeader;

        public ItemHolder(View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.txt1);
            this.subtitle1 = (TextView) view.findViewById(R.id.txt2);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu2);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.sectionHeader = (TextView) view.findViewById(R.id.section_header);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (getItemViewType()) {
                case 0:
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            long[] ret = new long[1];
                            ret[0] = ((Song) searchResults.get(getAdapterPosition())).id;
                            MusicPlayer.playAll(mContext, ret, 0, -1, BopUtil.IdType.NA, false);
                        }
                    }, 100);

                    break;
                case 1:
                    Album album = (Album) searchResults.get(getAdapterPosition());
                    NavigationUtil.navigateToAlbum(mContext, album.id, album.title,
                            new Pair<View, String>(image, "transition_album_art" + getAdapterPosition()));
                    break;
                case 2:
                    Artist artist = (Artist) searchResults.get(getAdapterPosition());
                    NavigationUtil.navigateToArtist(mContext, artist.id, artist.name,
                            new Pair<View, String>(image, "transition_artist_art" + getAdapterPosition()));
                    break;
                case 3:
                    break;
                case 10:
                    break;
            }
        }

    }
}
