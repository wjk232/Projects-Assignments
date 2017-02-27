package com.app.wjk232.messagingapp;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

import com.app.wjk232.messagingapp.Models.Message;

import java.util.List;

/**
 * Created by rogerycecy on 7/6/2016.
 */
 public class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;
        private int begin;
        private int end;

        public ProgressBarAnimation(List<Message> msg,int position, ProgressBar progressBar, int begin, int end) {
            super();
            this.progressBar = progressBar;
            this.begin = begin;
            this.end = end;
            return;
        }
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = begin + (end -  begin) * interpolatedTime;
            progressBar.setProgress((int) value);
            return;
        }



}
