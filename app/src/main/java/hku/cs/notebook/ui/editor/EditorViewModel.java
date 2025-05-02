package hku.cs.notebook.ui.editor;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import hku.cs.notebook.database.SQLiteHelper;
import hku.cs.notebook.utils.DBUtils;

public class EditorViewModel extends ViewModel {
    private SQLiteHelper mSQLiteHelper;
    private String id;

    public void init(SQLiteHelper sqliteHelper, String userid) {
        this.mSQLiteHelper = sqliteHelper;
        this.id = userid;
    }

    public boolean saveNote(String userid, String content, String name) {

        // 调试日志输出
        Log.d("EditorViewModel", "保存笔记时的 userId: " + userid);

        if (id != null) {
            return mSQLiteHelper.updateData(id, content, name, DBUtils.getTime());
        } else {
            return mSQLiteHelper.insertData(userid, content, name, DBUtils.getTime());
        }
    }


    public boolean hasId() {
        return id != null;
    }

    public String getId() {
        return id;
    }

    public boolean deleteNote() {
        if (id != null) {
            return mSQLiteHelper.deleteData(id);
        }
        return false;
    }
}
