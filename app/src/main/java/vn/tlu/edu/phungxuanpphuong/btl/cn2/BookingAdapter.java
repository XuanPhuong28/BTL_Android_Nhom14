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

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView txtCustomerName, txtPhone, txtGuests, txtPayment, txtCheckIn, txtCheckOut, txtStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtGuests = itemView.findViewById(R.id.txtGuests);
            txtPayment = itemView.findViewById(R.id.txtPayment);
            txtCheckIn = itemView.findViewById(R.id.txtCheckIn);
            txtCheckOut = itemView.findViewById(R.id.txtCheckOut);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
