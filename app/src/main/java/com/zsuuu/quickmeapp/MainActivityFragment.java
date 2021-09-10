package com.zsuuu.quickmeapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.provider.DocumentFile;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;


public class MainActivityFragment extends Fragment implements CryptoThread.ProgressDisplayer {

    public static final String CALLBACK_SELECT_OUTPUT_FILE = "com.dewdrop623.androidcrypt.MainActivityFragment.CALLBACK_SELECT_OUTPUT_FILE";
    public static final String CALLBACK_SELECT_INPUT_FILE = "com.dewdrop623.androidcrypt.MainActivityFragment.CALLBACK_SELECT_INPUT_FILE";

    private static char[] password = null;

    private static final int SELECT_INPUT_FILE_REQUEST_CODE = 623;
    private static final int SELECT_OUTPUT_DIRECTORY_REQUEST_CODE = 8878;
    private static final int WRITE_FILE_PERMISSION_REQUEST_CODE = 440;

    private static final String PROGRESS_DISPLAYER_ID = "com.dewdrop623.androidcrypt.MainActivityFragment.PROGRESS_DISPLAYER_ID";

    private static final String SAVED_INSTANCE_STATE_SHOW_PASSWORD = "com.dewdrop623.androidcrypt.MainActivityFragment.SAVED_INSTANCE_STATE_SHOW_PASSWORD";
    private static final String SAVED_INSTANCE_STATE_OPERATION_MODE = "com.dewdrop623.androidcrypt.MainActivityFragment.SAVED_INSTANCE_STATE_OPERATION_MODE";
    private static final String SAVED_INSTANCE_STATE_INPUT_FILENAME = "com.dewdrop623.androidcrypt.MainActivityFragment.SAVED_INSTANCE_STATE_INPUT_FILENAME";
    private static final String SAVED_INSTANCE_STATE_OUTPUT_FILENAME = "com.dewdrop623.androidcrypt.MainActivityFragment.SAVED_INSTANCE_STATE_OUTPUT_FILENAME";
    private static final String SAVED_INSTANCE_STATE_DELETE_INPUT_FILE = "com.dewdrop623.androidcrypt.MainActivityFragment.SAVED_INSTANCE_STATE_DELETE_INPUT_FILE";


    private boolean operationMode = CryptoThread.OPERATION_TYPE_ENCRYPTION;
    private boolean showPassword = false;
    private DocumentFile inputFileParentDirectory = null;
    String inputFileName;
    private DocumentFile outputFileParentDirectory = null;
    String outputFileName;
    private boolean deleteInputFile = false;

    private Context context;

    private Button encryptModeButton;

    private Button shengchengmiyao;
    private Button fuzhie;
    private Button shengchengaeskey;
    private Button fuzhiaeskey;
    private Button jiemiaeskey;
    private Button fuzhiaesmima;
    private Button qingchuothermiwen;
    private Button qingchuotherN;
    private TextView aboutkeye;
    private TextView aboutkeyd;
    private TextView aboutkeyn;
    private TextView aboutkeyaes;
    private TextView aboutaeskey;
    private TextView jiemiaesmima;

    private Button decryptModeButton;
    private LinearLayout inputFilePathLinearLayout;
    private TextView inputFilePathTextView;
    private View inputFilePathUnderlineView;
    private LinearLayout outputFilePathLinearLayout;
    private TextView outputFilePathTextView;
    private View outputFilePathUnderlineView;
    private FileSelectButton inputFileSelectButton;
    private CheckBox deleteInputFileCheckbox;
    private FileSelectButton outputFileSelectButton;
    private EditText otherN;
    private EditText othermiwen;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private CheckBox showPasswordCheckbox;
    private LinearLayout progressDisplayLinearLayout;
    private TextView progressDisplayTextView;
    private ProgressBar progressDisplayProgressBar;
    private LinearLayout progressDispayCancelButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        encryptModeButton = (Button) view.findViewById(R.id.encryptModeButton);
        shengchengmiyao = (Button) view.findViewById(R.id.shengchengmiyao);
        fuzhie = (Button) view.findViewById(R.id.fuzhie);
        shengchengaeskey = (Button) view.findViewById(R.id.shengchengaeskey);
        fuzhiaeskey = (Button) view.findViewById(R.id.fuzhiaeskey);
        jiemiaeskey = (Button) view.findViewById(R.id.jiemiaeskey);
        fuzhiaesmima = (Button) view.findViewById(R.id.fuzhiaesmima);
        qingchuotherN = (Button) view.findViewById(R.id.qingchuotherN);
        qingchuothermiwen = (Button) view.findViewById(R.id.qingchuothermiwen);
        aboutkeye = (TextView) view. findViewById(R.id.aboutkeye);
        aboutkeyd = (TextView) view. findViewById(R.id.aboutkeyd);
        aboutkeyn = (TextView) view. findViewById(R.id.aboutkeyn);
        aboutkeyaes = (TextView) view. findViewById(R.id.aboutkeyaes);
        aboutaeskey = (TextView) view. findViewById(R.id.aboutaeskey);
        jiemiaesmima = (TextView) view. findViewById(R.id.jiemiaesmima);
        otherN = (EditText) view.findViewById(R.id.otherN);
        othermiwen = (EditText) view.findViewById(R.id.othermiwen);


