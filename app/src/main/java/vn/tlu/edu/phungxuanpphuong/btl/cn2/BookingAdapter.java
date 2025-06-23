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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<BookingModel> bookingList;
    private Context context;

    public BookingAdapter(List<BookingModel> bookingList) {
        this.bookingList = bookingList;
        this.context = context;
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

        holder.layoutButtons.setVisibility(View.GONE); // ẩn mặc định

        if ("Đã đặt".equals(booking.getStatus())) {
            holder.layoutButtons.setVisibility(View.VISIBLE);
            holder.btnCheckIn.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCheckOut.setVisibility(View.GONE);

            holder.btnCheckIn.setOnClickListener(v -> updateStatus(booking, "Đã ở"));
            holder.btnCancel.setOnClickListener(v -> updateStatus(booking, "availible"));

        } else if ("Đã ở".equals(booking.getStatus())) {
            holder.layoutButtons.setVisibility(View.VISIBLE);
            holder.btnCheckIn.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnCheckOut.setVisibility(View.VISIBLE);

            holder.btnCheckOut.setOnClickListener(v -> updateStatus(booking, "availible"));
        }
    }

    private void updateStatus(BookingModel booking, String newStatus) {
        String roomId = booking.getRoomId();
        String bookingId = booking.getBookingId();

        FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("bookings")
                .child(roomId)
                .child(bookingId)
                .child("status")
                .setValue(newStatus)
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Đã cập nhật trạng thái!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
