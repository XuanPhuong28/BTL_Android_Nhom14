package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private List<RoomModel> roomList;
    private Context context;

    public RoomAdapter(List<RoomModel> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_card_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomModel room = roomList.get(position);

        holder.txtRoomNumber.setText("Phòng " + room.getRoomNumber());
        holder.txtRoomType.setText("Loại: " + room.getType());
        holder.txtRoomBeds.setText("Mô tả: " + room.getDescription());
        holder.txtRoomPrice.setText("Giá: " + formatCurrency(room.getPrice()));

        Glide.with(context)
                .load(room.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgRoom);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView txtRoomNumber, txtRoomType, txtRoomBeds, txtRoomPrice;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            txtRoomNumber = itemView.findViewById(R.id.txtRoomNumber);
            txtRoomType = itemView.findViewById(R.id.txtRoomType);
            txtRoomBeds = itemView.findViewById(R.id.txtRoomBeds);
            txtRoomPrice = itemView.findViewById(R.id.txtRoomPrice);
        }
    }

    private String formatCurrency(int price) {
        return String.format("%,dđ", price).replace(",", ".");
    }
}

