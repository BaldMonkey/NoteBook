package top.baldmonkey.notebook.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * 这是继承自 FragmentStateAdapter 的
 * 自定义的 MyFragmentPagerAdapter
 */
public class MyFragmentPagerAdapter extends FragmentStateAdapter {

    List<Fragment> fragmentList; // Fragment数组

    // 初始化
    public MyFragmentPagerAdapter(@NonNull FragmentManager fragmentManager,
                                  @NonNull Lifecycle lifecycle,
                                  List<Fragment> fragmentList) {
        super(fragmentManager, lifecycle);
        this.fragmentList = fragmentList; // 为Fragment数组赋值
    }
    // 根据 position 创建 Fragment
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    // 获取 Fragment 总数
    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
