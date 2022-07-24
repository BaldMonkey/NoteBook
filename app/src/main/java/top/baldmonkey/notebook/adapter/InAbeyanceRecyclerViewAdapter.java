package top.baldmonkey.notebook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.baldmonkey.notebook.R;
import top.baldmonkey.notebook.bean.InAbeyance;
import top.baldmonkey.notebook.util.AlarmUtil;
import top.baldmonkey.notebook.util.DBHelper;
import top.baldmonkey.notebook.util.DBOperator;

/**
 * 这是继承自 RecyclerView.Adapter 的
 * 自定义的待办的 RecyclerView 的适配器 InAbeyanceAdapter
 */
public class InAbeyanceRecyclerViewAdapter extends RecyclerView.Adapter<InAbeyanceRecyclerViewAdapter.ViewHolder> {

    List<InAbeyance> inAbeyances; // 待办数据
    Context context; // 上下文
    OnItemClickListener onItemClickListener; // InAbeyance item 单击事件监听接口

    // 构造器
    public InAbeyanceRecyclerViewAdapter(List<InAbeyance> inAbeyances, Context context) {
        this.inAbeyances = inAbeyances; // 获取数据
        this.context = context; // 获取上下文
    }

    // 创建ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 获取待办item的视图
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_in_abeyance, parent, false);
        return new ViewHolder(itemView);
    }

    // 绑定
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        // 设置待办完成状态
        // status为 1 则代表已完成
        // 默认为不勾选
        holder.accomplish_status
                .setSelected(inAbeyances.get(position).getStatus() == 1);
        // 给待办完成状态框添加点击事件监听
        holder.parent_of_accomplish_status.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInabeyanceStatus(position); // 更新完成状态
            }
        });
        // 设置待办内容
        holder.in_abeyance_content.setText(inAbeyances.get(position).getContent());
        // 设置闹钟图标显示状态并且设置提醒日期
        if (!"".equals(inAbeyances.get(position).getDate_remind())) {
            holder.hasRemind.setSelected(true);
            // 显示为提醒日期
            holder.date_remind.setText(inAbeyances.get(position).getDate_remind());
        } else {
            holder.hasRemind.setSelected(false);
            holder.date_remind.setText("");
        }
        // 设置设置待办条目主要内容区域长按事件监听 --> 长按删除
        holder.in_abeyance_main.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onLongClick(View view) {
                deleteInAbeyance(position); // 删除该条待办
                return true;
            }
        });
        // 设置待办条目主要内容区域点击事件监听 --> 点击可以进入待办修改界面
        holder.in_abeyance_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取待办数据
                InAbeyance inAbeyance = inAbeyances.get(position);
                // 调用自定义的点击监听方法
                onItemClickListener.OnItemClick(
                        position, inAbeyance.get_id() + "",
                        inAbeyance.getContent(), inAbeyance.getDate_remind());
            }
        });
    }

    @Override
    public int getItemCount() {
        // 获取待办的数目用于控制条目数量
        return inAbeyances.size();
    }

    /**
     * 这是继承自 RecyclerView.ViewHolder 的
     * 自定义的 ViewHolder
     * 也是 InAbeyanceAdapter 的内部类
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout parent_of_accomplish_status; // 待办完成状态选择框的容器
        private final ImageView accomplish_status; // 待办完成状态
        private final LinearLayout in_abeyance_main; // 待办信息主要布局
        private final TextView in_abeyance_content; // 待办内容
        private final ImageView hasRemind; // 待办是否有提醒
        private final TextView date_remind; // 待办提醒日期

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent_of_accomplish_status = itemView.findViewById(R.id.in_abeyance_status);
            accomplish_status = itemView.findViewById(R.id.in_abeyance_check_box);
            in_abeyance_main = itemView.findViewById(R.id.in_abeyance_main);
            in_abeyance_content = itemView.findViewById(R.id.in_abeyance_content);
            hasRemind = itemView.findViewById(R.id.in_abeyance_alarm);
            date_remind = itemView.findViewById(R.id.date_remind);
        }
    }

    // 更新待办的完成状态
    public void updateInabeyanceStatus(int position) {
        int _id = inAbeyances.get(position).get_id(); // 获取要删除的待办的_id
        // 更改该条待办数据的 status
        inAbeyances.get(position).
                setStatus(1 - inAbeyances.get(position).getStatus());
        notifyItemChanged(position); // 通知适配器该条数据已更改
        // 更新数据库中的数据
        DBOperator.changeInAbeyanceStatus(context, _id);
    }

    // 删除一条待办
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void deleteInAbeyance(int position) {
        int _id = inAbeyances.get(position).get_id(); // 获取要删除的待办的_id
        // 获取提醒日期，用于判断是否需要取消对应的闹钟
        String date_remind = inAbeyances.get(position).getDate_remind();
        if (!"".equals(date_remind)) {
            AlarmUtil.cancelAlarm(context.getApplicationContext(), _id);
        }
        // 在数据库中删除该待办
        DBOperator.deleteInAbeyance(context, _id); // 执行删除操作
        inAbeyances.remove(position); // 从数据中移除
        notifyItemRemoved(position); // 通知适配器该条数据已移除
        // 通知适配器 position 受到影响的范围
        notifyItemRangeChanged(position, inAbeyances.size() - position);
        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
    }

    // 设置条目单击事件监听器
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // InAbeyance item 单击事件监听接口
    public interface OnItemClickListener {
        void OnItemClick(int position, String _id, String content, String date_remind);
    }
}
