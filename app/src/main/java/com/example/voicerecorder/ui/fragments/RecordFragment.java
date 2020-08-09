package com.example.voicerecorder.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.voicerecorder.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordFragment extends Fragment implements View.OnClickListener {


    private NavController navController;
    private ImageButton btnRecord;
    private Boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer timer;
    private TextView fileName;

    public RecordFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recordeing, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        ImageButton btnList = view.findViewById(R.id.btnList);
        btnRecord = view.findViewById(R.id.btnRecord);
        timer = view.findViewById(R.id.timer);
        fileName = view.findViewById(R.id.tvFileName);

        btnList.setOnClickListener(this);
        btnRecord.setOnClickListener(this);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnList:
                if (isRecording) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment22);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", null);
                    alertDialog.setTitle("Audio still Recording");
                    alertDialog.setMessage("Are you sure, You want to stop Recoding ??");
                    alertDialog.create().show();
                } else {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment22);
                }
                break;

            case R.id.btnRecord:
                getIsRecording();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getIsRecording() {
        if (isRecording) {
            // stop Recording
            stopRecording();
            btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.mic_gray_scal, null));
            isRecording = false;
        } else {
            // recording
            if (checkPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startRecording();
                }
                btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.mic, null));
                isRecording = true;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        String recordpath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy_mm_dd_hh_mm_ss", Locale.ROOT);
        Date date = new Date();

        recordFile = "Recording_" + dateFormate.format(date) + ".wav";
        fileName.setText(String.format("Recording file name :\n\"%s\"", recordFile));

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioEncodingBitRate(128000);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setOutputFile(recordpath + "/" + recordFile);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            String LOG_TAG = "Recorder";
            Log.e(LOG_TAG, "prepare() failed");
        }

        mediaRecorder.start();
    }

    private void stopRecording() {
        timer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        fileName.setText(String.format("Recording Stoped,file saved\n\"%s\"", recordFile));
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            String AUDIO_RECORD = Manifest.permission.RECORD_AUDIO;
            int REQUEST_CODE = 0;
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{AUDIO_RECORD},
                    REQUEST_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
    }
}