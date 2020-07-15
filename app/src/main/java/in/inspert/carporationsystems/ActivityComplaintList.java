package in.inspert.carporationsystems;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityComplaintList extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Complaints> complaints;
    AdapterComplaints adapterComplaints;
    Button btn;
    TextView textView, textViewType;
    String userType, name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_complaint_list);
        setTitle("Complaint List");

       btn = findViewById(R.id.btnCreateComplaint);

        SharedPreferences prefs = getSharedPreferences(CommonKeys.SHARED, MODE_PRIVATE);
        userType = prefs.getString(CommonKeys.USER_TYPE, "");
        name = prefs.getString(CommonKeys.NAME, "");

        textView = findViewById(R.id.textHelpLine);
        textViewType = findViewById(R.id.textUser);

        textViewType.setText(name);


        if (!userType.equals(CommonKeys.USER_PEOPLE)){
            btn.setVisibility(View.GONE);
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(CommonKeys.ADMIN);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    textView.setText(" Helpline No : "+ dataSnapshot.child(CommonKeys.HELPLINE).getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

       btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityComplaintList.this, ActivityCreateComplaint.class));
            }
        });

        recyclerView = findViewById(R.id.recyclerViewList);
        complaints = new ArrayList<>();

        adapterComplaints = new AdapterComplaints(this, complaints);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        recyclerView.setAdapter(adapterComplaints);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(CommonKeys.COMPLAINT);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaints.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    String type = dataSnapshot1.child(CommonKeys.DEPARTMENT).getValue(String.class);
                    if (userType.equals(CommonKeys.USER_ADMIN) || userType.equals(CommonKeys.USER_PEOPLE)){

                        String name = dataSnapshot1.child(CommonKeys.SENDER).getValue(String.class);
                        String address = dataSnapshot1.child(CommonKeys.ADDRESS).getValue(String.class);
                        String complaint = dataSnapshot1.child(CommonKeys.COMPLAINT_TEXT).getValue(String.class);
                        String pincode = dataSnapshot1.child(CommonKeys.PINCODE).getValue(String.class);
                        String image = dataSnapshot1.child(CommonKeys.IMAGE).getValue(String.class);
                        String status = dataSnapshot1.child(CommonKeys.STATUS).getValue(String.class);
                        complaints.add(new Complaints(dataSnapshot1.getKey(), name, address, pincode, complaint, image, status, type));
                        Collections.reverse(complaints);
                        adapterComplaints.notifyDataSetChanged();
                    } else {
                        if (type != null)
                            if (userType.equals(type)) {
                                String name = dataSnapshot1.child(CommonKeys.SENDER).getValue(String.class);
                                String address = dataSnapshot1.child(CommonKeys.ADDRESS).getValue(String.class);
                                String complaint = dataSnapshot1.child(CommonKeys.COMPLAINT_TEXT).getValue(String.class);
                                String pincode = dataSnapshot1.child(CommonKeys.PINCODE).getValue(String.class);
                                String image = dataSnapshot1.child(CommonKeys.IMAGE).getValue(String.class);
                                String status = dataSnapshot1.child(CommonKeys.STATUS).getValue(String.class);
                                complaints.add(new Complaints(dataSnapshot1.getKey(), name, address, pincode, complaint, image, status, type));
                                Collections.reverse(complaints);
                                adapterComplaints.notifyDataSetChanged();
                            }

                    }



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout){
            SharedPreferences.Editor editor = getSharedPreferences(CommonKeys.SHARED, MODE_PRIVATE).edit();
            editor.putString(CommonKeys.USER_TYPE, "none");
            editor.apply();

            Intent intent = new Intent(ActivityComplaintList.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
