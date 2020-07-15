package in.inspert.carporationsystems;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class AdapterComplaints extends RecyclerView.Adapter<AdapterComplaints.ComplaintList> {
    Context context;
    ArrayList<Complaints> complaints;
    String[] status = { CommonKeys.PENDING,CommonKeys.DONE};
    String[] departments = {CommonKeys.SELECT_DEPARTMENT, CommonKeys.USER_WATER,CommonKeys.USER_GARBAGE,CommonKeys.USER_ELECTRICITY};
    FirebaseDatabase database;
    DatabaseReference myRef;
    String userType;

    public AdapterComplaints(Context context, ArrayList<Complaints> complaints) {
        this.context = context;
        this.complaints = complaints;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(CommonKeys.COMPLAINT);
        SharedPreferences prefs = context.getSharedPreferences(CommonKeys.SHARED, MODE_PRIVATE);
        userType = prefs.getString(CommonKeys.USER_TYPE, "");
    }

    class ComplaintList extends RecyclerView.ViewHolder{
        TextView textViewStatus, textViewAddress, textViewPincode, textViewComplaint, textViewDepartment;
        ImageView imageView;
        Spinner spinner;

        public ComplaintList(@NonNull View itemView) {
            super(itemView);
            textViewStatus = itemView.findViewById(R.id.textStatus);
            textViewAddress = itemView.findViewById(R.id.textAddress);
            textViewPincode = itemView.findViewById(R.id.textPincode);
            textViewComplaint = itemView.findViewById(R.id.textComplaint);
            imageView = itemView.findViewById(R.id.imageView);
            spinner = itemView.findViewById(R.id.spinnerStatus);
            textViewDepartment = itemView.findViewById(R.id.textViewDepartment);


            if (userType.equals(CommonKeys.USER_PEOPLE)){
                spinner.setVisibility(View.GONE);
            }

        }
    }

    @NonNull
    @Override
    public ComplaintList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_complaints, parent, false);
        return new ComplaintList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintList holder, int position) {
        final Complaints complaint = complaints.get(position);
        holder.textViewComplaint.setText(complaint.getComplaints());
        holder.textViewAddress.setText(complaint.getAddress());
        holder.textViewPincode.setText(complaint.getPincode());
        if (complaint.getStatus() != null)
            holder.textViewStatus.setText(complaint.getStatus());

        Glide.with(context).load(complaint.getImage()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityImageView.class);
                intent.putExtra("image", complaint.getImage());
                context.startActivity(intent);
            }
        });

        holder.spinner.setVisibility(View.VISIBLE);
        holder.textViewDepartment.setVisibility(View.GONE);

        if (userType.equals(CommonKeys.USER_PEOPLE)){
            holder.spinner.setVisibility(View.GONE);
        }

        if (!userType.equals(CommonKeys.USER_ADMIN)) {
            if (complaint.getStatus().equals(CommonKeys.DONE)) {
                holder.spinner.setVisibility(View.GONE);
            }
        } else {
            if (complaint.getDepartment()!= null)
                if (!complaint.getDepartment().equals(CommonKeys.SELECT_DEPARTMENT)){
                    holder.spinner.setVisibility(View.GONE);
                    holder.textViewDepartment.setVisibility(View.VISIBLE);
                    holder.textViewDepartment.setText(complaint.getDepartment());
                }
        }

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (userType.equals(CommonKeys.USER_ADMIN)){
                    myRef.child(complaint.getId()).child(CommonKeys.DEPARTMENT).setValue(departments[i]);
                } else {
                    if (status[i].equals(CommonKeys.DONE)) {
                        myRef.child(complaint.getId()).child(CommonKeys.STATUS).setValue(status[i]);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter aa;
        if (userType.equals(CommonKeys.USER_ADMIN)){
            aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item,departments);

        } else {
            aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item,status);
        }
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.spinner.setAdapter(aa);

    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }


}
