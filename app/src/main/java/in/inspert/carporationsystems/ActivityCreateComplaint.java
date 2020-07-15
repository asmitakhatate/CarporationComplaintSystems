package in.inspert.carporationsystems;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityCreateComplaint extends AppCompatActivity {
    EditText address, pincode, complaint;


    private Uri filePath;
    String firebasePath = "";

    private final int PICK_IMAGE_REQUEST = 71;

    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_create_complaint);
        setTitle("Send Complaint");
        address =findViewById(R.id.editTextAddress);
        pincode =findViewById(R.id.editTextPincode);
        complaint =findViewById(R.id.editTextComplaint);
        imageView = findViewById(R.id.imageViewCo);

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(CommonKeys.COMPLAINT).push();
                myRef.child(CommonKeys.ADDRESS).setValue(address.getText().toString());
                myRef.child(CommonKeys.PINCODE).setValue(pincode.getText().toString());
                myRef.child(CommonKeys.COMPLAINT_TEXT).setValue(complaint.getText().toString());
                myRef.child(CommonKeys.IMAGE).setValue(firebasePath);
                myRef.child(CommonKeys.STATUS).setValue(CommonKeys.PENDING);

                SharedPreferences prefs = getSharedPreferences(CommonKeys.SHARED, MODE_PRIVATE);
                String name = prefs.getString(CommonKeys.NAME, "No name defined");

                myRef.child(CommonKeys.SENDER).setValue(name);

                Toast.makeText(ActivityCreateComplaint.this, "Complaint sent", Toast.LENGTH_LONG).show();

                finish();

            }
        });

        findViewById(R.id.btnUploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            uploadImage();
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference  storageReference = storage.getReference();

            final String firebase_path = "images/"+ UUID.randomUUID().toString();

            final StorageReference ref = storageReference.child(firebase_path);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    firebasePath = uri.toString();
                                    Log.e("Pathh",firebasePath);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });


                            Toast.makeText(ActivityCreateComplaint.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ActivityCreateComplaint.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        } else {
            Toast.makeText(ActivityCreateComplaint.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
