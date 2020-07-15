package in.inspert.carporationsystems;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityImageView extends AppCompatActivity {
    String imagePath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_image_view);

        ImageView imageView = findViewById(R.id.imageView);

        if (getIntent().getStringExtra("image") != null){
            imagePath = getIntent().getStringExtra("image");
            Glide.with(this).load(imagePath).into(imageView);
        }

    }
}
