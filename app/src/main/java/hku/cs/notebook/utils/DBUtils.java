package hku.cs.notebook.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
public class DBUtils {
    public static final String DATABASE_NAME = "Notepad";//数据库名
    public static final String DATABASE_TABLE = "Note";  //表名
    public static final String USER_TABLE = "User";  //用户表名
    public static final String USER_NOTE_TABLE = "UserNote";  //用户-笔记关联表名
    public static final int DATABASE_VERION = 3;//数据库版本，增加盐值字段需要升级版本号
    //数据库表中的列名
    public static final String NOTEPAD_ID = "id";
    public static final String NOTEPAD_CONTENT = "content";
    public static final String NOTEPAD_NAME = "notename";
    public static final String NOTEPAD_TIME = "notetime";
    //User表中的列名
    public static final String USER_ID = "user_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SALT = "salt";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    //UserNote表中的列名
    public static final String USER_NOTE_ID = "user_note_id";
    public static final String USER_NOTE_USER_ID = "user_id";
    public static final String USER_NOTE_NOTE_ID = "note_id";
    //获取当前日期
    public static final String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}