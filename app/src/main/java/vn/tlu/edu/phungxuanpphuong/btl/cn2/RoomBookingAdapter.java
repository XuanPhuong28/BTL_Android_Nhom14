package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import vn.tlu.edu.phungxuanpphuong.btl.R;

public class RoomBookingAdapter extends RecyclerView.Adapter<RoomBookingAdapter.RoomViewHolder> {
    private List<RoomModel> roomList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RoomModel room);
    }

    public RoomBookingAdapter(List<RoomModel> roomList, Context context, OnItemClickListener listener) {
        this.roomList = roomList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomModel room = roomList.get(position);
        holder.txtRoomNumber.setText("Phòng " + room.getRoomNumber());
        holder.txtType.setText("Loại: " + room.getType());
        holder.txtBeds.setText("Mô tả: " + room.getDescription());
        holder.txtPrice.setText("Giá: " + room.getPrice() + "đ/ngày");
        holder.txtStatus.setText("Tình trạng: " + room.getStatus());

        Glide.with(context)
                .load(room.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgRoom);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(room));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView txtRoomNumber, txtType, txtBeds, txtPrice, txtStatus;
        ImageView imgRoom;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRoomNumber = itemView.findViewById(R.id.txtRoomNumber);
            txtType = itemView.findViewById(R.id.txtRoomType);
            txtBeds = itemView.findViewById(R.id.txtRoomBeds);
            txtPrice = itemView.findViewById(R.id.txtRoomPrice);
            txtStatus = itemView.findViewById(R.id.txtRoomStatus);
            imgRoom = itemView.findViewById(R.id.imgRoom);
        }
    }

    public void updateRoomList(List<RoomModel> newList) {
        roomList = newList;
        notifyDataSetChanged();
    }

}