package com.app.wjk232.messagingapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.wjk232.messagingapp.Activities.ContactActivity;
import com.app.wjk232.messagingapp.Models.ContactInfo;
import com.app.wjk232.messagingapp.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by rogerycecy on 6/23/2016.
 */
public class ContactAdapter extends ArrayAdapter<ContactInfo>{
    private Activity context;
    private List<ContactInfo> name;
    private Integer imageId;

    public ContactAdapter(Activity context, List<ContactInfo> name, Integer imageId) {
        super(context, R.layout.contacts_view, name);
        this.context = context;
        this.name = name;
        this.imageId = imageId;

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;
        if(rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView= inflater.inflate(R.layout.contacts_view, null, true);
        }


        if(name.get(position)  != null) {
            TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
            ImageView imageView_sett = (ImageView) rowView.findViewById(R.id.sett);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

            txtTitle.setText(name.get(position).getUserName());
            InputStream is = new ByteArrayInputStream(Base64.decode(name.get(position).getImageName(), Base64.DEFAULT));
            Bitmap image = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(image);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            imageView_sett.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ContactActivity.class);
                    intent.putExtra("settings", name.get(position));
                    context.startActivity(intent);
                }
            });
        }
        return rowView;
    }
}

