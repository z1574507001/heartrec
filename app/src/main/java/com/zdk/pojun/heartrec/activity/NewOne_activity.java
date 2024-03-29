package com.zdk.pojun.heartrec.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zdk.pojun.heartrec.AppData;
import com.zdk.pojun.heartrec.R;
import com.zdk.pojun.heartrec.utils.Constant;
import com.zdk.pojun.heartrec.utils.DbManager;
import com.zdk.pojun.heartrec.entity.Text_Entity;
import com.zdk.pojun.heartrec.utils.SqliteHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zero on 2017/2/16.
 */

public class NewOne_activity extends Activity implements OnClickListener {

    private Context context = this;

    private Intent intent;
    private String id = "";

    private SqliteHelper sqliteHelper;
    //    注册控件
    private EditText editText_substance;
    private TextView textView_time, btn_save, btn_cancel;
    private LinearLayout layout;

    //    数据变量
    private String nowtime, substance;

//    自动保存计时器
//    final Handler handler = new Handler();


    @Override
    public void onBackPressed() {
        cancel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newone_layout);

        //        获取时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date curtime = new Date(System.currentTimeMillis());//获取当前时间
        nowtime = formatter.format(curtime);

        initView();//初始化界面
        AppData.setFinalPage(1);

//        获取intent
        intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra("id")) {
//            获取intent中的值
                id = intent.getStringExtra("id");
//            为控件赋值
                initData(id);
            }
        }

        //        沉浸式状态栏
        // 4.4以上版本开启
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);


            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);

            // 状态栏背景色
            if (Build.VERSION.SDK_INT < 23) {
                tintManager.setStatusBarTintResource(R.color.colorAccent);
            } else {
                tintManager.setTintColor(getColor(R.color.colorAccent));
            }
        }


    }

    //    沉浸式状态栏
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initView() {
        sqliteHelper = DbManager.getIntance(this);
//        option_helper = new OptionHelper(context);
        //        实例化控件
        editText_substance = (EditText) findViewById(R.id.edittext_substance);
        btn_save = (TextView) findViewById(R.id.btn_save);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        textView_time = (TextView) findViewById(R.id.text_time);
        layout = (LinearLayout) findViewById(R.id.layout_newone);

//        点击监听
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        editText_substance.addTextChangedListener(textWatcher);
        textView_time.setText(nowtime);
    }


//    点击事件


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_save:
                save();
                finish();
                break;
        }
    }

//    取消

    public void cancel() {
        if (!"".equals(id)) {//id不为空，界面从已存在的条目创建
            //查询是否更改过内容
            SQLiteDatabase db = sqliteHelper.getWritableDatabase();
            Cursor cursor = null;
            String sql = "select * from " + Constant.TABLE_NAME + " where " + Constant.ID + "=" + id + ";";
            cursor = DbManager.selectDataBySql(db, sql, null);
            if (cursor != null) {
                cursor.moveToNext();
                Text_Entity text_entity = new Text_Entity();
                text_entity.setId(cursor.getString(cursor.getColumnIndex("id")));
                text_entity.setTime(cursor.getString(cursor.getColumnIndex("time")));
                text_entity.setSubstance(cursor.getString(cursor.getColumnIndex("substance")));

                db.close();//关闭数据库
                if (text_entity.getSubstance().equals(substance)) {//没有改变内容
                    finish();//直接关闭
                } else {
                    showDialog();//询问是否保存
                }
            }
        } else {
            //界面为新建界面
            if (substance == null || "".equals(substance)) {//内容为空，用户没有输入
                finish();
            } else {
                showDialog();
            }
        }
    }

    public void showDialog() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("是否保存已经编辑的内容？")
                .setIcon(R.drawable.ic_dialog_info)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“保存”后的操作，保存数据
                        save();
                        finish();
                    }
                })
                .setNegativeButton("不保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“不保存”后的操作，关闭界面
                        finish();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“取消”后的操作，不做任何操作
                    }
                })
                .show();
        WindowManager.LayoutParams params =
                dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = (int) getResources().getDimension(R.dimen.dialog_height);
        params.verticalMargin = getResources().getDimension(R.dimen.dialog_verticalMargin);
        dialog.getWindow().setAttributes(params);
    }

    private String escape(String str) {
        if (str.contains("'")) {
            str = str.replace("'", "''");
        }
        return str;
    }

    //    保存
    public void save() {
        if (substance != null && !"".equals(substance)) {
            SQLiteDatabase db = sqliteHelper.getWritableDatabase();//打开数据库
            substance = escape(substance);
            if (id != null && !id.equals("")) {
                try {
                    String sql = "update " + Constant.TABLE_NAME + " set " + "" + Constant.SUBSTANCE + "='" + substance + "'," + Constant.TIME + "=\"" + nowtime + "\" where " + Constant.ID + "=" + id + ";";
                    DbManager.execSQL(db, sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    String sql = "insert into " + Constant.TABLE_NAME + " values(null,'" + substance + "','" + nowtime + "');";
                    DbManager.execSQL(db, sql);
                } catch (Exception e) {
                }
            }
            db.close();//关闭数据库
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            //文本内容变化时

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            //文本内容变化前

        }

        @Override
        public void afterTextChanged(Editable s) {
            //文本内容变化后
            substance = editText_substance.getText().toString();
        }
    };

    /**
     * 查询并赋值控件
     */
    private void initData(String id) {
        //查询数据
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        String sql = "select * from " + Constant.TABLE_NAME + " where " + Constant.ID + "=" + id + ";";
        Cursor cursor = DbManager.selectDataBySql(db, sql, null);
        if (cursor != null) {
//                为控件赋值
            if (cursor.moveToFirst()) {
                editText_substance.setText(cursor.getString(cursor.getColumnIndex("substance")));
            }
        }
        db.close();//关闭数据库
    }

}
