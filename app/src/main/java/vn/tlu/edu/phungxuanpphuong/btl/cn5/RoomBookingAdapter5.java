package vn.tlu.edu.phungxuanpphuong.btl.cn5;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.tlu.edu.phungxuanpphuong.btl.R;
import vn.tlu.edu.phungxuanpphuong.btl.cn2.RoomModel;

public class RoomBookingAdapter5 extends RecyclerView.Adapter<vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomBookingAdapter5.RoomViewHolder> {

    private List<RoomModel> roomList;
    private Context context;
    private vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomBookingAdapter5.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RoomModel room);
    }

    public RoomBookingAdapter5(List<RoomModel> roomList, Context context, vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomBookingAdapter5.OnItemClickListener listener) {
        this.roomList = roomList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomBookingAdapter5.RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomBookingAdapter5.RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomBookingAdapter5.RoomViewHolder holder, int position) {
        RoomModel room = roomList.get(position);
        holder.txtRoomNumber.setText("Phòng " + room.getRoomNumber());
        holder.txtType.setText("Loại: " + room.getType());
        holder.txtBeds.setText("Số giường: " + room.getBeds());
        holder.txtPrice.setText("Giá: " + room.getPrice() + "đ/ngày");
        holder.txtStatus.setText("Tình trạng: " + room.getStatus());
        holder.txtDescription.setText("Mô tả: " + room.getDescription());

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
        TextView txtRoomNumber, txtType, txtBeds, txtPrice, txtStatus, txtDescription;
        ImageView imgRoom;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRoomNumber = itemView.findViewById(R.id.txtRoomNumber);
            txtType = itemView.findViewById(R.id.txtRoomType);
            txtBeds = itemView.findViewById(R.id.txtRoomBeds);
            txtPrice = itemView.findViewById(R.id.txtRoomPrice);
            txtStatus = itemView.findViewById(R.id.txtRoomStatus);
            txtDescription = itemView.findViewById(R.id.txtRoomDesc); // Thêm mô tả
            imgRoom = itemView.findViewById(R.id.imgRoom);
        }
    }

    public void updateRoomList(List<RoomModel> newList) {
        roomList = newList;
        notifyDataSetChanged();
    }
}

