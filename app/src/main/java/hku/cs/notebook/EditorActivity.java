package hku.cs.notebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import hku.cs.notebook.database.SQLiteHelper;
import hku.cs.notebook.utils.DBUtils;

public class EditorActivity extends Activity implements View.OnClickListener {
    ImageView note_back;
    TextView note_time;
    EditText content;
    ImageView delete;
    ImageView note_save;
    SQLiteHelper mSQLiteHelper;
    TextView noteName;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_editor);
        note_back = (ImageView) findViewById(R.id.note_back);
        note_time = (TextView)findViewById(R.id.tv_time);
        content = (EditText) findViewById(R.id.note_content);
        delete = (ImageView) findViewById(R.id.delete);
        note_save = (ImageView) findViewById(R.id.note_save);
        noteName = (TextView) findViewById(R.id.note_name);
        note_back.setOnClickListener(this);
        delete.setOnClickListener(this);
        note_save.setOnClickListener(this);
        initData();
    }
    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(this);
        noteName.setText("添加记录");
        Intent intent = getIntent();
        if(intent!= null){
            id = intent.getStringExtra("id");
            if (id != null){
                noteName.setText("修改记录");
                content.setText(intent.getStringExtra("content"));
                note_time.setText(intent.getStringExtra("time"));
                note_time.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onClick(View v) {
        int viewId = v.getId(); // Dynamically retrieve the ID

        if (viewId == R.id.note_back) {
            finish();
        } else if (viewId == R.id.delete) {
            content.setText(""); // Clear content
        } else if (viewId == R.id.note_save) {
            String noteContent = content.getText().toString().trim();

            if (noteContent.isEmpty()) {
                showToast("内容不能为空!");
                return;
            }

            if (id != null) { // Update operation
                if (mSQLiteHelper.updateData(id, noteContent, DBUtils.getTime())) {
                    showToast("修改成功");
                    setResult(2);
                    finish();
                } else {
                    showToast("修改失败");
                }
            } else { // Insert new data
                if (mSQLiteHelper.insertData(noteContent, DBUtils.getTime())) {
                    showToast("保存成功");
                    setResult(2);
                    finish();
                } else {
                    showToast("保存失败");
                }
            }
        } else {
            showToast("未知操作!");
        }
    }
    public void showToast(String message){
        Toast.makeText(EditorActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}