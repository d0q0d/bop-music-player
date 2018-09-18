package order.android.com.Bop.ui.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
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

import java.util.List;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.util.BopUtil;


public class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.ItemHolder> {

    private List<Song> arraylist;
    private Activity mContext;
    private long albumID;
    private long[] songIDs;

    public AlbumSongsAdapter(Activity context, long albumID) {
        this.mContext = context;
        this.albumID = albumID;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyceler_song, viewGroup, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(AlbumSongsAdapter.ItemHolder itemHolder, int i) {
        Song localItem = arraylist.get(i);
        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);
        if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            itemHolder.title.setTextColor(0xFF0000FF);
            itemHolder.artist.setTextColor(0xFF0000FF);
            itemHolder.popupMenu.setColorFilter(0xFF0000FF);
        } else {
            itemHolder.title.setTextColor(0xFF000000);
            itemHolder.artist.setTextColor(0xFF000000);
            itemHolder.popupMenu.setColorFilter(0xFF000000);
        }


        setOnPopupMenuListener(itemHolder, i);
    }

    private void setOnPopupMenuListener(final ItemHolder itemHolder, final int position) {

        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_play_next:
                                long[] ids = new long[1];
                                ids[0] = arraylist.get(position).id;
                                MusicPlayer.playNext(mContext, ids, -1, BopUtil.IdType.NA);
                                break;
                            case R.id.popup_song_addto_queue:
                                long[] id = new long[1];
                                id[0] = arraylist.get(position).id;
                                MusicPlayer.addToQueue(mContext, id, -1, BopUtil.IdType.NA);
                                break;
                            case R.id.popup_song_addto_playlist:
                                BopUtil.showAddPlaylistDialog(mContext, new long[]{arraylist.get(itemHolder.getAdapterPosition()).id});
                                break;
                            case R.id.popup_song_share:
                                BopUtil.shareTrack(mContext, arraylist.get(itemHolder.getAdapterPosition()).id);
                                break;
                            case R.id.popup_song_delete:
                                long[] deleteIds = {arraylist.get(position).id};
                                BopUtil.showDeleteDialog(mContext, arraylist.get(position).title, deleteIds,
                                        new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                arraylist.remove(position);
                                                songIDs = getSongIds();
                                                notifyDataSetChanged();
                                            }
                                        });
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_song_album_detail);
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    public void setSongList(List<Song> songList) {
        arraylist = songList;
        songIDs = getSongIds();
        notifyDataSetChanged();
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView artist;
        private TextView album;
        private ImageView albumArt;
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
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playAll(mContext, songIDs, getAdapterPosition(), albumID, BopUtil.IdType.Album, false);
                }
            }, 100);

        }

    }
}
