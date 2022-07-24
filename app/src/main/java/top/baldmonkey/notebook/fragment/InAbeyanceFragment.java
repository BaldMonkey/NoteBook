package top.baldmonkey.notebook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import top.baldmonkey.notebook.R;
import top.baldmonkey.notebook.activity.InAbeyanceDialogActivity;
import top.baldmonkey.notebook.adapter.InAbeyanceRecyclerViewAdapter;
import top.baldmonkey.notebook.bean.InAbeyance;
import top.baldmonkey.notebook.util.DBHelper;
import top.baldmonkey.notebook.util.DBOperator;

/**
 * 这是继承自 Fragment 的
 * 自定义的 InAbeyanceFragment类
 */
public class InAbeyanceFragment extends Fragment {

    private View rootView; // 根视图
    List<InAbeyance> inAbeyances; // 存放 InAbeyance 数据
    EditText search_inAbeyance; // 搜索框
    RecyclerView recyclerView; // RecyclerView控件
    InAbeyanceRecyclerViewAdapter inAbeyanceRecyclerViewAdapter; // 适配器
    ImageView add_inAbeyance; // 添加待办按钮

    private InAbeyanceFragment() {
        // 私有的空构造器,不让外界直接调用,而是通过 newInstance 方法获取实例
    }

    /**
     * 通过这个方法在外界获取 InAbeyanceFragment 实例
     * @return InAbeyanceFragment fragment
     */
    public static InAbeyanceFragment newInstance() {
        return new InAbeyanceFragment();
    }

    // 初始化
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化根视图 rootView
     * 并且在通过返回键返回该 Fragment 所在 Activity 时也会调用
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return rootView
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_in_abeyance, container, false);
            initView();
        }
        return rootView;
    }

    // 在返回到该Fragment时调用
    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    // 初始化根视图的控件
    private void initView() {
        // 获取搜索框
        search_inAbeyance = rootView.findViewById(R.id.search_in_abeyance_editText);
        // 为搜索框添加文本改变监听事件
        search_inAbeyance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            // 输入框内容改变后触发
            @Override
            public void afterTextChanged(Editable editable) {
                String keyWord = search_inAbeyance.getText().toString();
                inAbeyances = queryInAbeyancesData(keyWord);
                bindData();
            }
        });
        // 获取全部待办数据
        inAbeyances = getInAbeyancesData();
        bindData(); // 绑定数据
        // 获取添加按钮
        add_inAbeyance = rootView.findViewById(R.id.add_in_abey_ance);
        // 为添加按钮添加单击事件监听 --> 单击到达添加待办页面
        add_inAbeyance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建意图
                Intent intent = new Intent(getActivity(), InAbeyanceDialogActivity.class);
                // 执行意图
                startActivity(intent);
            }
        });
    }

    // 绑定数据
    private void bindData() {
        // 获取RecyclerView
        recyclerView = rootView.findViewById(R.id.in_abeyance_recyclerview);
        // 设置为线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 创建适配器
        inAbeyanceRecyclerViewAdapter = new InAbeyanceRecyclerViewAdapter(inAbeyances, getActivity());
        // 为待办条目设置单击事件监听器
        inAbeyanceRecyclerViewAdapter.setOnItemClickListener(new InAbeyanceRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position, String _id, String content, String date_remind) {
                // 创建 Bundle
                Bundle bundle = new Bundle();
                // 添加数据
                bundle.putString("_id", _id);
                bundle.putString("content", content);
                bundle.putString("date_remind", date_remind);
                // 创建意图
                Intent intent = new Intent(getActivity(), InAbeyanceDialogActivity.class);
                // 将 bundle 放入 intent
                intent.putExtras(bundle);
                startActivity(intent); // 执行意图
            }
        });
        // 设置适配器
        recyclerView.setAdapter(inAbeyanceRecyclerViewAdapter);
    }

    // 获取全部待办信息
    public List<InAbeyance> getInAbeyancesData() {
        // 在数据库中全部查询待办数据
        return DBOperator.getInAbeyanceData(getActivity());
    }

    // 根据关键字查询信息
    public List<InAbeyance> queryInAbeyancesData(String keyWord) {
        // 根据关键字在数据库中进行搜索待办
        return DBOperator.queryInAbeyanceData(getActivity(), keyWord);
    }
}