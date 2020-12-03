package mahmutcankahya.com.mzeappdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class MuzeEserGoster extends AppCompatActivity {
    String JSON_STRING;
    TextView eserAdi;
    TextView eserDetay;
    ImageView eserResim;
    private TextToSpeech mTTS;
    ImageButton mButtonSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.muze_eser_goster_layout);


        JSON_STRING = getIntent().getExtras().getString("json_data");
        eserAdi = (TextView) findViewById(R.id.tx_eser_ad);
        eserDetay = (TextView) findViewById(R.id.tx_eser_detay);
        eserResim = (ImageView) findViewById(R.id.imgv_eser_resim);
        mButtonSpeak=(ImageButton)findViewById(R.id.imgbSound);

        String a,b,c;
        a=getIntent().getExtras().getString("eser_ad");
        b=getIntent().getExtras().getString("eser_detay");
        c=getIntent().getExtras().getString("eser_resim");
        eserAdi.setText(a);
        eserDetay.setText(b);
        Picasso.get().load(c).resize(100,100).into(eserResim);

        mTTS=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    Locale locale=new Locale("tr","TR");
                    int result=mTTS.setLanguage(locale);
                    if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS","Languagee not supported");
                    }else{
                        mButtonSpeak.setEnabled(true);
                    }
                }else{
                    Log.e("TTS","Initialization failed");
                }
            }
        });

        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTS != null) {
            mTTS.stop();
        }
        mTTS.shutdown();
    }

    private void speak() {
        String text=eserDetay.getText().toString();
        mTTS.setPitch(0.5f);
        mTTS.setSpeechRate(0.5f);

        mTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }


}
