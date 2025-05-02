package hku.cs.notebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hku.cs.notebook.bean.NotepadBean;
import hku.cs.notebook.bean.UserBean;
import hku.cs.notebook.utils.DBUtils;
import hku.cs.notebook.utils.PasswordUtils;

public class SQLiteHelper extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;
    //创建数据库
    public SQLiteHelper(Context context){
        super(context, DBUtils.DATABASE_NAME, null, DBUtils.DATABASE_VERION);//调用了DBUtils类，得到数据库名Notepad
        Log.d("SQLiteHelper", "context: " + context);
        sqLiteDatabase = this.getWritableDatabase();
    }
    //创建表，用execSQL()方法创建一个数据表，列名分别为ID、CONTENT、TIME
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建笔记表
        db.execSQL("create table "+DBUtils.DATABASE_TABLE+"("+DBUtils.NOTEPAD_ID+
                " integer primary key autoincrement,"+ DBUtils.NOTEPAD_CONTENT +
                " text," + DBUtils.NOTEPAD_NAME+ " text," + DBUtils.NOTEPAD_TIME+ " text)");

        // 创建用户表
        db.execSQL("create table "+DBUtils.USER_TABLE+"("+DBUtils.USER_ID+
                " integer primary key autoincrement,"+ DBUtils.USERNAME +
                " text," + DBUtils.PASSWORD+ " text," + DBUtils.SALT+ " text," +
                DBUtils.EMAIL+ " text)");

        // 创建用户-笔记关联表
        db.execSQL("create table "+DBUtils.USER_NOTE_TABLE+"("+DBUtils.USER_NOTE_ID+
                " integer primary key autoincrement,"+ DBUtils.USER_NOTE_USER_ID +
                " integer," + DBUtils.USER_NOTE_NOTE_ID+ " integer," +
                "FOREIGN KEY("+DBUtils.USER_NOTE_USER_ID+") REFERENCES "+DBUtils.USER_TABLE+"("+DBUtils.USER_ID+")," +
                "FOREIGN KEY("+DBUtils.USER_NOTE_NOTE_ID+") REFERENCES "+DBUtils.DATABASE_TABLE+"("+DBUtils.NOTEPAD_ID+"))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 处理数据库升级
        if (oldVersion < 2) {
            // 创建用户表
            db.execSQL("create table "+DBUtils.USER_TABLE+"("+DBUtils.USER_ID+
                    " integer primary key autoincrement,"+ DBUtils.USERNAME +
                    " text," + DBUtils.PASSWORD+ " text," + DBUtils.SALT+ " text," +
                    DBUtils.EMAIL+ " text)");

            // 创建用户-笔记关联表
            db.execSQL("create table "+DBUtils.USER_NOTE_TABLE+"("+DBUtils.USER_NOTE_ID+
                    " integer primary key autoincrement,"+ DBUtils.USER_NOTE_USER_ID +
                    " integer," + DBUtils.USER_NOTE_NOTE_ID+ " integer," +
                    "FOREIGN KEY("+DBUtils.USER_NOTE_USER_ID+") REFERENCES "+DBUtils.USER_TABLE+"("+DBUtils.USER_ID+")," +
                    "FOREIGN KEY("+DBUtils.USER_NOTE_NOTE_ID+") REFERENCES "+DBUtils.DATABASE_TABLE+"("+DBUtils.NOTEPAD_ID+"))");
        }

        // 如果是从版本2升级到版本3，添加盐值字段
        if (oldVersion < 3) {
            // 检查盐值字段是否存在
            Cursor cursor = db.rawQuery("PRAGMA table_info(" + DBUtils.USER_TABLE + ")", null);
            boolean hasSaltColumn = false;
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    if (columnName.equals(DBUtils.SALT)) {
                        hasSaltColumn = true;
                        break;
                    }
                }
                cursor.close();
            }

            // 如果盐值字段不存在，添加它
            if (!hasSaltColumn) {
                db.execSQL("ALTER TABLE " + DBUtils.USER_TABLE + " ADD COLUMN " + DBUtils.SALT + " TEXT");
            }
        }
    }

    // 笔记相关方法
    //添加笔记数据
    public boolean insertData(String userId, String noteContent, String noteName, String noteTime) {
        // 首先插入笔记内容
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.NOTEPAD_CONTENT, noteContent);
        contentValues.put(DBUtils.NOTEPAD_NAME, noteName);
        contentValues.put(DBUtils.NOTEPAD_TIME, noteTime);

        // 插入到笔记表并返回新插入的笔记ID
        long noteId = sqLiteDatabase.insert(DBUtils.DATABASE_TABLE, null, contentValues);

        // 如果插入成功，将用户和笔记的关联添加到 USER_NOTE_TABLE
        if (noteId > 0) {
            // 打印插入的数据
            Log.d("Debug_InsertUserNote", "Inserting User ID: " + userId + ", Note ID: " + noteId);

            ContentValues userNoteContentValues = new ContentValues();
            userNoteContentValues.put(DBUtils.USER_NOTE_USER_ID, userId);
            userNoteContentValues.put(DBUtils.USER_NOTE_NOTE_ID, noteId);

            long result = sqLiteDatabase.insert(DBUtils.USER_NOTE_TABLE, null, userNoteContentValues);
            if (result > 0) {
                Log.d("Debug_InsertResult", "Successfully inserted into USER_NOTE_TABLE with ID: " + result);
                return true;
            } else {
                Log.e("Debug_InsertResult", "Failed to insert into USER_NOTE_TABLE.");
                return false;
            }
        }
        Log.e("Debug_InsertUserNote", "Invalid Note ID: " + noteId);
        return false;
    }
    //删除笔记数据
    public boolean deleteData(String id){
        String sql=DBUtils.NOTEPAD_ID+"=?";
        String[] contentValuesArray=new String[]{String.valueOf(id)};
        return
                sqLiteDatabase.delete(DBUtils.DATABASE_TABLE,sql,contentValuesArray)>0;
    }
    //修改笔记数据
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
    //查询所有笔记数据
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
                    noteInfo.setNoteId(String.valueOf(cursor.getInt(idIndex)));
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

    // 用户相关方法
    // 添加用户（使用加盐哈希密码）
    public boolean insertUser(String username, String password, String email) {
        // 生成盐值
        String salt = PasswordUtils.generateSalt();
        // 对密码进行哈希
        String hashedPassword = PasswordUtils.hashPassword(password, salt);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.USERNAME, username);
        contentValues.put(DBUtils.PASSWORD, hashedPassword);
        contentValues.put(DBUtils.SALT, salt);
        contentValues.put(DBUtils.EMAIL, email);
        return sqLiteDatabase.insert(DBUtils.USER_TABLE, null, contentValues) > 0;
    }

    // 更新用户信息（包括密码）
    public boolean updateUser(String userId, String username, String password, String email) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.USERNAME, username);

        // 如果提供了新密码，则更新密码和盐值
        if (password != null && !password.isEmpty()) {
            String salt = PasswordUtils.generateSalt();
            String hashedPassword = PasswordUtils.hashPassword(password, salt);
            contentValues.put(DBUtils.PASSWORD, hashedPassword);
            contentValues.put(DBUtils.SALT, salt);
        }

        contentValues.put(DBUtils.EMAIL, email);
        String sql = DBUtils.USER_ID + "=?";
        String[] strings = new String[]{userId};
        return sqLiteDatabase.update(DBUtils.USER_TABLE, contentValues, sql, strings) > 0;
    }

    // 更新用户信息（不包括密码）
    public boolean updateUserWithoutPassword(String userId, String username, String email) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.USERNAME, username);
        contentValues.put(DBUtils.EMAIL, email);
        String sql = DBUtils.USER_ID + "=?";
        String[] strings = new String[]{userId};
        return sqLiteDatabase.update(DBUtils.USER_TABLE, contentValues, sql, strings) > 0;
    }

    // 删除用户
    public boolean deleteUser(String userId) {
        String sql = DBUtils.USER_ID + "=?";
        String[] contentValuesArray = new String[]{String.valueOf(userId)};
        return sqLiteDatabase.delete(DBUtils.USER_TABLE, sql, contentValuesArray) > 0;
    }

    // 查询所有用户
    public List<UserBean> queryAllUsers() {
        List<UserBean> list = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(DBUtils.USER_TABLE, null, null, null,
                null, null, DBUtils.USER_ID + " DESC");

        if (cursor != null) {
            try {
                int idIndex = cursor.getColumnIndexOrThrow(DBUtils.USER_ID);
                int usernameIndex = cursor.getColumnIndexOrThrow(DBUtils.USERNAME);
                int passwordIndex = cursor.getColumnIndexOrThrow(DBUtils.PASSWORD);
                int saltIndex = cursor.getColumnIndexOrThrow(DBUtils.SALT);
                int emailIndex = cursor.getColumnIndexOrThrow(DBUtils.EMAIL);

                while (cursor.moveToNext()) {
                    UserBean userInfo = new UserBean();
                    userInfo.setUserId(String.valueOf(cursor.getInt(idIndex)));
                    userInfo.setUsername(cursor.getString(usernameIndex));
                    userInfo.setPassword(cursor.getString(passwordIndex));
                    userInfo.setSalt(cursor.getString(saltIndex));
                    userInfo.setEmail(cursor.getString(emailIndex));
                    list.add(userInfo);
                }
            } catch (IllegalArgumentException e) {
                Log.e("SQLiteHelper", "Column name not found in Cursor: " + e.getMessage());
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    // 根据用户名查询用户
    public UserBean queryUserByUsername(String username) {
        UserBean userBean = null;
        String sql = DBUtils.USERNAME + "=?";
        String[] selectionArgs = new String[]{username};
        Cursor cursor = sqLiteDatabase.query(DBUtils.USER_TABLE, null, sql, selectionArgs,
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int idIndex = cursor.getColumnIndexOrThrow(DBUtils.USER_ID);
                int usernameIndex = cursor.getColumnIndexOrThrow(DBUtils.USERNAME);
                int passwordIndex = cursor.getColumnIndexOrThrow(DBUtils.PASSWORD);
                int saltIndex = cursor.getColumnIndexOrThrow(DBUtils.SALT);
                int emailIndex = cursor.getColumnIndexOrThrow(DBUtils.EMAIL);

                userBean = new UserBean();
                userBean.setUserId(String.valueOf(cursor.getInt(idIndex)));
                userBean.setUsername(cursor.getString(usernameIndex));
                userBean.setPassword(cursor.getString(passwordIndex));
                userBean.setSalt(cursor.getString(saltIndex));
                userBean.setEmail(cursor.getString(emailIndex));
            } catch (IllegalArgumentException e) {
                Log.e("SQLiteHelper", "Column name not found in Cursor: " + e.getMessage());
            } finally {
                cursor.close();
            }
        }
        return userBean;
    }

    // 根据用户ID查询用户
    public UserBean queryUserById(String userId) {
        UserBean userBean = null;
        String sql = DBUtils.USER_ID + "=?";
        String[] selectionArgs = new String[]{userId};
        Cursor cursor = sqLiteDatabase.query(DBUtils.USER_TABLE, null, sql, selectionArgs,
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int idIndex = cursor.getColumnIndexOrThrow(DBUtils.USER_ID);
                int usernameIndex = cursor.getColumnIndexOrThrow(DBUtils.USERNAME);
                int passwordIndex = cursor.getColumnIndexOrThrow(DBUtils.PASSWORD);
                int saltIndex = cursor.getColumnIndexOrThrow(DBUtils.SALT);
                int emailIndex = cursor.getColumnIndexOrThrow(DBUtils.EMAIL);

                userBean = new UserBean();
                userBean.setUserId(String.valueOf(cursor.getInt(idIndex)));
                userBean.setUsername(cursor.getString(usernameIndex));
                userBean.setPassword(cursor.getString(passwordIndex));
                userBean.setSalt(cursor.getString(saltIndex));
                userBean.setEmail(cursor.getString(emailIndex));
            } catch (IllegalArgumentException e) {
                Log.e("SQLiteHelper", "Column name not found in Cursor: " + e.getMessage());
            } finally {
                cursor.close();
            }
        }
        return userBean;
    }

    // 验证用户登录
    public UserBean verifyUser(String username, String password) {
        UserBean userBean = queryUserByUsername(username);
        System.out.println(userBean.getUsername()+userBean.getSalt());
        if (userBean != null) {
            // 验证密码
            boolean isValid = PasswordUtils.verifyPassword(password, userBean.getSalt(), userBean.getPassword());
            if (isValid) {
                return userBean;
            }
        }
        return null;
    }

    // 用户-笔记关联相关方法
    // 添加用户-笔记关联
    public boolean insertUserNote(String userId, String noteId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.USER_NOTE_USER_ID, userId);
        contentValues.put(DBUtils.USER_NOTE_NOTE_ID, noteId);
        return sqLiteDatabase.insert(DBUtils.USER_NOTE_TABLE, null, contentValues) > 0;
    }

    // 删除用户-笔记关联
    public boolean deleteUserNote(String userId, String noteId) {
        String sql = DBUtils.USER_NOTE_USER_ID + "=? AND " + DBUtils.USER_NOTE_NOTE_ID + "=?";
        String[] contentValuesArray = new String[]{userId, noteId};
        return sqLiteDatabase.delete(DBUtils.USER_NOTE_TABLE, sql, contentValuesArray) > 0;
    }

    public List<String> queryUserNoteIds(String userId) {
        List<String> noteIds = new ArrayList<>();
        String sql = DBUtils.USER_NOTE_USER_ID + "=?";
        String[] selectionArgs = new String[]{userId};

        // 打印调试日志
        Log.d("Debug_UserId", "Passed userId: " + userId);
        Log.d("Debug_SQL", "SQL: " + sql + ", Args: " + Arrays.toString(selectionArgs));

        Cursor cursor = sqLiteDatabase.query(DBUtils.USER_NOTE_TABLE, null, sql, selectionArgs,
                null, null, null);

        if (cursor != null) {
            Log.d("Debug_Cursor", "Cursor Count: " + cursor.getCount()); // 打印游标结果数
            try {

                int noteIdIndex = cursor.getColumnIndexOrThrow(DBUtils.USER_NOTE_NOTE_ID);

                while (cursor.moveToNext()) {
                    String noteId = String.valueOf(cursor.getInt(noteIdIndex));
//                    Log.d("Debug_NoteId", "Fetched Note ID: " + noteId); // 打印获取的笔记ID
                    noteIds.add(noteId);
                }
            } catch (IllegalArgumentException e) {
                Log.e("SQLiteHelper", "Column name not found in Cursor: " + e.getMessage());
            } finally {
                cursor.close(); // 确保游标关闭
            }
        } else {
            Log.d("Debug_Cursor", "Cursor is null.");
        }

        Log.d("Debug_NoteIds", "Final Note IDs List: " + noteIds);
        return noteIds;
    }


    // 查询笔记的所有用户ID
    public List<String> queryNoteUserIds(String noteId) {
        List<String> userIds = new ArrayList<>();
        String sql = DBUtils.USER_NOTE_NOTE_ID + "=?";
        String[] selectionArgs = new String[]{noteId};
        Cursor cursor = sqLiteDatabase.query(DBUtils.USER_NOTE_TABLE, null, sql, selectionArgs,
                null, null, null);

        if (cursor != null) {
            try {
                int userIdIndex = cursor.getColumnIndexOrThrow(DBUtils.USER_NOTE_USER_ID);

                while (cursor.moveToNext()) {
                    userIds.add(String.valueOf(cursor.getInt(userIdIndex)));
                }
            } catch (IllegalArgumentException e) {
                Log.e("SQLiteHelper", "Column name not found in Cursor: " + e.getMessage());
            } finally {
                cursor.close();
            }
        }
        return userIds;
    }

    public List<NotepadBean> queryUserNotes(String userId) {
        List<NotepadBean> list = new ArrayList<>();

        // 先查询用户关联的所有笔记ID
        List<String> noteIds = queryUserNoteIds(userId);
        Log.d("Debug_QueryUserNoteIds", "Returned Note IDs: " + noteIds.toString());

        // 如果没有关联的笔记，直接返回空列表
        if (noteIds.isEmpty()) {
            Log.d("Debug_EmptyNoteIds", "No Note IDs found for userId: " + userId);
            return list;
        }

        // 构建IN查询条件
        StringBuilder selection = new StringBuilder(DBUtils.NOTEPAD_ID + " IN (");
        String[] selectionArgs = new String[noteIds.size()];
        for (int i = 0; i < noteIds.size(); i++) {
            selection.append("?");
            selectionArgs[i] = noteIds.get(i);
            if (i < noteIds.size() - 1) {
                selection.append(",");
            }
        }
        selection.append(")");
        Log.d("Debug_SQL_Selection", "Selection Clause: " + selection.toString());
        Log.d("Debug_SQL_SelectionArgs", "Selection Args: " + Arrays.toString(selectionArgs));

        // 查询笔记
        Cursor cursor = sqLiteDatabase.query(DBUtils.DATABASE_TABLE, null, selection.toString(), selectionArgs,
                null, null, DBUtils.NOTEPAD_ID + " DESC");

        if (cursor == null) {
            Log.e("Debug_Cursor", "Cursor is null. Query may have failed.");
            return list;
        }
        Log.d("Debug_Cursor", "Cursor Count: " + cursor.getCount());

        if (cursor != null) {
            try {
                int idIndex = cursor.getColumnIndexOrThrow(DBUtils.NOTEPAD_ID);
                int contentIndex = cursor.getColumnIndexOrThrow(DBUtils.NOTEPAD_CONTENT);
                int nameIndex = cursor.getColumnIndexOrThrow(DBUtils.NOTEPAD_NAME);
                int timeIndex = cursor.getColumnIndexOrThrow(DBUtils.NOTEPAD_TIME);

                while (cursor.moveToNext()) {
                    NotepadBean noteInfo = new NotepadBean();
                    noteInfo.setNoteId(String.valueOf(cursor.getInt(idIndex)));
                    noteInfo.setNotepadContent(cursor.getString(contentIndex));
                    noteInfo.setNotepadName(cursor.getString(nameIndex));
                    noteInfo.setNotepadTime(cursor.getString(timeIndex));

//                    Log.d("Debug_NoteInfo", "Note: ID=" + noteInfo.getNoteId() +
//                            ", Content=" + noteInfo.getNotepadContent() +
//                            ", Name=" + noteInfo.getNotepadName() +
//                            ", Time=" + noteInfo.getNotepadTime());

                    list.add(noteInfo);
                }
            } catch (IllegalArgumentException e) {
                Log.e("SQLiteHelper", "Column name not found in Cursor: " + e.getMessage());
            } finally {
                cursor.close();
            }
        }
        return list;
    }
}