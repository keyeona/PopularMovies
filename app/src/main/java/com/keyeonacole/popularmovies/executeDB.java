package com.keyeonacole.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.concurrent.Executor;

/**
 * Created by keyeona on 7/1/18.
 */

public class executeDB implements Executor{
    @Override
    public void execute(@NonNull Runnable command) {

        new Thread(command).start();

    }
}
