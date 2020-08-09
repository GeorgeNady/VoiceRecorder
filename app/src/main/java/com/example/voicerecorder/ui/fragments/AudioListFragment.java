package com.example.voicerecorder.ui.fragments;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voicerecorder.R;
import com.example.voicerecorder.adapter.AudioListAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;

public class AudioListFragment extends Fragment implements AudioListAdapter.onItemListClick {

    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;
    private File fileToPlay;
    private BottomSheetBehavior bottomSheetBehavior;

    //UI elements
    private ImageButton btnPlay;
    private TextView tvPlayerHeader;
    private TextView tvPlayerFileName;

    private SeekBar seekBar;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;


    public AudioListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPlay = view.findViewById(R.id.btnPlay);
        tvPlayerHeader = view.findViewById(R.id.tvNowPlaying);
        tvPlayerFileName = view.findViewById(R.id.tvFileName);
        seekBar = view.findViewById(R.id.seekBar);
        ImageButton btnForward = view.findViewById(R.id.btnForward);
        ConstraintLayout bottomSheetDialog = view.findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetDialog);


        RecyclerView rvAudioList = view.findViewById(R.id.rvAudioList);
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        File[] allFiles = directory.listFiles();
        AudioListAdapter adapter = new AudioListAdapter(allFiles, this);

        rvAudioList.setHasFixedSize(true);
        rvAudioList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudioList.setAdapter(adapter);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    pauseAudio();
                } else {
                    if (fileToPlay != null) {
                        resumeAudio();
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.getCurrentPosition();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        if (isPlaying) {
            stopAudio();
            playAudio(fileToPlay);
        } else {
            playAudio(fileToPlay);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play, null));
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void resumeAudio() {
        mediaPlayer.start();
        isPlaying = true;
        btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, null));

        updateRunnable();
        seekBarHandler.postDelayed(updateSeekBar, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopAudio() {
        // stop the audio
        btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play, null));
        tvPlayerHeader.setText(getString(R.string.not_playing));
        isPlaying = false;
        if (isPlaying = true) {
            mediaPlayer.stop();
        }
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void playAudio(File fileToPlay) {
        // play the audio
        mediaPlayer = new MediaPlayer();
        if (fileToPlay == null) {
            Toast.makeText(getContext(), "please choose any file to play", Toast.LENGTH_SHORT).show();
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, null));
        tvPlayerFileName.setText(fileToPlay.getName());
        tvPlayerHeader.setText(getString(R.string.now_playing));
        //play the audio
        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    stopAudio();
                }
                tvPlayerHeader.setText(R.string.finish);
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());
        seekBarHandler = new Handler();
        updateRunnable();
        seekBarHandler.postDelayed(updateSeekBar, 0);

    }

    private void updateRunnable() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this, 300);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStop() {
        super.onStop();

        try {
            stopAudio();
        } catch (Exception ignored) {}

    }
}