        decryptModeButton = (Button) view.findViewById(R.id.decryptModeButton);
        inputFilePathLinearLayout = (LinearLayout) view.findViewById(R.id.inputFilePathLinearLayout);
        inputFilePathTextView = (TextView) view.findViewById(R.id.inputFilePathTextView);
        inputFilePathUnderlineView = view.findViewById(R.id.inputFilePathUnderlineView);
        outputFilePathLinearLayout = (LinearLayout) view.findViewById(R.id.outputFilePathLinearLayout);
        outputFilePathTextView = (TextView) view.findViewById(R.id.outputFilePathTextView);
        outputFilePathUnderlineView = view.findViewById(R.id.outputFilePathUnderlineView);
        inputFileSelectButton = (FileSelectButton) view.findViewById(R.id.selectInputFileButton);
        deleteInputFileCheckbox = (CheckBox) view.findViewById(R.id.deleteInputFileCheckbox);
        outputFileSelectButton = (FileSelectButton) view.findViewById(R.id.selectOutputFileButton);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.confirmPasswordEditText);
        showPasswordCheckbox = (CheckBox) view.findViewById(R.id.showPasswordCheckbox);
        progressDisplayLinearLayout = (LinearLayout) view.findViewById(R.id.progressDisplayLinearLayout);
        progressDisplayTextView = (TextView) view.findViewById(R.id.progressDisplayTextView);
        progressDisplayProgressBar = (ProgressBar) view.findViewById(R.id.progressDisplayProgressBar);
        progressDispayCancelButton = (LinearLayout) view.findViewById(R.id.progressDisplayCancelButtonLinearLayout);

        showPasswordCheckbox.setOnCheckedChangeListener(showPasswordCheckBoxOnCheckedChangeListener);
        outputFileSelectButton.setOnClickListener(outputFileSelectButtonOnClickListener);
        inputFileSelectButton.setOnClickListener(inputFileSelectButtonOnClickListener);
        encryptModeButton.setOnClickListener(operationModeButtonsOnClickListener);


        shengchengmiyao.setOnClickListener(shengchengmiyaoOnClickListener);
        fuzhie.setOnClickListener(fuzhieOnClickListener);
        shengchengaeskey.setOnClickListener(shengchengaeskeyOnClickListener);
        fuzhiaeskey.setOnClickListener(fuzhiaeskeyOnClickListener);
        jiemiaeskey.setOnClickListener(jiemiaeskeyOnClickListener);
        fuzhiaesmima.setOnClickListener(fuzhiaesmimaOnClickListener);
        qingchuotherN.setOnClickListener(qingchuotherNOnClickListener);
        qingchuothermiwen.setOnClickListener(qingchuothermiwenOnClickListener);


        decryptModeButton.setOnClickListener(operationModeButtonsOnClickListener);
        progressDispayCancelButton.setOnClickListener(progressDispayCancelButtonOnClickListener);
        deleteInputFileCheckbox.setOnCheckedChangeListener(deleteInputFileCheckboxOnCheckedChangedListener);

        checkPermissions();


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        updateUI(savedInstanceState);

        CryptoThread.registerForProgressUpdate(PROGRESS_DISPLAYER_ID, this);

        if (CryptoThread.operationInProgress) {
            update(CryptoThread.getCurrentOperationType(), CryptoThread.getProgressUpdate(), CryptoThread.getCompletedMessageStringId(), -1, -1);
        }


        if (SettingsHelper.getUseDarkTeme(getContext())) {
            int textColor = ((MainActivity)getActivity()).getDarkThemeColor(android.R.attr.textColorPrimary);
            deleteInputFileCheckbox.setTextColor(textColor);
            showPasswordCheckbox.setTextColor(textColor);
            inputFilePathTextView.setTextColor(textColor);
            outputFilePathTextView.setTextColor(textColor);
            inputFilePathUnderlineView.setBackgroundColor(textColor);
            outputFilePathUnderlineView.setBackgroundColor(textColor);
        }
        SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        String n = sp.getString("gongyao", "");
        String aes = sp.getString("aes", "");
        otherN.setText(n);
        passwordEditText.setText(aes);
        confirmPasswordEditText.setText(aes);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).returnedToMainFragment();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(createOutStateBundle(outState));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                ((MainActivity) getActivity()).displaySecondaryFragmentScreen(new AboutFragment(), context.getString(R.string.action_about), null);
                return true;
            case R.id.action_settings:
                ((MainActivity) getActivity()).displaySecondaryFragmentScreen(new SettingsFragment(), context.getString(R.string.action_settings), null);
                return true;
        }
        return false;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void update(final boolean operationType, final int progress, final int completedMessageStringId, final int minutesToCompletion, final int secondsToCompletion) {
        final Context context = getContext();
        if (context != null) {
            new Handler(context.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    progressDisplayLinearLayout.setVisibility(progress == 100 ? View.INVISIBLE : View.VISIBLE);
                    progressDisplayProgressBar.setProgress(progress);
                    String progressText = operationType == CryptoThread.OPERATION_TYPE_ENCRYPTION ? getString(R.string.encrypting) : getString(R.string.decrypting);
                    if (minutesToCompletion != -1) {
                        progressText = progressText.concat(" " + minutesToCompletion + "m");
                    }
                    if (secondsToCompletion != -1) {
                        progressText = progressText.concat(" " + secondsToCompletion + "s");
                    }
                    progressDisplayTextView.setText(progressText);
                }
            });
        }
    }

    private View.OnClickListener operationModeButtonsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.encryptModeButton:
                    enableEncryptionMode();
                    break;
                case R.id.decryptModeButton:
                    enableDecryptionMode();
                    break;
            }
        }
    };




     //清除按钮1和2
    private View.OnClickListener qingchuotherNOnClickListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            otherN.setText("");
            Toast.makeText(context.getApplicationContext(), "清除对方公钥成功", Toast.LENGTH_SHORT).show();
        }
    };
    private View.OnClickListener qingchuothermiwenOnClickListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            othermiwen.setText("");
            Toast.makeText(context.getApplicationContext(), "清除对方密文成功", Toast.LENGTH_SHORT).show();
        }
    };

