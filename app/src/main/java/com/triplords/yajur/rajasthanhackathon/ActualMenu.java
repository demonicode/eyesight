package com.triplords.yajur.rajasthanhackathon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

public class ActualMenu extends AppCompatActivity {

    ImageView i1,i2,i3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_menu);
        i1=(ImageView)this.findViewById(R.id.ivEmotions);
        i2=(ImageView)this.findViewById(R.id.ivObject);
        i3=(ImageView)this.findViewById(R.id.ivText);

        Glide.with(this);
        Glide.with(this)
                .load(R.drawable.sentt)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .centerCrop()
                .into(i1);
        Glide.with(this)
                .load(R.drawable.objectt)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .centerCrop()
                .into(i2);
        Glide.with(this)
                .load(R.drawable.textt)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .centerCrop()
                .into(i3);

        setListeners();
    }

    private void setListeners()
    {
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1Click();
            }
        });

        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button2Click();
            }
        });
        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button3Click();
            }
        });
    }

    public void button1Click ()
    {
        startActivity(new Intent(this,MainActivity.class));
    }

    public void button2Click ()
    {
        startActivity(new Intent(this,MenuActivity.class));

    }

    public void button3Click ()
    {
        startActivity(new Intent(this,OCRActivity.class));

    }


}
