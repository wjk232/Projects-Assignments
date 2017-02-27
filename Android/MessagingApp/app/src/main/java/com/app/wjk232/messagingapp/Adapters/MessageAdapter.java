package com.app.wjk232.messagingapp.Adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.wjk232.messagingapp.DataBaseHandler;
import com.app.wjk232.messagingapp.Models.Message;
import com.app.wjk232.messagingapp.ProgressBarAnimation;
import com.app.wjk232.messagingapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rogerycecy on 7/1/2016.
 */
public class MessageAdapter extends ArrayAdapter<Message> implements Serializable{
    private Activity context;
    private List<Message> name;
    private ProgressBar mProgress;
    private TextView username;
    private List<Integer>ttlC = new ArrayList<Integer>();

    public MessageAdapter(Activity context, List<Message> name) {
        super(context, R.layout.message_view, name);
        this.context = context;
        this.name = name;
        return;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;

        if(rowView == null) {
            rowView = context.getLayoutInflater().inflate(R.layout.message_view, parent, false);
        }

        mProgress = (ProgressBar) rowView.findViewById(R.id.progress_bar);
        username = (TextView) rowView.findViewById(R.id.text_view);
        username.setText(getItem(position).getUserName());
        ProgressBarAnimation anim = new ProgressBarAnimation(name, position, mProgress, (int) (getItem(position).percentLeftToLive() / (getItem(position).getTimeToLive_ms() / 100)), 0);
        anim.setDuration((int) getItem(position).percentLeftToLive());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                DataBaseHandler db = DataBaseHandler.getInstance(context);
                db.deleteMessage(getItem(position));
                name.remove(position);
                notifyDataSetChanged();
                db.close();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });
        mProgress.startAnimation(anim);
    return rowView;

    }

}
