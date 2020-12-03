package mahmutcankahya.com.mzeappdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    JSONObject jsonObject;
    JSONArray jsonArray;

    Context context=this;
    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector barcodeDetector;
    HttpHandler httpHandler;
    private static String URL ;
    ProgressDialog progressDialog;
    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
        textView = (TextView) findViewById(R.id.textView);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Uygulamayı kapamak istediğinizden eminmisiniz?")
                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        textView.setText("Lütfen QR Code okutunuz.");

        barcodeScan();

    }

    private void barcodeScan() {
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setFacing(0).setRequestedPreviewSize(1920, 1080).setAutoFocusEnabled(true).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    }
                    try {
                        cameraSource.start(holder);
                    } catch (IOException o) {
                        o.printStackTrace();
                    }
                    return;
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();
                if (qrCode.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);


                            textView.setText(qrCode.valueAt(0).displayValue);
                            String eserLink = qrCode.valueAt(0).displayValue;
                            URL=eserLink;
                            new getApi().execute();
                            /*
                            arananEser(eser);
                            if (eserVarMı(eser)) {
                                vibrator.vibrate(1000);
                                barcodeDetector.release();
                                textView.setText("Başarılı");
                                Intent intent = new Intent(MainActivity.this, MuzeEserGoster.class);
                                intent.putExtra("eser_ad", arananEser(eser).getEserAdi());
                                intent.putExtra("eser_detay", arananEser(eser).getEserDetay());
                                intent.putExtra("eser_resim", arananEser(eser).getEserResim());
                                startActivity(intent);

                            }*/
                        }
                    });

                }
            }
        });
    }

    private class getApi extends AsyncTask<Void, Void, Eserler> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Lütfen Bekleyiniz...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Eserler result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            Intent intent = new Intent(MainActivity.this, MuzeEserGoster.class);
            intent.putExtra("eser_ad", result.getEserAdi());
            intent.putExtra("eser_detay", result.getEserDetay());
            intent.putExtra("eser_resim", result.getEserResim());
            startActivity(intent);
        }

        @Override
        protected Eserler doInBackground(Void... voids) {
            httpHandler = new HttpHandler();
            String jsonString = new String(httpHandler.makeServiceCall(URL));
            Log.d("JSON_RESPONSE", jsonString);
            JSON_STRING=jsonString;
            /*if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    String link = jsonObject.toString();
                    JSON_STRING = link;
                    Log.d("JSON_RESPONSE", link);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d("JSON_RESPONSE", "Sayfa kaynağı boş");
            }*/

            String[] parts = JSON_STRING.split(":::");
            Eserler eser =new Eserler(parts[0],parts[1],parts[2]);
            return eser;
        }
    }

    /*public Eserler arananEser(String aranan) {
        int count = 0;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            jsonArray = jsonObject.getJSONArray("eserler");
            //Burada tek bir eseri döndürecek bir methot yazmam lazım.
            String a, b, c, d;
            while (count < jsonArray.length()) {
                JSONObject JO = jsonArray.getJSONObject(count);
                a = JO.getString("eser_adi");
                b = JO.getString("eser_detay");
                c = JO.getString("eser_resim");
                d = JO.getString("eser_qr_code");


                eserler.add(new Eserler(a, b, c));
                //eserlere içine attık.
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int count2=0;
        while (count2 < jsonArray.length()) {
            if (aranan.equals(eserler.get(count2).getQrCode())) {
                return eserler.get(count2);
            }
            count2++;
        }
        return null;
    }

    public boolean eserVarMı(String aranan) {

        if (arananEser(aranan) != null) {
            return true;
        }

        return false;
    }*/
}
