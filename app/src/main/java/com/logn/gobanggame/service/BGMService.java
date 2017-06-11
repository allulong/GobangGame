package com.logn.gobanggame.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.logn.gobanggame.R;

/**
 * Created by long on 2017/6/11.
 */

public class BGMService extends Service {
    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (player == null) {
            player = MediaPlayer.create(this, R.raw.bgm_dayu);
            player.setLooping(true);
            player.start();
            Log.e("BGM", "start");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        Log.e("BGM", "stop");
    }
}
