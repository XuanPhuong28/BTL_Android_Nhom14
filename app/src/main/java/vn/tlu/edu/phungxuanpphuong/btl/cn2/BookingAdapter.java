package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<BookingModel> bookingList;
    private Context context;
    private OnStatusChangedListener statusChangedListener;

    public interface OnStatusChangedListener {
        void onStatusChanged();
    }

    public BookingAdapter(List<BookingModel> bookingList, Context context, OnStatusChangedListener listener) {
        this.bookingList = bookingList;
        this.context = context;
        this.statusChangedListener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingModel booking = bookingList.get(position);
        holder.txtCustomerName.setText("Khách hàng: " + booking.getCustomer_name());
        holder.txtPhone.setText("SĐT: " + booking.getCustomer_phone());
        holder.txtGuests.setText("Khách: " + booking.getGuests());
        holder.txtPayment.setText("Thanh toán: " + booking.getPayment());
        holder.txtCheckIn.setText("Nhận phòng: " + booking.getCheck_in());
        holder.txtCheckOut.setText("Trả phòng: " + booking.getCheck_out());
        holder.txtStatus.setText("Tình trạng: " + booking.getStatus());

        holder.layoutButtons.setVisibility(View.GONE);

        if ("Đã đặt".equals(booking.getStatus())) {
            holder.layoutButtons.setVisibility(View.VISIBLE);
            holder.btnCheckIn.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCheckOut.setVisibility(View.GONE);

            holder.btnCheckIn.setOnClickListener(v -> updateStatus(booking, "Đã ở"));
            holder.btnCancel.setOnClickListener(v -> updateStatus(booking, "Đã huỷ đặt"));

        } else if ("Đã ở".equals(booking.getStatus())) {
            holder.layoutButtons.setVisibility(View.VISIBLE);
            holder.btnCheckIn.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnCheckOut.setVisibility(View.VISIBLE);

            holder.btnCheckOut.setOnClickListener(v -> updateStatus(booking, "Đã trả phòng"));
        }
    }

    private void updateStatus(BookingModel booking, String newStatus) {
        String roomNumber = booking.getRoomNumber(); // KEY CHUẨN như "203"
        String bookingId = booking.getBookingId();

        if (roomNumber == null || bookingId == null) {
            Toast.makeText(context, "Thiếu thông tin phòng hoặc mã đặt phòng!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/");

        // Cập nhật trạng thái booking
        db.getReference("bookings")
                .child(roomNumber)
                .child(bookingId)
                .child("status")
                .setValue(newStatus)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Đã cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                    if (statusChangedListener != null) {
                        statusChangedListener.onStatusChanged();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // Trạng thái tương ứng của phòng
        String newRoomStatus;
        switch (newStatus) {
            case "Đã ở":
                newRoomStatus = "in-use";
                break;
            case "Đã trả phòng":
            case "Đã huỷ đặt":
                newRoomStatus = "available";
                break;
            default:
                newRoomStatus = "booked";
                break;
        }

        // Cập nhật trạng thái của phòng
        db.getReference("rooms")
                .child(roomNumber)
                .child("status")
                .setValue(newRoomStatus);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView txtCustomerName, txtPhone, txtGuests, txtPayment, txtCheckIn, txtCheckOut, txtStatus;
        LinearLayout layoutButtons;
        Button btnCheckIn, btnCancel, btnCheckOut;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtGuests = itemView.findViewById(R.id.txtGuests);
            txtPayment = itemView.findViewById(R.id.txtPayment);
            txtCheckIn = itemView.findViewById(R.id.txtCheckIn);
            txtCheckOut = itemView.findViewById(R.id.txtCheckOut);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            layoutButtons = itemView.findViewById(R.id.layoutButtons);
            btnCheckIn = itemView.findViewById(R.id.btnCheckIn);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnCheckOut = itemView.findViewById(R.id.btnCheckOut);
        }
    }
}
