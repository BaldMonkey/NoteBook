package top.baldmonkey.notebook.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import top.baldmonkey.notebook.bean.InAbeyance;
import top.baldmonkey.notebook.bean.Note;

/**
 * 这是数据库操作的工具类
 * 需要配合DBHelper使用
 */
public class DBOperator {

    // 通过这个方法来获取全部Note数据
    // 根据 recycle_status 决定是查询回收的还是未被回收的笔记
    // recycle_status = 0 代表未回收, recycle_status = 1 代表已回收
    public static List<Note> getNotesData(Context context, int recycle_status) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // 编写全部查询的sql语句
        String getAllNotes = "select * from note " +
                "where recycle_status = " + recycle_status +
                " order by date_updated desc";
        List<Note> notes = new ArrayList<>(); // 用于存放Note数据
        // 记录查询返回的游标
        Cursor cursor = database.rawQuery(getAllNotes, null);
        // 迭代游标,获取note数据
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            String date_created = cursor.getString(3);
            String date_updated = cursor.getString(4);
            // 将以上信息封装为一个Note实体,添加到note数组中
            notes.add(new Note(_id, title, content, date_created, date_updated));
        }
        cursor.close(); // 关闭游标
        database.close(); // 关闭数据库
        return notes; // 将note数组返回
    }

    // 通过这个方法来查询符合关键字的note 数据
    // 根据 recycle_status 决定是查询回收的还是未被回收的笔记
    // recycle_status = 0 代表未回收, recycle_status = 1 代表已回收
    public static List<Note> queryNotesData(Context context,String keyWord, int recycle_status) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // 查询符合关键字的note的sql语句
        String queryNotesData = "select * from note " +
                "where (title like \"%" + keyWord + "%\" " +
                "or content like \"%" + keyWord + "%\") " +
                "and recycle_status = " + recycle_status +
                " order by date_updated desc";
        List<Note> notes = new ArrayList<>(); // 存放note数据
        // 记录返回的游标
        Cursor cursor = database.rawQuery(queryNotesData, null);
        // 迭代游标,获取note数据
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            String date_created = cursor.getString(3);
            String date_updated = cursor.getString(4);
            // 将以上数据封装为Note实体,并添加到note数组中
            notes.add(new Note(_id, title, content, date_created, date_updated));
        }
        cursor.close(); // 关闭游标
        database.close(); // 关闭数据库
        return notes; // 返回note数组
    }

    // 添加一条笔记
    public static void addNote(Context context, Note note) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // 获取当前时间作为创建时间和更新时间
        String date = DateUtil.getCurrentTime();
        // 添加笔记的sql语句
        String insert_note = "insert into note" +
                "(title, content, date_created, date_updated)" +
                "values" +
                "('" + note.getTitle() + "','" +
                note.getContent() + "','" +
                date + "','" + date + "')";
        database.execSQL(insert_note); // 执行sql语句
        database.close(); // 关闭数据库
    }

    // 更新一条笔记
    public static void updateNote(Context context, Note note) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // 获取当前时间作为更新时间
        String date = DateUtil.getCurrentTime();
        // 更新笔记的sql语句
        String update_note = "update note " +
        "set title = '" + note.getTitle() + "'," +
                "content = '" + note.getContent() + "'," +
                "date_updated = '" + date + "'" +
                "where _id = " + note.get_id();
        database.execSQL(update_note); // 执行sql语句
        database.close(); // 关闭数据库
    }

    // 更改笔记回收状态
    public static void changeNoteRecycleStatus(Context context, int _id) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // 根据 _id 更改笔记回收状态
        String change_recycle_status = "update note " +
                "set recycle_status = 1 - recycle_status " +
                "where _id = " + _id;
        database.execSQL(change_recycle_status); // 执行
        database.close(); // 关闭数据库
    }

    // 删除一条笔记
    public static void deleteNote(Context context, int _id) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // 根据_id删除笔记的sql语句
        String delete_note = "delete from note " +
                "where _id = " + _id;
        database.execSQL(delete_note); // 执行sql语句
        database.close(); // 关闭数据库
    }

    // 添加待办
    public static int add_in_abeyance(Context context, InAbeyance inAbeyance) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // 添加待办的sql语句
        String add_in_abeyance = "insert into in_abeyance " +
                "(content, date_remind, date_created)" +
                "values" +
                "('" + inAbeyance.getContent() + "','" +
                inAbeyance.getDate_remind() + "','" +
                inAbeyance.getDate_created() +
                "')";
        database.execSQL(add_in_abeyance); // 执行sql
        String get_id = "select last_insert_rowid() from in_abeyance";
        Cursor cursor = database.rawQuery(get_id, null);
        int _id = -1;
        if (cursor.moveToFirst()) {
            _id = cursor.getInt(0);
        }
        cursor.close(); // 关闭游标
        database.close(); // 关闭数据库
        return _id; // 返回 _id
    }

    // 获取全部待办信息
    public static List<InAbeyance> getInAbeyanceData(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获得数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String get_all_in_abeyance = "select * from in_abeyance " +
                "order by _id desc";
        Cursor cursor = database.rawQuery(get_all_in_abeyance, null);
        List<InAbeyance> inAbeyanceList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            String content = cursor.getString(1);
            String date_remind = cursor.getString(2);
            int status = cursor.getInt(3);
            InAbeyance inAbeyance = new InAbeyance(_id, content, date_remind, status);
            inAbeyanceList.add(inAbeyance);
        }
        cursor.close(); // 关闭游标
        database.close(); // 关闭数据库
        return inAbeyanceList; // 返回全部待办
    }

    // 根据关键字查询待办
    public static List<InAbeyance> queryInAbeyanceData(Context context, String keyWord) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获得数据库
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // 编写按关键字查询的sql语句
        String query_in_abeyance = "select * from in_abeyance " +
                "where content like \"%" + keyWord + "%\"" +
                "order by _id desc";
        Cursor cursor = database.rawQuery(query_in_abeyance, null);// 执行sql语句
        List<InAbeyance> inAbeyanceList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            String content = cursor.getString(1);
            String date_remind = cursor.getString(2);
            int status = cursor.getInt(3);
            InAbeyance inAbeyance = new InAbeyance(_id, content, date_remind, status);
            inAbeyanceList.add(inAbeyance);
        }
        Log.e("xxx", "queryInAbeyanceData: " + inAbeyanceList );
        cursor.close(); // 关闭游标
        database.close(); // 关闭数据库
        return inAbeyanceList;
    }

    // 删除一条待办
    public static void deleteInAbeyance(Context context, int _id) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // 删除待办的sql语句
        String deleteInAbeyance = "delete from in_abeyance where " +
                "_id = " + _id;
        database.execSQL(deleteInAbeyance); // 执行sql语句
        database.close(); // 关闭数据库
    }

    // 修改待办状态
    public static void changeInAbeyanceStatus(Context context, int _id) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // 更新待办完成状态的sql语句
        String update_in_abeyance_status = "update in_abeyance set " +
                "status = 1 - status where " +
                "_id = " + _id;
        database.execSQL(update_in_abeyance_status); // 执行sql语句
        database.close(); // 关闭数据库
    }

    // 更新一条待办
    public static void updateInAbeyance(Context context, InAbeyance inAbeyance) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        // 获取数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // 更新待办的sql语句
        String update_in_abeyance = "update in_abeyance set " +
                "content = '" + inAbeyance.getContent() + "'," +
                "date_remind = '" + inAbeyance.getDate_remind() + "'" +
                " where " +
                "_id = " + inAbeyance.get_id();
        database.execSQL(update_in_abeyance); // 执行sql语句
        database.close(); // 关闭数据库
    }
}
