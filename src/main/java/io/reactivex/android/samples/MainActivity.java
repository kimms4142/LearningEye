package io.reactivex.android.samples;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.*;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import io.reactivex.android.samples.BluetoothFragment;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RxAndroidSamples";
    private TextToSpeech textToSpeech;
    TextToSpeech tts;
    private Button btnEnter;
    private EditText edtSpeech;
    TextView tv;
    Intent i;
    SpeechRecognizer mRecognizer;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;


    private final CompositeDisposable disposables = new CompositeDisposable();



    private final String BROADCAST_MESSAGE = "io.reactivex.android.samples";
    private BroadcastReceiver mReceiver = null;
    private int number = 0;


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();

    }






    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        //run_scheduler 부분

        findViewById(R.id.button_run_scheduler).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onRunSchedulerExampleButtonClicked();
            }
        });



        //appinfo 부분
        Button AppIn = (Button) findViewById(R.id.button_app_info) ;
        AppIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent viewinfo = new Intent(getApplicationContext(), AppInfoActivity.class);
                startActivity(viewinfo);
            }
        });


        //블루투스 부분

        Button btn_Connect = (Button) findViewById(R.id.btn_connect) ;
        btn_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent BTN = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivity(BTN);
            }
        });



        if(SpeechRecognizer.isRecognitionAvailable(this)) {
            SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(this);
        } else {
            // SOME SORT OF ERROR
        }

        Button btn_stt = (Button) findViewById(R.id.btn_stt) ;
        btn_stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("-------------------------------------- 음성인식 시작!");
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
                    //권한을 허용하지 않는 경우
                } else {
                    //권한을 허용한 경우
                    final TextView txt = new TextView(MainActivity.this);
                    inputVoice(txt);
                }
            };
        });







        //TTS 부분
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //사용할 언어를 설정
                    int result = textToSpeech.setLanguage(Locale.KOREA);
                    //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        btnEnter.setEnabled(true);
                        //음성 톤
                        textToSpeech.setPitch(0.7f);
                        //읽는 속도
                        textToSpeech.setSpeechRate(1.2f);
                    }
                }
            }
        });

        edtSpeech = (EditText) findViewById(R.id.edt_speech);
        btnEnter = (Button) findViewById(R.id.btn_ent);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Speech();
            }
        });


        //브로드캐스트 부분
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

////////oncreate 끝


    private void Speech() {
        String text = edtSpeech.getText().toString();

        String TTSMessage = getIntent().getStringExtra("ttsMessage");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            textToSpeech.speak(TTSMessage, TextToSpeech.QUEUE_FLUSH, null, null);
        else
            textToSpeech.speak(TTSMessage, TextToSpeech.QUEUE_FLUSH, null);

    }


    public void inputVoice(final TextView txt) {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            final SpeechRecognizer stt = SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    toast("음성 입력 시작...");
                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                }

                @Override
                public void onEndOfSpeech() {
                    toast("음성 입력 종료");
                }

                @Override
                public void onError(int error) {
                    toast("오류 : " + error);
                    stt.destroy();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> result = (ArrayList<String>) results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    txt.append("[나] "+result.get(0)+"\n");
                    replyAnswer(result.get(0), txt);
                    stt.destroy();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            stt.startListening(intent);
        } catch (Exception e) {
            toast(e.toString());
        }
    }



    private void replyAnswer(String input, TextView txt){
        try{
            if(input.equals("안녕")){
                txt.append("[] 누구세요?\n");
                textToSpeech.speak("누구세요?", TextToSpeech.QUEUE_FLUSH, null);
            }
            else if(input.equals("너는 누구니")){
                txt.append("[] 글쎄.\n");
                textToSpeech.speak("글쎄.", TextToSpeech.QUEUE_FLUSH, null);
            }
            else if(input.equals("종료")){
                finish();
            }
            else {
                txt.append("[] 뭐라는거야?\n");
                textToSpeech.speak("뭐라는거야?", TextToSpeech.QUEUE_FLUSH, null);
            }
        } catch (Exception e) {
            toast(e.toString());
        }
    }


    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }



/////////////////브로드캐스트 메인부분
    /** 브로드 캐스트를 발생시킨다. **/
    public void clickMethod(View v){
        /** 1. 전달할 메세지를 담은 인텐트 생성
         * 2. DATA를 잘 전달받는지 확인할 수 있게 Key, value 넣기
         * 3. sendBroadcast(intent); 메서드를 이용해서 전달할 intent를 넣고, 브로드캐스트한다. */

        Intent intent = new Intent(BROADCAST_MESSAGE);
        intent.putExtra("value",number);
        sendBroadcast(intent);
        number++;

    }
    /** 동적으로(코드상으로) 브로드 캐스트를 등록한다. **/
    private void registerReceiver(){
        /** 1. intent filter를 만든다
         *  2. intent filter에 action을 추가한다.
         *  3. BroadCastReceiver를 익명클래스로 구현한다.
         *  4. intent filter와 BroadCastReceiver를 등록한다.
         * */
        if(mReceiver != null) return;

        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(BROADCAST_MESSAGE);

        this.mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int receviedData = intent.getIntExtra("value",0);
                if(intent.getAction().equals(BROADCAST_MESSAGE)){
                    Toast.makeText(context, "recevied Data : "+receviedData, Toast.LENGTH_SHORT).show();
                }
            }
        };

        this.registerReceiver(this.mReceiver, theFilter);

    }

    /** 동적으로(코드상으로) 브로드 캐스트를 종료한다. **/
    private void unregisterReceiver() {
        if(mReceiver != null){
            this.unregisterReceiver(mReceiver);
            mReceiver = null;
        }

    }


    ////////////////////////나중에 스케줄러 부분


    @Override protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    void onRunSchedulerExampleButtonClicked() {
        disposables.add(sampleObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override public void onComplete() {
                        Log.d(TAG, "onComplete()");
                    }
                    @Override public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }
                    @Override public void onNext(String string) {
                        Log.d(TAG, "onNext(" + string + ")");
                    }
                }));
    }

    static Observable<String> sampleObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override public ObservableSource<? extends String> call() throws Exception {
                // Do some long running operation
                SystemClock.sleep(5000);
                return Observable.just("one", "two", "three", "four", "five");
            }
        });
    }


}
