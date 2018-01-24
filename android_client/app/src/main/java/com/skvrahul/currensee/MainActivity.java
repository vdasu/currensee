package com.skvrahul.currensee;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private CameraBridgeViewBase cameraView;
    private String TAG = "MainActivity";
    private Mat rgba = null;
    private String URL = "http://52.224.236.61:5000/";
    private TextToSpeech tts;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cameraView = (CameraBridgeViewBase)findViewById(R.id.camera_view);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(new MyCameraViewListener());
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rgba!=null){
                    shakeItBaby();
                    uploadImage(getBytes(rgba));

                }
            }
        });
    }
    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        }   {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (cameraView != null)
            cameraView.disableView();
        if(tts!=null) {
            tts.stop();
            tts.shutdown();
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    class MyTTS implements TextToSpeech.OnInitListener{
        private String text;
        public MyTTS(String text){
            this.text = text;
        }
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                tts.setPitch((float) 0.7);
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d("TTS", "This Language is not supported");
                }else{
                    speakOut(text);
                }
            } else {
                Log.d("TTS", "Initilization Failed!");
            }
        }
    }
    public void speakOut(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    public void onDestroy() {
        super.onDestroy();
        if (cameraView != null)
            cameraView.disableView();
        tts.stop();
        tts.shutdown();

    }
    public byte[] getBytes(Mat im) {

        Bitmap image = Bitmap.createBitmap(im.cols(),
                im.rows(), Bitmap.Config.RGB_565);

        Utils.matToBitmap(im, image);

        Bitmap bitmap = (Bitmap) image;
        bitmap = Bitmap.createScaledBitmap(bitmap, 600, 450, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return byteArray;
    }
    public void uploadImage(byte[] imageBytes){
        //Setting custom timeout by specifying it in OkHttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface retrofitInterface = retrofit.create(APIInterface.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "file", requestFile);
        Call<Response> call = retrofitInterface.uploadImage(body);
        Log.d(TAG, "uploadImage: Started call");
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {


                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Success");
                    Response responseBody = response.body();
                    Log.d(TAG, "onResponse(Success): Num Notes found = "+responseBody.getTotal());
                    tts = new TextToSpeech(getBaseContext(), new MyTTS("Total is "+responseBody.getTotal()+" rupees"));

                } else {
                    Log.d(TAG, "onResponse: Failure");

//                    ResponseBody errorBody = response.errorBody();
//
//                    Gson gson = new Gson();
//
//                    try {
//
//                        Response errorResponse = gson.fromJson(errorBody.string(), Response.class);
//                        Log.d(TAG, "onResponse(Failure) :"+ errorBody.string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
    }
    private class MyCameraViewListener implements CameraBridgeViewBase.CvCameraViewListener2{
        @Override
        public void onCameraViewStarted(int width, int height) {
            Toast.makeText(getBaseContext(),"Started Camera VIew",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCameraViewStarted: ");
        }

        @Override
        public void onCameraViewStopped() {

        }
        public Mat rotate(Mat src, double angle)
        {
            Mat dst = new Mat();
            if(angle == 180 || angle == -180) {
                Core.flip(src, dst, -1);
            } else if(angle == 90 || angle == -270) {
                Core.flip(src.t(), dst, 1);
            } else if(angle == 270 || angle == -90) {
                Core.flip(src.t(), dst, 0);
            }

            return dst;
        }
        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

            rgba = inputFrame.rgba();
//            Mat rot = rotate(rgba, 90);
            return rgba;
        }
    }
}