//点击生成密钥按钮后会发生的事情
    private View.OnClickListener shengchengmiyaoOnClickListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.shengchengmiyao){
//                Toast.makeText(context.getApplicationContext(), "生成密钥按钮启动", Toast.LENGTH_SHORT).show();
//            生成大质数P、Q
                Random ran = new Random();
                BigInteger inputp = BigInteger.probablePrime(1700, ran);
                BigInteger inputq = BigInteger.probablePrime(1700, ran);
//            默认公钥e：65537，为公认加密标准
                int e = 65537;
//                xianshi_e.setText("公钥e:65537");
//            生成私钥d
                BigInteger d;
                d = mainshengcheng(inputp,inputq);
//                xianshi_d.setText("私钥d:"+d.toString(36));
//            生成模n
                BigInteger n = qium(inputp,inputq);
// 加密文件所使用的AESKey需要生成一个32位的随机数
//                String aes = "1239876547539152846";
                Random rand = new Random();
                int aesint1 = rand.nextInt(99999999);
                int aesint2 = rand.nextInt(99999999);
//                int aesint3 = rand.nextInt(99999999);
//                int aesint4 = rand.nextInt(99999999);
                String aesint1str = Integer.toString(aesint1);
                String aesint2str = Integer.toString(aesint2);
//                String aesint3str = Integer.toString(aesint3);
//                String aesint4str = Integer.toString(aesint4);

                String aes ="";
//                aes = aesint1str+aesint2str+aesint3str+aesint4str;
                aes = aesint1str+aesint2str;
                SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                sp.edit().putString("e","65537")
                        .putString("d",d.toString())
                        .putString("n",n.toString())
                        .putString("aes", aes.toString()).commit();
//-----------------------------------------------------------
//                SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
//                String username = sp.getString("username", "");
//                String userpass = sp.getString("userpass", "");
//-----------------------------------------------------------
//                显示所有信息
                aboutkeye.setText("该信息只显示1次，请不要保管密钥，经常跟换密钥保证通讯安全. e:"+ e);
                aboutkeyd.setText("d:" + d.toString());
                aboutkeyn.setText("n:" + n.toString());
                aboutkeyaes.setText("aes:" + aes.toString());
                aboutaeskey.setText("AES密文尚未生成，请获取对方公钥后点击生成");
                passwordEditText.setText(aes.toString());
                confirmPasswordEditText.setText(aes.toString());
                Toast.makeText(context.getApplicationContext(), "生成密钥成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //    求私钥
    private BigInteger mainshengcheng(BigInteger inputp, BigInteger inputq) {
        //        println("p:" + p)
//        println("q:" + q)
//    求公模
        BigInteger n = qium(inputp, inputq);
//        println("n:" + n)
        BigInteger d = genkey(inputp, inputq);
//    大数比较，需要补一次数据
        if(d.compareTo(BigInteger.ZERO)==-1){
//            var dd = d+ qiufy(p,q)
            BigInteger qiufypaddq = qiufy(inputp,inputq);
            BigInteger dd = d.add(qiufypaddq);
//            println("d:" + dd)
            return dd;
        }
        else{
            return  d;
        }
    };

    private BigInteger qium(BigInteger inputp, BigInteger inputq) {
        BigInteger n = inputp.multiply(inputq);
        return n;
    };

    private BigInteger genkey(BigInteger inputp, BigInteger inputq) {
//    var n = qium(p, q)
//    val n = p.multiply(q)
//    var fy =p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))
        BigInteger fy = qiufy(inputp,inputq);
        BigInteger e = new BigInteger("65537");
        BigInteger a = e;
        BigInteger b = fy;
//    这里有个拓展欧几里得算法
        BigInteger[] rxy = extGcd(a, b);
        BigInteger r = rxy[0];
        BigInteger x = rxy[1];
        BigInteger y = rxy[2];
        BigInteger d = x;
        return d;
    }

    private BigInteger[] extGcd(BigInteger a, BigInteger b) {
        if(b.equals(BigInteger.ZERO)){
            BigInteger x1 = BigInteger.ONE;
            BigInteger y1 = BigInteger.ZERO;
            BigInteger x = x1;
            BigInteger y = y1;
            BigInteger r = a;
//        if(x.compareTo(BigInteger.ZERO)==-1){
//            x = x.add (b)
//        }
            BigInteger[] result = {r, x, y};
            return result;
        }else{
            BigInteger bb = a.remainder(b);
            BigInteger[] temp = extGcd(b, bb);
            BigInteger r = temp[0];
//            println("r:" + r)
            BigInteger x1 = temp[1];
//            println("x1:" + x1)
            BigInteger y1 = temp[2];
//            println("y1:" + y1)
//        r, x1, y1 = ext_gcd(b, a % b)
//        如果私钥小于0,d=d+fy
            BigInteger x = y1;
//        if(x.compareTo(BigInteger.ZERO)==-1){
//            x = x.add (b)
//        }
            BigInteger y = x1.subtract(a.divide(b).multiply(y1));
            BigInteger[] result = {r, x, y};
            return result;
        }
    };

    private BigInteger qiufy(BigInteger inputp, BigInteger inputq) {
        BigInteger fy = inputp.subtract(BigInteger.ONE).multiply(inputq.subtract(BigInteger.ONE));
        return fy;
    };
//以上为生成密钥按钮内容

//     复制公钥按钮
    private View.OnClickListener fuzhieOnClickListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
//            SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
//            sp.edit().putString("e","65537")
//                    .putString("d",d.toString())
//                    .putString("n",n.toString())
//                    .putString("aes", aes.toString()).commit();
//读取-----------------------------------------------------------
                SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                String n = sp.getString("n", "NoData");
//                otherN.setText(n);
//            复制到剪贴板
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//            array[1] = namen, 公钥e默认为65537，所以不用复制了
            ClipData clipData = ClipData.newPlainText("n", n);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context.getApplicationContext(), "复制公钥成功", Toast.LENGTH_SHORT).show();
        }
    };

