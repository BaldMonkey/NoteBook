package top.baldmonkey.notebook.fragment;

import android.app.Activity;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import top.baldmonkey.notebook.R;
import top.baldmonkey.notebook.activity.EditActivity;
import top.baldmonkey.notebook.adapter.NoteRecyclerViewAdapter;
import top.baldmonkey.notebook.bean.Note;
import top.baldmonkey.notebook.util.DBHelper;
import top.baldmonkey.notebook.util.DBOperator;

/**
 * 这是继承自Fragment的自定义的NoteFragment类
 * 可以通过newInstance方法获取该NoteFragment实例
 */
public class NoteFragment extends Fragment {

    private View rootView; // rootView 根视图
    private List<Note> notes; // 笔记数据
    EditText search_note; // 搜索框
    RecyclerView recyclerView; // RecyclerView
    NoteRecyclerViewAdapter myRecyclerViewAdapter; // RecyclerView适配器
    public ImageView add_note; // 添加按钮

    private NoteFragment() {
        // 私有的空构造器,不让外界直接调用,而是通过 newInstance 方法获取实例
    }

    /**
     * 通过这个方法可以在外部获取NoteFragment类的实例
     * @return NoteFragment
     */
    public static NoteFragment newInstance() {
        return new NoteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 通过这个方法来初始化NoteFragment的根视图
     * 并且在通过返回键返回该Fragment所在Activity时也会调用
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View rootView 这是这个Fragment的根视图
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_note, container, false);
            initView();
        }
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
//        Log.e("xxx", "onResume: xxx" );
        initView();
    }

    // 初始化该Fragment的控件
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView() {
//        Log.e("xxx", "NoteFragment--->initView: 走到这里");
        // 获取搜索框控件
        search_note = rootView.findViewById(R.id.search_note_editText);


        // 为搜索框添加事件监听,在搜索框的值改变时触发
        search_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            // 在内容改变后触发该方法
            @Override
            public void afterTextChanged(Editable editable) {
                // 获取搜索框内容作为搜索关键字
                String keyWord = search_note.getText().toString();
                // 查询与关键字匹配的笔记
                notes = queryNotesData(keyWord);
                // 更新所绑定的数据
                bindData();
            }
        });

        // 获取全部Note数据
        notes = getNotesData();
        bindData();// 将数据绑定到RecyclerView中

        // 获取添加按钮控件
        add_note = rootView.findViewById(R.id.add_note);
        // 为添加按钮添加单击事件监听
        add_note.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                startActivity(intent);
            }
        });
    }

    // 将数据绑定到RecyclerView中
    public void bindData() {
        // 获取RecyclerView
        recyclerView  = rootView.findViewById(R.id.note_recyclerview);
        // 采用瀑布网格布局
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
        // 设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        // 新建适配器
        myRecyclerViewAdapter = new NoteRecyclerViewAdapter(notes, getActivity());
        // 为适配器的每个条目设置单击事件监听
        // 与 MyRecyclerViewAdapter 中的 OnItemClickListener 接口结合使用
        myRecyclerViewAdapter.setOnItemClickListener(new NoteRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String title, String content, String date_created, String note_id) {
                Bundle bundle = new Bundle();// 新建一个 Bundle 来传递数据
                bundle.putString("note_title", title); // 放入笔记标题
                bundle.putString("note_content", content); // 放入笔记内容
                bundle.putString("date_created", date_created); // 放入创建日期
                bundle.putString("note_id", note_id); // 放入笔记 _id
                // 创建意图,跳转到 EditActivity
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtras(bundle); // 将 Bundle 添加到 Intent 中
                startActivity(intent); // 进行跳转
            }
        });
        // 为RecyclerView设置适配器
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }

    // 通过这个方法来获取全部Note数据
    public List<Note> getNotesData() {
        // 查询全部Note数据并返回
        return DBOperator.getNotesData(getActivity(), 0);
    }

    // 通过这个方法来查询符合关键字的note数据
    public List<Note> queryNotesData(String keyWord) {
        // 按照关键字查询Note并返回
        return DBOperator.queryNotesData(getActivity(), keyWord, 0);
    }
}