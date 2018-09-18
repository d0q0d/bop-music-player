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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.dataloader.AlbumSongLoader;
import order.android.com.Bop.mvp.model.Album;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.BopUtil;
import order.android.com.Bop.util.NavigationUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemHolder> {

    private List<Album> arraylist;
    private Activity mContext;
    private String action;

    public AlbumAdapter(Activity context, String action) {
        this.mContext = context;
        this.action = action;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_grid_layout_item, viewGroup, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, final int i) {
        Album localItem = arraylist.get(i);
        itemHolder.album.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);
        Glide.with(itemHolder.itemView.getContext())
                .load(BopUtil.getAlbumArtUri(localItem.id).toString())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.defult_album_art)
                .into(itemHolder.albumArt);
        if (BopUtil.isLollipop())
            itemHolder.albumArt.setTransitionName("transition_album_art" + i);

        setOnPopupMenuListener(itemHolder, i);
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public void setAlbumsList(List<Album> arraylist) {
        this.arraylist = arraylist;
        notifyDataSetChanged();
    }

    private void setOnPopupMenuListener(final AlbumAdapter.ItemHolder itemHolder, final int position) {
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu menu = new PopupMenu(mContext, v);
                int adapterPosition = itemHolder.getAdapterPosition();
                final Album album = arraylist.get(adapterPosition);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_album_addto_queue:
                                getSongListIdByAlbum(arraylist.get(position).id)
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
                                getSongListIdByAlbum(arraylist.get(position).id)
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
                                        getSongListIdByAlbum(album.id)
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
                                        getSongListIdByAlbum(album.id)
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
                                        AlbumSongLoader.getSongsForAlbum(mContext,arraylist.get(position).id)
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
                menu.inflate(R.menu.popup_album);
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

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView album;
        private TextView artist;
        private ImageView albumArt;
        private ImageView popupMenu;
        private View footer;

        public ItemHolder(View view) {
            super(view);
            this.album = (TextView) view.findViewById(R.id.text_item_title);
            this.artist = (TextView) view.findViewById(R.id.text_item_subtitle);
            this.albumArt = (ImageView) view.findViewById(R.id.image);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtil.navigateToAlbum(mContext, arraylist.get(getAdapterPosition()).id,
                    arraylist.get(getAdapterPosition()).title,
                    new Pair<View, String>(albumArt, "transition_album_art" + getAdapterPosition()));
        }

    }

}