//获取对方公钥后生成aes加密后的密文，生成AES密文按钮
    private View.OnClickListener shengchengaeskeyOnClickListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
//            if (view.getId() == R.id.shengchengaeskey){
//                Toast.makeText(context.getApplicationContext(), "生成AESKey按钮启动", Toast.LENGTH_SHORT).show();
//            }
//          如果没有对方的公钥
            String mimiwen2 = otherN.getText().toString();
            if(mimiwen2.length() == 0){
                Toast.makeText(context.getApplicationContext(), "未输入对方公钥", Toast.LENGTH_SHORT).show();
            }else{
//                执行
//            打开文件
            SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
//            把36进制转换为了bigintger
            BigInteger inputn = new BigInteger(otherN.getText().toString());
//            进制转换来完成字符的传递
//            读取保存刚刚生成的aes密码
            String aes = sp.getString("aes", "NoData");
            BigInteger mingwentext = new BigInteger(aes);
//            String utfmingwen = chaijiejiami(mingwentext.toString());
//            BigInteger inputm = new BigInteger(utfmingwen);
            BigInteger rr = encrypt(mingwentext,inputn);
//            显示在屏幕上
            aboutaeskey.setText("AES密文："+rr.toString());
//            Toast.makeText(this, "加密成功，请点击“复制密文”", Toast.LENGTH_SHORT).show()
//            保存了加密的内容
//            val editor = getSharedPreferences("data",Context.MODE_PRIVATE).edit()
//            editor.putString("mingwen", rr.toString(36))
//            editor.putString("gongyao", inputn.toString(36))
//            editor.apply()
            sp.edit().putString("gongyao",inputn.toString())
                    .putString("AESKey",rr.toString()).commit();
            Toast.makeText(context.getApplicationContext(), "生成AES密文成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

//    private String chaijiejiami(String string) {
////        var text = "0123我爱你abc,.!，。！1230"
////    var textt: Char = '我'
////    println(chartoint(textt))
////    var bbb=chartoint(textt)
////    println(inttochar(bbb))
////        char a[]=s.toCharArray()
//        char chrCharArray[] = string.toCharArray();
//        String zonghe= "";
////    var danzi =chrCharArray[10]
////    println("加密的字符"+danzi)
////    var endanzi =chartoint(danzi)
////    println("得到的10进制码"+endanzi)
////    var dcdanzi =inttochar(endanzi.toInt())
////    println("解码后的字符"+dcdanzi)
//        for(int i = 0;i >= 0  &&  i<=(chrCharArray.length-1);i++){
////        println(chrCharArray[i])
//            zonghe = zonghe + (int)(chrCharArray[i]);
////            println(i);
//        };
////        println(zonghe)
//        String zonghechu = "9" + zonghe;
////        println(zonghechu)
//        return zonghechu;
//    }

    private BigInteger encrypt(BigInteger m, BigInteger n){
//    var e:BigInteger = ("65537")
        BigInteger e = new BigInteger("65537");
        //    var r:BigInteger = 1
        BigInteger r = new BigInteger("1");
        BigInteger shuzi2 = new BigInteger("2");
        BigInteger biao = new BigInteger("55555");
        BigInteger mm = m;
//        mm = mm.mod(n)
        while (e.compareTo(BigInteger.ZERO)==1){
            biao = e.mod(shuzi2);
            if(biao.compareTo(BigInteger.ONE)==0){
                r = r.multiply(mm).remainder(n);
            }
            mm =mm.multiply(mm).remainder(n);
            e = e.divide(shuzi2);
        }
        return r;
    }
    //以上是AESKey的生成按钮

    //复制AES密文的按钮
    private View.OnClickListener fuzhiaeskeyOnClickListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
//            if (view.getId() == R.id.fuzhiaeskey){
//                Toast.makeText(context.getApplicationContext(), "复制AES密文按钮启动", Toast.LENGTH_SHORT).show();
//            }
            SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
            String n = sp.getString("AESKey", "NoData");
//                otherN.setText(n);
//            复制到剪贴板
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//            array[1] = namen, 公钥e默认为65537，所以不用复制了
            ClipData clipData = ClipData.newPlainText("n", n);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context.getApplicationContext(), "复制AES密文成功", Toast.LENGTH_SHORT).show();
        }
    };
    //以上复制AESKey的按钮
