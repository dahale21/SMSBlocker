package dev.sagar.smsblocker.ux.activities;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import dev.sagar.smsblocker.Constants;
import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.broadcastreceivers.LocalSMSReceiver;
import dev.sagar.smsblocker.tech.broadcastreceivers.SMSReceiver;
import dev.sagar.smsblocker.tech.utils.ContactUtilSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVThreadAdapter;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.tech.utils.SMSUtil;

public class ThreadActivity extends AppCompatActivity implements
        RVThreadAdapter.Callback,
        LocalSMSReceiver.Callback{

    //Log Initiate
    LogUtil log = new LogUtil(this.getClass().getName());

    //Constants
    public static final String KEY_THREAD_ID = "THREAD_ID";

    //View
    private RecyclerView recyclerView;
    private Button btnSend;
    private EditText etMsg;

    //Java Android

    //Java Core
    private ArrayList<SMS> smses;
    private String threadId;
    private InboxUtil readerUtil = null;
    private SMSUtil smsUtil;
    private final int REQUEST_CODE_ALL_PERMISSIONS = 123;
    private PermissionUtilSingleton permissionIstance = PermissionUtilSingleton.getInstance();
    private LocalSMSReceiver smsReceiver = null;


    private void process()  {
        final String methodName =  "process()";
        log.debug(methodName, "Just Entered..");

        RVThreadAdapter adapter = new RVThreadAdapter(this, this, smses);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        boolean hasPermissions = permissionIstance.hasPermissions(this, Constants.PERMISSIONS);
        if(!hasPermissions) {
            askPermissions();
            return;
        }
        registerSMSReceiver();
        updateActionBar();

        log.debug(methodName, "Returning..");
    }


    private void getData(){
        final String methodName =  "getData()";
        log.debug(methodName, "Just Entered..");

        //From Previous Activity
        Bundle basket = getIntent().getExtras();
        threadId = basket.getString(KEY_THREAD_ID);

        //By Computation
        smses = readerUtil.getAllSMSFromTo(threadId);

        log.debug(methodName, "Returning..");
    }


    private void updateActionBar(){
        final String methodName =  "updateActionBar()";
        log.debug(methodName, "Just Entered..");

        String contact = ContactUtilSingleton.getContactName(this, threadId);
        getSupportActionBar().setTitle(contact);

        log.debug(methodName, "Returning..");
    }


    private void sendMsg(){
        final String methodName =  "sendMsg()";
        log.debug(methodName, "Just Entered..");

        String msg = etMsg.getText().toString();
        String phoneNo = threadId;
        smsUtil.sendSMS(phoneNo, msg);

        log.debug(methodName, "Returning..");
    }


    public void registerSMSReceiver(){
        final String methodName =  "registerSMSReceiver()";
        log.debug(methodName, "Just Entered..");

        registerReceiver(smsReceiver, new IntentFilter(SMSReceiver.LOCAL_SMS_RECEIVED));

        log.debug(methodName, "Returning..");
    }


    public void unregisterSMSReceiver(){
        final String methodName =  "unregisterSMSReceiver()";
        log.debug(methodName, "Just Entered..");

        unregisterReceiver(smsReceiver);

        log.debug(methodName, "Returning..");
    }


    private void askPermissions(){
        final String methodName =  "askPermissions()";
        log.debug(methodName, "Just Entered..");

        ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, REQUEST_CODE_ALL_PERMISSIONS);

        log.debug(methodName, "Returning..");
    }


    private void init(){
        final String methodName =  "init()";
        log.debug(methodName, "Just Entered..");

        recyclerView = (RecyclerView) findViewById(R.id.lv_sms);
        btnSend = (Button) findViewById(R.id.btn_send);
        etMsg = (EditText) findViewById(R.id.et_msg);

        if(readerUtil == null) readerUtil = new InboxUtil(this);
        smsUtil = new SMSUtil(this);
        smsReceiver = new LocalSMSReceiver(this);

        log.debug(methodName, "Returning..");
    }


    private void addListeners(){
        final String methodName =  "addListeners()";
        log.debug(methodName, "Just Entered..");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });

        log.debug(methodName, "Returning..");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String methodName =  "onCreate()";
        log.debug(methodName, "Just Entered..");

        setContentView(R.layout.activity_thread);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        init();
        getData();
        process();
        addListeners();

        log.debug(methodName, "Returning..");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        final String methodName =  "onRequestPermissionsResult()";
        log.debug(methodName, "Just Entered..");

        switch (requestCode) {
            case REQUEST_CODE_ALL_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    updateActionBar();
                } else {
                    // Permission Denied
                    Toast.makeText(ThreadActivity.this, "Permissions Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        log.debug(methodName, "Returning..");
    }


    @Override
    protected void onStart() {
        final String methodName =  "onStart()";
        log.debug(methodName, "Just Entered..");

        if(smsReceiver!=null)
            registerSMSReceiver();
        super.onStart();

        log.debug(methodName, "Returning..");
    }


    @Override
    protected void onStop() {
        final String methodName =  "onStop()";
        log.debug(methodName, "Just Entered..");

        unregisterSMSReceiver();
        super.onStop();

        log.debug(methodName, "Returning..");
    }

    @Override
    public void onSMSReceived(SMS sms) {
        final String methodName = "onSMSReceived()";
        log.info(methodName, "Just Entered..");

        Toast.makeText(this, "SMS Received From: "+sms.getFrom(), Toast.LENGTH_SHORT).show();

        log.info(methodName, "Returnind");
    }
}
