package hku.cs.notebook.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                " text," + DBUtils.NOTEPAD_TIME+ " text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    //添加数据
    public boolean insertData(String userContent,String userTime){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBUtils.NOTEPAD_CONTENT,userContent);
        contentValues.put(DBUtils.NOTEPAD_TIME,userTime);
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
    public boolean updateData(String id,String content,String userYear){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBUtils.NOTEPAD_CONTENT,content);
        contentValues.put(DBUtils.NOTEPAD_TIME,userYear);
        String sql=DBUtils.NOTEPAD_ID+"=?";
        String[] strings=new String[]{id};
        return
                sqLiteDatabase.update(DBUtils.DATABASE_TABLE,contentValues,sql,strings)>0;
    }
    //查询数据
    public List<NotepadBean> query(){//将遍历的数据存放在一个List<NotepadBean>类型的合集中
        List<NotepadBean> list=new ArrayList<NotepadBean>();
//        通过query()方法查询数据库表中的所有数据，并返回一个Cursor对象
        Cursor cursor=sqLiteDatabase.query(DBUtils.DATABASE_TABLE,null,null,null,
                null,null,DBUtils.NOTEPAD_ID+" desc");
        if (cursor!=null){
            while (cursor.moveToNext()){//通过while循环遍历Cursor对象中的数据
                NotepadBean noteInfo=new NotepadBean();
                @SuppressLint("Range") String id = String.valueOf(cursor.getInt
                        (cursor.getColumnIndex(DBUtils.NOTEPAD_ID)));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex
                        (DBUtils.NOTEPAD_CONTENT));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex
                        (DBUtils.NOTEPAD_TIME));
                noteInfo.setId(id);
                noteInfo.setNotepadContent(content);
                noteInfo.setNotepadTime(time);
                list.add(noteInfo);
            }
            cursor.close();
        }
        return list;
    }
}