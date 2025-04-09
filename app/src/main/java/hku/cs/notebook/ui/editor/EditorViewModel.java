package hku.cs.notebook.ui.editor;

import androidx.lifecycle.ViewModel;
import hku.cs.notebook.database.SQLiteHelper;
import hku.cs.notebook.utils.DBUtils;

public class EditorViewModel extends ViewModel {
    private SQLiteHelper mSQLiteHelper;
    private String id;

    public void init(SQLiteHelper sqliteHelper, String noteId) {
        this.mSQLiteHelper = sqliteHelper;
        this.id = noteId;
    }

    public boolean saveNote(String content, String name) {
        if (id != null) {
            return mSQLiteHelper.updateData(id, content, name, DBUtils.getTime());
        } else {
            return mSQLiteHelper.insertData(content, name, DBUtils.getTime());
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
