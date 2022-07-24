package top.baldmonkey.notebook.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 这是继承自 SQLiteOpenHelper 的
 * 自定义的 MyDBHelper
 * 可以通过这个类获取数据库
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "note_book.db"; // 数据库名称
    private static DBHelper instance; // 实例

    // 对外提供获取实例的方法
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context, DBNAME, null, 1);
        }
        return instance;
    }

    // 私有化构造函数
    private DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 数据库初始化
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建note表的sql语句
        String create_table_note = "create table note(" +
                "_id integer primary key autoincrement," +
                "title text," +
                "content text," +
                "date_created text," +
                "date_updated text," +
                "recycle_status integer default 0)";
        // 执行
        sqLiteDatabase.execSQL(create_table_note);

        // 插入一个默认笔记,作为用户使用手册
        String insert_default_note = "insert into note " +
                "(title, content, date_created, date_updated)" +
                "values" +
                "(\"使用手册\", \"1.本软件可以编写笔记，也可以创建待办事件;\n" +
                "2.页面提供有相应的搜索框来搜索内容;\n" +
                "3.点击右下角按钮可以进行添加操作;\n" +
                "4.点击笔记或待办可以进行修改操作;\n" +
                "5.长按笔记将笔记移入回收站;\n" +
                "6.长按待办可以进行删除操作;\n" +
                "7.设置了提醒日期的待办,会在指定时间提醒;\n" +
                "8.点击待办左边的选择框,可以改变待办的完成状态;\n" +
                "9.在回收站页面可以按照关键字查询已回收笔记;\n" +
                "10.在回收站页面点击笔记可以将笔记还原;\n" +
                "11.在回收站页面长按笔记可以将笔记彻底删除;\n" +
                "12.如有不足之处,欢迎反馈至QQ:xxxxxxx！\"," +
                "\"06月20日 上午 11:11\",\"06月20日 上午 11:11\")";
        sqLiteDatabase.execSQL(insert_default_note);

        // 创建in_abeyance表的sql语句
        String create_table_in_abeyance = "create table in_abeyance (" +
                "_id integer primary key autoincrement," +
                "content text," +
                "date_remind text," +
                "status integer default 0," +
                "date_created text)";
        // 执行
        sqLiteDatabase.execSQL(create_table_in_abeyance);
    }

    // 数据库升级
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
