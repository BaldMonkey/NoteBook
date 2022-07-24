package top.baldmonkey.notebook.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import top.baldmonkey.notebook.R;
import top.baldmonkey.notebook.adapter.MyFragmentPagerAdapter;
import top.baldmonkey.notebook.fragment.InAbeyanceFragment;
import top.baldmonkey.notebook.fragment.NoteFragment;

/**
 * 这是继承自 AppCompatActivity 的
 * 实现了 View.OnClickListener 点击事件接口的 MainActivity
 * 也是程序的入口
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager2 viewPager; // ViewPager2
    TextView note; // 顶部笔记标签
    TextView in_abeyance; // 顶部待办标签
    ImageView recycle_bin; // 回收站按钮
    // 标志当前显示笔记页面或者待办页面, 0 为笔记, 1 为待办
    public static int status = 0;

    // 初始化
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        // 透明状态栏
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.light_grey));
        // 设置状态栏字体颜色
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);
        note = findViewById(R.id.note); // 获取笔记文本控件
        note.setOnClickListener(this); // 添加单击事件监听
        in_abeyance = findViewById(R.id.in_abeyance); // 获取待办文本控件
        in_abeyance.setOnClickListener(this); // 添加单击事件监听
        recycle_bin = findViewById(R.id.recycle_bin);
        // 给回收站按钮添加点击事件
        recycle_bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建意图
                Intent intent = new Intent(
                        MainActivity.this, RecycleBinActivity.class);
                // 执行意图,跳转到回收站页面
                startActivity(intent);
            }
        });
        initViewPager(); // 初始化 ViewPager2
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新返回本页面时设置显示的页面
        viewPager.setCurrentItem(status);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 离开本页面时调用
        status = viewPager.getCurrentItem();
    }

    // 初始化 ViewPager2 方法
    private void initViewPager() {
        // 获取 mainViewPager2
        viewPager = findViewById(R.id.mainViewPager2);
        // 创建 FragMent 数组
        List<Fragment> fragmentList = new ArrayList<>();
        // 添加笔记的 Fragment
        fragmentList.add(NoteFragment.newInstance());
        // 添加代办的 Fragment
        fragmentList.add(InAbeyanceFragment.newInstance());
        // 创建 Fragment 适配器
        MyFragmentPagerAdapter myFragmentPagerAdapter =
                new MyFragmentPagerAdapter(getSupportFragmentManager(),
                        getLifecycle(), fragmentList);
        // 给 ViewPager2 设置适配器
        viewPager.setAdapter(myFragmentPagerAdapter);
        // Fragment 切换事件监听
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changeFragment(position);
            }
        });
    }

    // 切换 Fragment
    @SuppressLint("NonConstantResourceId")
    private void changeFragment(int position) {
        switch (position) {
            case R.id.note: // 代表滑动切换到NoteFragment
                viewPager.setCurrentItem(0);
            case 0: // 代表点击 ”笔记“
                note.setTextColor(note.getResources().getColor(R.color.black));
                in_abeyance.setTextColor(in_abeyance.getResources().getColor(R.color.dark_grey));
                recycle_bin.setVisibility(View.VISIBLE);
                break;
            case R.id.in_abeyance: // 代表滑动切换到InAbeyanceFragment
                viewPager.setCurrentItem(1);
            case 1: // 代表点击 ”待办“
                note.setTextColor(note.getResources().getColor(R.color.dark_grey));
                in_abeyance.setTextColor(in_abeyance.getResources().getColor(R.color.black));
                recycle_bin.setVisibility(View.INVISIBLE);
                break;
            default:break;
        }
    }

    // 重写点击事件 处理方法
    @Override
    public void onClick(View view) {
        // 调用切换 Fragment 方法
        changeFragment(view.getId());
    }
}