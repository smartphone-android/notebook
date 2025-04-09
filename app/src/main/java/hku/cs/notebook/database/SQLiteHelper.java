package hku.cs.notebook.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hku.cs.notebook.bean.NotepadBean;
import hku.cs.notebook.utils.DBUtils;

public class SQLiteHelper extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;
    //创建数据库
    public SQLiteHelper(Context context){
        super(context, DBUtils.DATABASE_NAME, null, DBUtils.DATABASE_VERION);//调用了DBUtils类，得到数据库名Notepad
        sqLiteDatabase = this.getWritableDatabase();
    }
    //创建表，用execSQL()方法创建一个数据表，列名分别为ID、CONTENT、TIME
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+DBUtils.DATABASE_TABLE+"("+DBUtils.NOTEPAD_ID+
                " integer primary key autoincrement,"+ DBUtils.NOTEPAD_CONTENT +
                " text," + DBUtils.NOTEPAD_NAME+ " text," + DBUtils.NOTEPAD_TIME+ " text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    //添加数据
    public boolean insertData(String noteContent,String noteName,String noteTime){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBUtils.NOTEPAD_CONTENT,noteContent);
        contentValues.put(DBUtils.NOTEPAD_NAME,noteName);
        contentValues.put(DBUtils.NOTEPAD_TIME,noteTime);
        return
                sqLiteDatabase.insert(DBUtils.DATABASE_TABLE,null,contentValues)>0;
    }
    //删除数据
    public boolean deleteData(String id){
        String sql=DBUtils.NOTEPAD_ID+"=?";
        String[] contentValuesArray=new String[]{String.valueOf(id)};
        return
                sqLiteDatabase.delete(DBUtils.DATABASE_TABLE,sql,contentValuesArray)>0;
    }
    //修改数据
    public boolean updateData(String id,String content,String name,String time){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBUtils.NOTEPAD_CONTENT,content);
        contentValues.put(DBUtils.NOTEPAD_NAME,name);
        contentValues.put(DBUtils.NOTEPAD_TIME,time);
        String sql=DBUtils.NOTEPAD_ID+"=?";
        String[] strings=new String[]{id};
        return
                sqLiteDatabase.update(DBUtils.DATABASE_TABLE,contentValues,sql,strings)>0;
    }
    //查询数据
    public List<NotepadBean> query() {
        List<NotepadBean> list = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(DBUtils.DATABASE_TABLE, null, null, null,
                null, null, DBUtils.NOTEPAD_ID + " DESC");

        if (cursor != null) {
            try {
                int idIndex = cursor.getColumnIndexOrThrow(DBUtils.NOTEPAD_ID);
                int contentIndex = cursor.getColumnIndexOrThrow(DBUtils.NOTEPAD_CONTENT);
                int nameIndex = cursor.getColumnIndexOrThrow(DBUtils.NOTEPAD_NAME);
                int timeIndex = cursor.getColumnIndexOrThrow(DBUtils.NOTEPAD_TIME);

                while (cursor.moveToNext()) {
                    NotepadBean noteInfo = new NotepadBean();
                    noteInfo.setId(String.valueOf(cursor.getInt(idIndex)));
                    noteInfo.setNotepadContent(cursor.getString(contentIndex));
                    noteInfo.setNotepadName(cursor.getString(nameIndex));
                    noteInfo.setNotepadTime(cursor.getString(timeIndex));
                    list.add(noteInfo);
                }
            } catch (IllegalArgumentException e) {
                Log.e("SQLiteHelper", "Column name not found in Cursor: " + e.getMessage());
            } finally {
                cursor.close(); // 确保 Cursor 在使用完毕后关闭，避免内存泄漏
            }
        } else {
            Log.d("SQLiteHelper", "Query returned null Cursor.");
        }
        return list;
    }

}