//    解密AESKey的按钮
    private View.OnClickListener jiemiaeskeyOnClickListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
//            读取数据自己的私钥d，n
            SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
            BigInteger d = new BigInteger(sp.getString("d", "NoData"));
            BigInteger n = new BigInteger(sp.getString("n", "NoData"));
//            var inputmi = BigInteger(miwen3.editableText.toString())
            String mimiwen = othermiwen.getText().toString();
            if(mimiwen.length() == 0){
                Toast.makeText(context.getApplicationContext(), "未输入密文", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context.getApplicationContext(), "解密中", Toast.LENGTH_SHORT).show();
                BigInteger inputmi = new BigInteger(mimiwen);
                BigInteger rr = decrypt(inputmi,d,n);
//            显示在屏幕上
                String mingwenrr = rr.toString();
//                String rrr = zifuchuanzhuanhuan(mingwenrr);
                jiemiaesmima.setText("对方AES密码:"+rr);
//                aboutaeskey.setText("rr2:"+rr);
//                System.out.print(rrr);
                Toast.makeText(context.getApplicationContext(), "解密成功", Toast.LENGTH_SHORT).show();
//                保存一下别人的aes密码，就是解密后的aes结果啦
                sp.edit().putString("AESKeyother",rr.toString()).commit();
                Toast.makeText(context.getApplicationContext(), "解密AESKey成功", Toast.LENGTH_SHORT).show();
            }
        };
    };

    private BigInteger decrypt(BigInteger m, BigInteger d, BigInteger n){
//    var e:BigInteger = ("65537")
        BigInteger e = d;
        //    var r:BigInteger = 1
        BigInteger r = new BigInteger("1");
        BigInteger shuzi2 = new BigInteger("2");
        BigInteger biao = new BigInteger("55555");
        BigInteger mm = m;
//        mm = mm.mod(n)
        while (e.compareTo(BigInteger.ZERO)==1){
            biao = e.mod(shuzi2);
            if(biao.compareTo(BigInteger.ONE)==0){
                r = r.multiply(mm).remainder(n);
            }
            mm =mm.multiply(mm).remainder(n);
            e = e.divide(shuzi2);
        }
        return r;
    }
