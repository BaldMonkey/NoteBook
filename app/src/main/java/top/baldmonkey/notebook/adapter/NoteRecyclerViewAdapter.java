package top.baldmonkey.notebook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.baldmonkey.notebook.R;
import top.baldmonkey.notebook.bean.Note;
import top.baldmonkey.notebook.util.DBHelper;
import top.baldmonkey.notebook.util.DBOperator;

/**
 * 这是继承自 RecyclerView.Adapter 的
 * 自定义的笔记的适配器 MyRecyclerViewAdapter
 */
public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.MyRecyclerViewHolder> {

    List<Note> notes; // 存放笔记数据
    Context context; // 上下文
    OnItemClickListener onItemClickListener; // note item 单击事件监听器

    // 构造器
    public NoteRecyclerViewAdapter(List<Note> notes, Context context) {
        this.notes = notes; // 获取数据
        this.context = context; // 获取上下文
    }

    // 创建 ViewHolder
    @NonNull
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        // 获取笔记条目视图
        View itemView = View.inflate(context, R.layout.item_note, null);
        return new MyRecyclerViewHolder(itemView);
    }

    // 为 ViewHolder 绑定数据
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        // 获取标题内容
        String title = notes.get(position).getTitle();
        String content = notes.get(position).getContent();
        if (title.length() == 0) {
            holder.note_title.setVisibility(View.GONE);
        } else {
            holder.note_title.setVisibility(View.VISIBLE);
            holder.note_title.setText(title);
        }
        if (content.length() == 0) {
            holder.note_content.setVisibility(View.GONE);
        } else {
            holder.note_content.setVisibility(View.VISIBLE);
            holder.note_content.setText(content);
        }
        // 设置笔记更新日期
        holder.note_date.setText(notes.get(position).getDate_updated());
        // 设置笔记条目单击事件监听 --> 点击进入笔记修改界面
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用单击事件监听器
                if (onItemClickListener != null) {
                    // 获取笔记标题和内容
                    String title = notes.get(position).getTitle();
                    String content = notes.get(position).getContent();
                    // 获取笔记的创建日期和id
                    String date_created = notes.get(position).getDate_created();
                    String note_id = notes.get(position).get_id() + "";
                    // 调用监听器的处理方法
                    onItemClickListener.onItemClick(position, title, content, date_created, note_id);
                }
            }
        });
        // 为笔记条目设置长按事件监听器 --> 长按回收
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                recycleNote(position); // 回收笔记
                return true;
            }
        });
    }

    // 获取数据的总数
    @Override
    public int getItemCount() {
        // 获取笔记数目
        return notes.size();
    }

    // 回收一条笔记
    public void recycleNote(int position) {
        // 获取要回收的笔记的_id
        int _id = notes.get(position).get_id();
        // 从数据库中更改回收状态
        DBOperator.changeNoteRecycleStatus(context, _id);
        notifyItemRemoved(position); // 通知哪一条数据数据被回收
        notes.remove(position); // 在条目中移除被回收的笔记
        // 通知适配器受到影响的position的位置
        notifyItemRangeChanged(position, notes.size() - position);
        Toast.makeText(context, "移入回收站",Toast.LENGTH_SHORT).show();
    }

    // 设置单击事件监听器
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * 这是继承自 RecyclerView.ViewHolder 的
     * 自定义的 MyRecyclerViewHolder
     * 也是
     */
    public static class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

        final TextView note_title; // 笔记标题
        final TextView note_content; // 笔记内容
        final TextView note_date; // 笔记修改日期

        public MyRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            // 获取控件
            note_title = itemView.findViewById(R.id.note_title);
            note_content = itemView.findViewById(R.id.note_content);
            note_date = itemView.findViewById(R.id.note_date);
        }
    }

    // note item 单击事件监听接口
    public interface OnItemClickListener {
        void onItemClick(int position, String title, String content, String date_created, String note_id);
    }
}



