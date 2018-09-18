package order.android.com.Bop.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.dataloader.PlaylistSongLoader;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.util.BopUtil;


public class PlaylistSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Song> arraylist;
    private Context mContext;
    private long[] songIDs;
    private long playlistId;

    public PlaylistSongAdapter(Context context, long playlistId, @Nullable List<Song> arraylist) {

        if (arraylist == null) {
            this.arraylist = new ArrayList<>();
        } else {
            this.arraylist = arraylist;

        }
        this.mContext = context;
        this.songIDs = getSongIds();
        this.playlistId = playlistId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyceler_song, viewGroup, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        Song localItem = arraylist.get(holder.getAdapterPosition());

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);
        if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            itemHolder.title.setTextColor(0xFF0000FF);
            itemHolder.artist.setTextColor(0xFF0000FF);
            itemHolder.popupMenu.setColorFilter(0xFF0000FF);
        } else {
            itemHolder.title.setTextColor(0xFF000000 );
            itemHolder.artist.setTextColor(0xFF000000);
            itemHolder.popupMenu.setColorFilter(0xFF000000);
        }
        setOnPopupMenuListener(itemHolder, position);
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    private void setOnPopupMenuListener(final ItemHolder itemHolder, final int position) {
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext, v);
                final int adapterPosition = itemHolder.getAdapterPosition();
                final Song song = arraylist.get(adapterPosition);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_play_next:
                                long[] ids = new long[1];
                                ids[0] = arraylist.get(adapterPosition).id;
                                MusicPlayer.playNext(mContext, ids, -1, BopUtil.IdType.NA);
                                break;
                            case R.id.popup_song_addto_queue:
                                long[] id = new long[1];
                                id[0] = song.id;
                                MusicPlayer.addToQueue(mContext, id, -1, BopUtil.IdType.Playlist);
                                break;
                            case R.id.popup_song_addto_playlist:
                                BopUtil.showAddPlaylistDialog(mContext,new long[]{song.id});
                                break;
                            case R.id.popup_song_share:
                                BopUtil.shareTrack(mContext, arraylist.get(itemHolder.getAdapterPosition()).id);
                                break;
                            case R.id.popup_song_delete:
                                new MaterialDialog.Builder(mContext)
                                        .title(mContext.getResources().getString(R.string.delete_playlist_song) + "?")
                                        .positiveText(R.string.delete)
                                        .negativeText(R.string.cancel)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                PlaylistSongLoader.removeFromPlaylist(mContext, new long[]{song.id}, playlistId);
                                                arraylist.remove(adapterPosition);
                                                songIDs = getSongIds();
                                                notifyItemRemoved(adapterPosition);
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
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

    public long[] getSongIds() {
        int songNum = arraylist.size();
        long[] ret = new long[songNum];
        for (int i = 0; i < songNum; i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    public List<Song> getSongList() {
        return arraylist;
    }

    public void setSongList(List<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView artist;
        private ImageView popupMenu;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.txt1);
            this.artist = (TextView) view.findViewById(R.id.txt2);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playAll(mContext, songIDs, getAdapterPosition(), -1, BopUtil.IdType.Playlist, false);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(getAdapterPosition());
                        }
                    }, 50);
                }
            }, 100);

        }
    }
}
