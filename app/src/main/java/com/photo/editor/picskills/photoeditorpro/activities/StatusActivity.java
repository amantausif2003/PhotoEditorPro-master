package com.photo.editor.picskills.photoeditorpro.activities;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import com.photo.editor.picskills.photoeditorpro.R;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StatusActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener, View.OnTouchListener, View.OnClickListener {
    private ImageView imageView, arrowTop;
    private StoriesProgressView storiesProgressView;
    private long pressTime = 0L;
    long limit = 500L;
    private Button tryNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_status);
        imageView = findViewById(R.id.status_image);
        arrowTop = findViewById(R.id.arrow_top);
        tryNow = findViewById(R.id.try_now);
        tryNow.setOnClickListener(this);
        arrowTop.setOnClickListener(this);
        storiesProgressView = findViewById(R.id.stories);
        setModelStory();
        storiesProgressView.setStoriesCount(1); // <- set stories
        storiesProgressView.setStoryDuration(10000); // <- set a story duration  1200L
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories(); // <- start progress
        imageView.setOnTouchListener(this);
        arrowTop.setOnClickListener(this);
    }

    private void setModelStory() {
        if (MainActivity.isModelSelected == 0) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.wings_status
            ));
        }
        if (MainActivity.isModelSelected == 1) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.spiral_status
            ));
        }
        if (MainActivity.isModelSelected == 2) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.blur_status
            ));
        }
        if (MainActivity.isModelSelected == 3) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.b_and_w_status
            ));
        }
        if (MainActivity.isModelSelected == 4) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.filter_status
            ));
        }
        if (MainActivity.isModelSelected == 5) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.gradient_status
            ));
        }
    }

    @Override
    public void onNext() {
        Log.i("onNext", "next");
    }

    @Override
    public void onPrev() {
        Log.i("onPrev", "previous");
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.status_image) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return true;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.try_now) {
            itemClick();
        }
        if (v.getId() == R.id.arrow_top) {
            itemClick();
        }
    }

    private void itemClick() {
        if (MainActivity.isModelSelected == 0) {
            finish();
            MainActivity.getActivity().showIdOrPermission();
        }
        if (MainActivity.isModelSelected == 1) {
            finish();
            MainActivity.getActivity().showIdOrPermission();
        }
        if (MainActivity.isModelSelected == 2) {
            finish();
            MainActivity.getActivity().showIdOrPermission();
        }
        if (MainActivity.isModelSelected == 3) {
            finish();
            MainActivity.getActivity().showIdOrPermission();
        }
        if (MainActivity.isModelSelected == 4) {
            finish();
            MainActivity.getActivity().showIdOrPermission();
        }
        if (MainActivity.isModelSelected == 5) {
            finish();
            MainActivity.getActivity().showIdOrPermission();
        }
    }
}
