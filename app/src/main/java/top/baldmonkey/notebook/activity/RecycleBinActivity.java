package top.baldmonkey.notebook.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import top.baldmonkey.notebook.R;
import top.baldmonkey.notebook.adapter.RecycledNoteRecyclerViewAdapter;
import top.baldmonkey.notebook.bean.Note;
import top.baldmonkey.notebook.util.DBOperator;

/**
 * 这是继承自 AppCompatActivity 的
 * 自定义的 RecycleBinActivity
 */
public class RecycleBinActivity extends AppCompatActivity {

    List<Note> notes; // 存放回收的笔记数据

    ImageView back; // 返回按钮
    EditText recycle_note_search; // 回收笔记搜索框
    RecyclerView recyclerView; // 盛放回收笔记

    // 初始化
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 透明状态栏
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.light_grey));
        // 设置状态栏字体颜色
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_recycle_bin);
        // 获取返回按钮
        back = findViewById(R.id.back_of_recycle_bin);
        // 为返回按钮添加点击事件监听
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecycleBinActivity.this.onBackPressed();
            }
        });
        // 获取搜索框
        recycle_note_search = findViewById(R.id.search_recycle_note_editText);
        // 给搜索框添加文本改变事件监听
        recycle_note_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            // 文本改变时触发
            @Override
            public void afterTextChanged(Editable editable) {
                String keyWord = RecycleBinActivity.this
                        .recycle_note_search.getText().toString();
                // 获取数据
                notes = findNotesByKeyWord(keyWord);
                // 绑定数据
                bindData();
            }
        });
        // 获取数据
        notes = getAllNotes();
        // 绑定数据
        bindData();
    }

    // 绑定数据
    public void bindData() {
        // 获取RecyclerView
        recyclerView = findViewById(R.id.recycle_note_recyclerview);
        // 设置瀑布网格布局
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // 创建适配器
        RecycledNoteRecyclerViewAdapter adapter =
                new RecycledNoteRecyclerViewAdapter(notes, this);
        // 设置适配器
        recyclerView.setAdapter(adapter);
    }

    // 查询全部已被回收的数据
    public List<Note> getAllNotes() {
        return DBOperator.getNotesData(this, 1);
    }

    // 按照关键字查询已被回收的笔记
    public List<Note> findNotesByKeyWord(String keyWord) {
        return DBOperator.queryNotesData(this, keyWord, 1);
    }
}