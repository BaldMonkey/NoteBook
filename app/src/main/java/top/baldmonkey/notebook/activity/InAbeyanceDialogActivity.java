package top.baldmonkey.notebook.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;

import java.util.Calendar;
import java.util.Date;

import top.baldmonkey.notebook.R;
import top.baldmonkey.notebook.bean.InAbeyance;
import top.baldmonkey.notebook.util.AlarmUtil;
import top.baldmonkey.notebook.util.DBHelper;
import top.baldmonkey.notebook.util.DBOperator;
import top.baldmonkey.notebook.util.DateUtil;

/**
 * 这是继承自 AppCompatActivity 的 InAbeyanceDialogActivity
 * 用于充当添加或修改待办的对话框
 */
public class InAbeyanceDialogActivity extends AppCompatActivity {

    TextView _id; // 待办的 _id
    EditText content; // 代办的内容
    LinearLayout set_remind; // 设置提醒按钮
    ImageView setting_status; // 闹钟设置状态
    TextView date_remind; // 提醒日期
    TextView accomplish; // 完成按钮
    Bundle bundle; // 传入的数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_abeyance_dialog);
        // 初始化控件
        _id = findViewById(R.id.in_abeyance_id);
        content = findViewById(R.id.in_abeyance_edittext);
        set_remind = findViewById(R.id.set_remind);
        // 为设置提醒按钮创建点击事件监听
        set_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!"".equals(date_remind.getText().toString())) {
                    // 如果提醒日期不为空,则改变”设置提醒“前面的小闹钟状态
                    setting_status.setSelected(true);
                }
                // 设置时间选择器开始和末尾时间
                // 开始时间为当前时间
                Calendar startTime = Calendar.getInstance();
                // 设置为0秒
                startTime.set(Calendar.SECOND, 0);
                // 末尾时间暂定为 2030年12月31日 下午 23:59
                Calendar endTime = Calendar.getInstance();
                endTime.set(2030, 12, 31, 23, 59);
                // 创建并打开日期时间选择器
                TimePickerView pickerView = new TimePickerView.Builder(
                        InAbeyanceDialogActivity.this,
                        new TimePickerView.OnTimeSelectListener() {
                    // 选择时间后触发该事件
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        // 将日期时间格式化
                        String time = DateUtil.getFormatTime(date);
                        // 改变”设置提醒“小闹钟图标的状态
                        setting_status.setSelected(true);
                        // 为”设置提醒“后的日期文本控件设置日期
                        date_remind.setText(time);
                    }
                })
                        // 设置不显示秒
                        .setType(new boolean[]{true,true,true,true,true,false})
                        // 设置”确定“和”取消“按钮文本和颜色
                        .setCancelText("取消")
                        .setCancelColor(getResources().getColor(R.color.yellow))
                        .setSubmitText("确定")
                        .setSubmitColor(getResources().getColor(R.color.yellow))
                        .setTitleText("设置提醒日期") // 设置标题
                        .setTitleBgColor(getResources().getColor(R.color.white))
                        .isCyclic(true) // 是否循环滚动
                        .setRangDate(startTime, endTime) // 设置起止日期
                        .isCenterLabel(false) // 是否仅中间日期显示标签
                        .setOutSideCancelable(true) // 点击外界是否关闭
                        .build(); // 创建
                    pickerView.show(); // 显示
            }
        });
        // 获取设置提醒前的闹钟图标
        setting_status = findViewById(R.id.setting_status);
        // 获取提醒日期文本框
        date_remind = findViewById(R.id.date_remind_show);
        // 获取完成按钮
        accomplish = findViewById(R.id.input_accomplish);
        // 为完成按钮设置点击事件
        accomplish.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View view) {
                // 获取控件的值
                String content_str = content.getText().toString();
                String date_remind_str = date_remind.getText().toString();
                String _id_str = _id.getText().toString();
                // 声明 InAbeyance 对象
                InAbeyance inAbeyance;
                // 根据是否有id判断是添加还是修改
                if ("".equals(_id_str)) { // _id 为空,代表是添加操作
                    // 获取 当前时间作为创建时间
                    String date_created_str = DateUtil.getCurrentTime();
                    inAbeyance = new InAbeyance(content_str, date_remind_str, date_created_str);
                    // 新增待办，并且获取_id
                    int _id = addInAbeyance(inAbeyance);
                    // 判断是否设置了提醒日期
                    if (!"".equals(date_remind_str)){
                        // 定时发送提醒
                        AlarmUtil.setAlarm(getApplicationContext(),
                                DateUtil.getTimeMillis(date_remind_str),
                                _id, content_str);
                    }
                    // 显示提示
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                } else { // _id不为空,代表是修改操作
                    int _id_int = Integer.parseInt(_id_str);
                    inAbeyance = new InAbeyance(_id_int, content_str,
                            date_remind_str);
                    updateInAbeyance(inAbeyance); // 更新一条待办
                    // 判断是否设置了提醒日期
                    if (!"".equals(date_remind_str)) {
                        // 更新定时发送通知的时间
                        AlarmUtil.setAlarm(getApplicationContext(),
                                DateUtil.getTimeMillis(date_remind_str),
                                _id_int, content_str);
                    }
                    // 显示提醒日期
                    Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
                }
                MainActivity.status = 1;
                onBackPressed();
            }
        });
        // 设置完成按钮默认不可点击
        accomplish.setClickable(false);
        // 为内容框获取焦点
        content.requestFocus();
        // 弹出键盘
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        // 为内容输入框添加文本改变事件监听
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            // 在文本改变后触发
            @Override
            public void afterTextChanged(Editable editable) {
                // 获取输入框控件
                EditText editText = InAbeyanceDialogActivity.this.content;
                // 获取完成按钮控件
                TextView textView = InAbeyanceDialogActivity.this.accomplish;
                // 获取输入框内容
                String content = editText.getText().toString();
                if ("".equals(content)) { // 内容为空
                    // 设置完成按钮为灰色
                    textView.setTextColor(getResources().getColor(R.color.mid_grey));
                    // 设置为不可点击
                    textView.setClickable(false);
                } else {
                    // 设置完成按钮为黄色
                    textView.setTextColor(getResources().getColor(R.color.yellow));
                    // 设置为可以点击
                    textView.setClickable(true);
                }
            }
        });
        // 获取传入的数据
        bundle = getIntent().getExtras();
        if (bundle != null) {
            _id.setText(bundle.getString("_id"));
            content.setText(bundle.getString("content"));
            content.setSelection(content.getText().length());
            // 如果提醒日期不为空
            if (!"".equals(bundle.getString("date_remind"))) {
                // 改变”设置提醒“前面的闹钟显示状态
                setting_status.setSelected(true);
                // 显示提醒日期
                date_remind.setText(bundle.getString("date_remind"));
            }
        }
    }

    // 添加一条待办
    public int addInAbeyance(InAbeyance inAbeyance){
        // 在数据库中执行添加操作, 并返回 _id
        return DBOperator.add_in_abeyance(getApplicationContext(), inAbeyance);
    }
    // 更新一条待办
    public void updateInAbeyance(InAbeyance inAbeyance) {
        // 在数据库中更新待办
        DBOperator.updateInAbeyance(getApplicationContext(), inAbeyance);
    }
}