////    字符串转换的
//
//    private String zifuchuanzhuanhuan(String string ) {
////        var text = "90004800049000500005125105292332032000097000980009900044000460003365292122906528100049000500005100048"
////        inttochar(string)
//        char changshu[] = string.toCharArray();
////            println(changshu[0]);
//        String jieguo = "";
//
//        for (int i=1; i>= 1 && i <= changshu.length-1; i+=5){
//            String danci1 = "";
//            for(int y = i; y>= i && y <= i+5; y++){
//                danci1 += changshu[y];
//            }
////                println("danci1"+danci1);
//            char shuchu = inttochar2(Integer.valueOf(danci1));
////                println("shuchu"+shuchu);
//            jieguo += shuchu;
//        }
////            println(jieguo);
//        return jieguo;
//    }
//
//    private char inttochar2(int Int) {
//        char bbb=(char)Int;
//        return bbb;
//    }
//    复制AESKey
    private View.OnClickListener fuzhiaesmimaOnClickListener= new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
            String n = sp.getString("AESKeyother", "NoData");
//                otherN.setText(n);
//            复制到剪贴板
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("n", n);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context.getApplicationContext(), "复制AESKey成功", Toast.LENGTH_SHORT).show();
        };
    };

//                <!--以下是修改的代码，加入非对称加密RSA，传递生成的固定密钥-->


    private CheckBox.OnCheckedChangeListener showPasswordCheckBoxOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            setShowPassword(b);
        }
    };

    private View.OnClickListener inputFileSelectButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectInputFile();
        }
    };

    private View.OnClickListener outputFileSelectButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectOutputFile();
        }
    };
    private View.OnClickListener progressDispayCancelButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CryptoThread.cancel();
        }
    };

    private CompoundButton.OnCheckedChangeListener deleteInputFileCheckboxOnCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            deleteInputFile = b;
        }
    };


    public void selectInputFile() {
        String initialFolder = null;
        ((MainActivity) getActivity()).pickFile(false, inputFileParentDirectory, null);
    }


    public void selectOutputFile() {
        DocumentFile initialFolder = outputFileParentDirectory;
        if (initialFolder == null) {
            initialFolder = inputFileParentDirectory;
        }
        ((MainActivity) getActivity()).pickFile(true, initialFolder, getDefaultOutputFileName());
    }

    public void setFile(DocumentFile file, String filename, boolean isOutput) {
        if (isOutput) {
            outputFileParentDirectory = file;
            outputFileName = filename;
        } else {
            inputFileParentDirectory = file;
            inputFileName = filename;
        }
        if (context != null) {
            updateFileUI(isOutput);
        }
    }


    private void updateFileUI(boolean isOutput) {
        String filePath;
        TextView filePathTextView;
        FileSelectButton fileSelectButton;
        View filePathUnderlineView;
        LinearLayout filePathLinearLayout;
        String filePathTextPrefix;
        if (isOutput) {
            filePath = StorageAccessFrameworkHelper.getDocumentFilePath(outputFileParentDirectory, outputFileName);
            filePathTextView = outputFilePathTextView;
            fileSelectButton = outputFileSelectButton;
            filePathUnderlineView = outputFilePathUnderlineView;
            filePathLinearLayout = outputFilePathLinearLayout;
            filePathTextPrefix = context.getString(R.string.output_file).concat(": ");
        } else {
            filePath = StorageAccessFrameworkHelper.getDocumentFilePath(inputFileParentDirectory, inputFileName);
            filePathTextView = inputFilePathTextView;
            fileSelectButton = inputFileSelectButton;
            filePathUnderlineView = inputFilePathUnderlineView;
            filePathLinearLayout = inputFilePathLinearLayout;
            filePathTextPrefix = context.getString(R.string.input_file).concat(": ");
        }
        int filePathTextViewVisibility = View.GONE;
        int filePathUnderlineViewVisibility = View.INVISIBLE;
        boolean fileSelectButtonMinimize = false;
        int gravity = Gravity.CENTER;
        if (!filePath.isEmpty()) {
            filePathTextViewVisibility = View.VISIBLE;
            filePathUnderlineViewVisibility = View.VISIBLE;
            fileSelectButtonMinimize = true;
            gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        }
        fileSelectButton.setMinimized(fileSelectButtonMinimize);
        SpannableString contentURISpannableString = new SpannableString(filePathTextPrefix.concat(filePath));
        contentURISpannableString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, filePathTextPrefix.length(), 0);
        filePathTextView.setText(contentURISpannableString);
        filePathTextView.setVisibility(filePathTextViewVisibility);
        filePathUnderlineView.setVisibility(filePathUnderlineViewVisibility);
        filePathLinearLayout.setGravity(gravity);
    }


    public void actionButtonPressed() {
        if (isValidElsePrintErrors()) {

            Intent intent = new Intent(context, CryptoService.class);
            intent.putExtra(CryptoService.INPUT_FILE_NAME_EXTRA_KEY, inputFileName);
            intent.putExtra(CryptoService.OUTPUT_FILE_NAME_EXTRA_KEY, outputFileName);
            GlobalDocumentFileStateHolder.setInputFileParentDirectory(inputFileParentDirectory);
            GlobalDocumentFileStateHolder.setOutputFileParentDirectory(outputFileParentDirectory);
            intent.putExtra(CryptoService.INPUT_FILENAME_KEY, inputFileName);
            intent.putExtra(CryptoService.OUTPUT_FILENAME_KEY, outputFileName);
            intent.putExtra(CryptoService.VERSION_EXTRA_KEY, SettingsHelper.getAESCryptVersion(getContext()));
            intent.putExtra(CryptoService.OPERATION_TYPE_EXTRA_KEY, operationMode);
            intent.putExtra(CryptoService.DELETE_INPUT_FILE_KEY, deleteInputFile);
            MainActivityFragment.password = passwordEditText.getText().toString().toCharArray();
            context.startService(intent);
        }
    }

   private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_FILE_PERMISSION_REQUEST_CODE);
        }
    }


    private void showError(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }


    private void showError(int stringId) {
        showError(context.getString(stringId));
    }

    private void setShowPassword(boolean showPassword) {
        this.showPassword = showPassword;
        int inputType;
        if (showPassword) {
            inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        } else {
            inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        }
        passwordEditText.setInputType(inputType);
        confirmPasswordEditText.setInputType(inputType);

        passwordEditText.setTypeface(Typeface.DEFAULT);
        confirmPasswordEditText.setTypeface(Typeface.DEFAULT);
    }


    private void enableEncryptionMode() {
        changeOperationTypeButtonAppearance(R.drawable.operation_mode_button_selected, R.drawable.operation_mode_button_selector);
        operationMode = CryptoThread.OPERATION_TYPE_ENCRYPTION;
        confirmPasswordEditText.setVisibility(View.VISIBLE);
        encryptModeButton.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        decryptModeButton.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        ((MainActivity) getActivity()).setFABIcon(R.drawable.ic_lock);
    }


    private void enableDecryptionMode() {
        changeOperationTypeButtonAppearance(R.drawable.operation_mode_button_selector, R.drawable.operation_mode_button_selected);
        operationMode = CryptoThread.OPERATION_TYPE_DECRYPTION;
        confirmPasswordEditText.setVisibility(View.INVISIBLE);
        encryptModeButton.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        decryptModeButton.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        ((MainActivity) getActivity()).setFABIcon(R.drawable.ic_unlock);
    }


    private void changeOperationTypeButtonAppearance(int encryptionDrawableId, int decryptionDrawableId) {
        encryptModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), encryptionDrawableId, null));
        decryptModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), decryptionDrawableId, null));
    }


   private String getDefaultOutputFileName() {
        String result = null;
        if (inputFileName != null) {
            String fileName = inputFileName;
            if (operationMode == CryptoThread.OPERATION_TYPE_ENCRYPTION) {
                result = fileName.concat(".aes");
            } else if (operationMode == CryptoThread.OPERATION_TYPE_DECRYPTION) {
                if (fileName.lastIndexOf('.') != -1 && fileName.substring(fileName.lastIndexOf('.')).equals(".aes")) {
                    result = fileName.substring(0, fileName.lastIndexOf('.'));
                } else {
                    result = "";
                }
            }
        }
        return result;
    }


    private boolean isValidElsePrintErrors() {
        boolean valid = true;
        if (inputFileParentDirectory == null || inputFileName == null) {
            valid = false;
            showError(R.string.no_input_file_selected);
        } else if (outputFileParentDirectory == null || outputFileName == null) {
            valid = false;
            showError(R.string.no_output_file_selected);
//        } else if (operationMode == CryptoThread.OPERATION_TYPE_ENCRYPTION && !passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
//            valid = false;
//            showError(R.string.passwords_do_not_match);
        } else if (inputFileParentDirectory.equals(outputFileParentDirectory) && inputFileName.equals(outputFileName)) {
            valid = false;
            showError(R.string.the_input_and_output_files_must_be_different);
        } else if (CryptoThread.operationInProgress) {
            valid = false;
            showError(R.string.another_operation_is_already_in_progress);
        }
        return valid;
    }


    public static String getAndClearPassword() {
        if (MainActivityFragment.password == null) {
            return null;
        }
        String password = String.valueOf(MainActivityFragment.password);
        Arrays.fill(MainActivityFragment.password, '\0');
        MainActivityFragment.password = null;
        return password;
    }


    private Bundle createOutStateBundle(Bundle systemOutStateBundle) {
        Bundle outState;
        if (systemOutStateBundle == null) {
            outState = new Bundle();
        } else {
            outState = systemOutStateBundle;
        }
        outState.putBoolean(SAVED_INSTANCE_STATE_SHOW_PASSWORD, showPassword);
        outState.putBoolean(SAVED_INSTANCE_STATE_OPERATION_MODE, operationMode);
        if (inputFileParentDirectory != null) {
            GlobalDocumentFileStateHolder.setSavedInputParentDirectoryForRotate(inputFileParentDirectory);
            outState.putString(SAVED_INSTANCE_STATE_INPUT_FILENAME, inputFileName);
            outState.putBoolean(SAVED_INSTANCE_STATE_DELETE_INPUT_FILE, deleteInputFile);
        }
        if (outputFileParentDirectory != null) {
            GlobalDocumentFileStateHolder.setSavedOutputParentDirectoryForRotate(outputFileParentDirectory);
            outState.putString(SAVED_INSTANCE_STATE_OUTPUT_FILENAME, outputFileName);
        }


        if (passwordEditText != null) {
            MainActivityFragment.password = passwordEditText.getText().toString().toCharArray();
        }
        return outState;
    }

    private void updateUI(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        setShowPassword(savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_SHOW_PASSWORD, showPassword));
        deleteInputFileCheckbox.setChecked(savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_DELETE_INPUT_FILE, deleteInputFile));
        if (savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_OPERATION_MODE, operationMode) == CryptoThread.OPERATION_TYPE_ENCRYPTION) {
            enableEncryptionMode();
        } else {
            enableDecryptionMode();
        }
        DocumentFile inputFileParentDirectory = GlobalDocumentFileStateHolder.getAndClearSavedInputParentDirectoryForRotate();
        DocumentFile outputFileParentDirectory = GlobalDocumentFileStateHolder.getAndClearSavedOutputParentDirectoryForRotate();
        if (inputFileParentDirectory != null) {
            String filename = savedInstanceState.getString(SAVED_INSTANCE_STATE_INPUT_FILENAME, null);
            setFile(inputFileParentDirectory, filename, false);
        }
        updateFileUI(false);
        if (outputFileParentDirectory != null) {
            String filename = savedInstanceState.getString(SAVED_INSTANCE_STATE_OUTPUT_FILENAME, null);
            setFile(outputFileParentDirectory, filename, true);
        }
        updateFileUI(true);
        String password = getAndClearPassword();
        if (password != null) {
            passwordEditText.setText(password);
        }
    }
}
