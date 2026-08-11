package com.yarra.ffbooster;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.util.*;

import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {
    static final int REQ=900;
    TextView status; Spinner level; String pkg=null;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(com.yarra.ffbooster.R.layout.activity_main);
        status=findViewById(R.id.status); level=findViewById(R.id.level);
        ArrayAdapter<String> a=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,
                new String[]{"80% — خفض بسيط","70% — موصى به للهاتف الضعيف","60% — خفض قوي"});
        level.setAdapter(a);
        pkg=findGame();
        findViewById(R.id.shizuku).setOnClickListener(v->checkShizuku());
        findViewById(R.id.apply).setOnClickListener(v->applyBoost());
        findViewById(R.id.reset).setOnClickListener(v->resetBoost());
        findViewById(R.id.launch).setOnClickListener(v->launchGame());
        status.setText(pkg==null ? "الحالة: لم يتم العثور على Free Fire" : "الحالة: تم العثور على "+pkg);
        Shizuku.addRequestPermissionResultListener((requestCode,grantResult)->{
            if(requestCode==REQ) status.setText(grantResult==PackageManager.PERMISSION_GRANTED ?
                    "✅ Shizuku مصرح للتطبيق." : "❌ لم تمنح صلاحية Shizuku.");
        });
    }

    String findGame(){
        String[] p={"com.dts.freefireth","com.dts.freefiremax"};
        for(String x:p) try { getPackageManager().getApplicationInfo(x,0); return x; } catch(Exception ignored){}
        return null;
    }

    void checkShizuku(){
        if(!Shizuku.pingBinder()){status.setText("❌ Shizuku غير شغال. شغله أولًا ثم اضغط الزر."); return;}
        if(Build.VERSION.SDK_INT>=23 && Shizuku.checkSelfPermission()==PackageManager.PERMISSION_GRANTED){
            status.setText("✅ Shizuku جاهز.");
        } else if(Build.VERSION.SDK_INT>=23){
            Shizuku.requestPermission(REQ);
        }
    }

    String run(String cmd){
        try{
            Process p=Shizuku.newProcess(new String[]{"sh","-c",cmd},null,null);
            BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder s=new StringBuilder(); String line;
            while((line=br.readLine())!=null)s.append(line).append("\n");
            int code=p.waitFor();
            return "exit="+code+"\\n"+s;
        }catch(Throwable e){return "ERROR: "+e;}
    }

    boolean ready(){
        if(pkg==null){status.setText("❌ Free Fire غير مثبت.");return false;}
        if(!Shizuku.pingBinder()){status.setText("❌ شغّل Shizuku أولًا.");return false;}
        if(Build.VERSION.SDK_INT>=23 && Shizuku.checkSelfPermission()!=PackageManager.PERMISSION_GRANTED){
            status.setText("❌ امنح التطبيق صلاحية Shizuku."); return false;
        }
        return true;
    }

    void applyBoost(){
        if(!ready())return;
        String factor=level.getSelectedItemPosition()==0?"0.8":level.getSelectedItemPosition()==1?"0.7":"0.6";
        status.setText("⏳ تطبيق خفض الدقة...");
        new Thread(()->{
            String c1="device_config put game_overlay "+pkg+" mode=2,downscaleFactor="+factor;
            String c2="cmd game mode performance "+pkg;
            String r1=run(c1),r2=run(c2);
            runOnUiThread(()->status.setText("✅ تم إرسال إعداد خفض الدقة "+factor+" + Performance.\\n"+r1+"\\n"+r2+
                    "\\nأعد تشغيل Free Fire لتطبيق خفض الدقة. إذا قال النظام إن Game Mode غير مدعوم، فهذا الهاتف لا يسمح بهذا التدخل.")); 
        }).start();
    }

    void resetBoost(){
        if(!ready())return;
        status.setText("⏳ إرجاع الإعداد...");
        new Thread(()->{
            String r1=run("device_config delete game_overlay "+pkg);
            String r2=run("cmd game mode standard "+pkg);
            runOnUiThread(()->status.setText("↩️ تم طلب الرجوع للوضع الطبيعي. أعد تشغيل Free Fire.\\n"+r1+"\\n"+r2));
        }).start();
    }

    void launchGame(){
        if(pkg==null){status.setText("❌ لم يتم العثور على Free Fire.");return;}
        Intent i=getPackageManager().getLaunchIntentForPackage(pkg);
        if(i!=null) startActivity(i); else status.setText("❌ تعذر تشغيل اللعبة.");
    }

    @Override protected void onDestroy(){super.onDestroy(); try{Shizuku.removeRequestPermissionResultListener((requestCode,grantResult)->{});}catch(Exception ignored){}}
}
