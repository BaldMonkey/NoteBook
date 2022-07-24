package top.baldmonkey.notebook.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import top.baldmonkey.notebook.R;
import top.baldmonkey.notebook.bean.Note;
import top.baldmonkey.notebook.util.DBOperator;

/**
 * 这是继承自 AppCompatActivity 的 EditActivity
 */
public class EditActivity extends AppCompatActivity {

    Bundle bundle; // 传来的数据
    TextView date_created; // 创建日期文本框
    TextView note_id; // 一个隐藏的文本框用于标记 note 的 _id
    EditText title; // 标题编辑框
    EditText content; // 内容编辑框
    ImageView backButton; // 返回按钮
    ImageView accomplishButton; // 完成按钮

    // 初始化方法
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 透明状态栏
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
        // 设置字体颜色
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_edit);
        // 初始化获取控件
        note_id = findViewById(R.id.note_id);
        title = findViewById(R.id.note_title_edit);
        content = findViewById(R.id.note_content_edit);
        backButton  = findViewById(R.id.back);
        accomplishButton = findViewById(R.id.accomplish);
        // 为返回按钮添加监听
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用系统返回键
                MainActivity.status = 0;
                EditActivity.this.onBackPressed();
            }
        });
        // 为完成按钮添加监听
        accomplishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取控件 _id,标题,内容
                String s_id = note_id.getText().toString();
                String title = EditActivity.this.title.getText().toString();
                String content = EditActivity.this.content.getText().toString();
                // 声明一个 Note 对象
                Note note;
                // 对内容进行判断
                if ("".equals(s_id) && (!"".equals(title) || !"".equals(content))) {
                    // _id为空代表是添加笔记
                    // 且不允许标题和内容同时为空
                    note = new Note(title, content);
                    addNote(note); // 执行添加
                } else if (!"".equals(s_id) && (!"".equals(title) || !"".equals(content))){
                    // _id不为空代表为修改数据
                    // 且不允许标题和内容同时为空
                    int _id = Integer.parseInt(s_id);
                    note = new Note(_id,title, content);
                    updateNote(note); // 执行更新
                }
                // 调用系统返回键
                EditActivity.this.onBackPressed();
            }
        });

        bundle = getIntent().getExtras(); // 获取传入的笔记数据

        if (bundle != null) {
            // 将传来的数据与控件绑定
            date_created = findViewById(R.id.date_created);
            title.setText(bundle.getString("note_title"));
            content.setText(bundle.getString("note_content"));
            note_id.setText(bundle.getString("note_id"));
            String tips = "创建于：" + bundle.getString("date_created");
            date_created.setText(tips);
            bundle.clear(); // 清除 bundle
        }
    }

    // 添加一条笔记
    public void addNote(Note note) {
        // 在数据库中添加笔记
        DBOperator.addNote(this, note);
        // 进行提示
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
    }

    // 更新一条笔记
    public void updateNote(Note note) {
        // 在数据库中更新笔记
        DBOperator.updateNote(this, note);
        // 提示
        Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
    }
}