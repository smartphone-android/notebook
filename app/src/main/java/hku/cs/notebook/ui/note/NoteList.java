package hku.cs.notebook.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.widget.Toast;

import java.util.List;

import hku.cs.notebook.EditorActivity;
import hku.cs.notebook.R;
import hku.cs.notebook.adapter.NotepadAdapter;
import hku.cs.notebook.bean.NotepadBean;
import hku.cs.notebook.database.SQLiteHelper;



public class NoteList extends Fragment {
    ListView listView;
    List<NotepadBean> list;
    SQLiteHelper mSQLiteHelper;
    NotepadAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化布局中的控件
        listView = view.findViewById(R.id.listview);
        ImageView add = view.findViewById(R.id.add);

        // 设置按钮点击事件
        add.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditorActivity.class);
            startActivityForResult(intent, 1);
        });

        // 初始化数据
        initData();

        // 设置列表点击事件
        listView.setOnItemClickListener((parent, v, position, id) -> {
            NotepadBean notepadBean = list.get(position);
            Intent intent = new Intent(getActivity(), EditorActivity.class);
            intent.putExtra("id", notepadBean.getId());
            intent.putExtra("time", notepadBean.getNotepadTime());
            intent.putExtra("content", notepadBean.getNotepadContent());
            startActivityForResult(intent, 1);
        });

        // 设置列表长按事件
        listView.setOnItemLongClickListener((parent, v, position, id) -> {
            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setMessage("是否删除此事件？")
                    .setPositiveButton("确定", (dialogInterface, which) -> {
                        NotepadBean notepadBean = list.get(position);
                        if (mSQLiteHelper.deleteData(notepadBean.getId())) {
                            list.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", (dialogInterface, which) -> dialogInterface.dismiss());
            dialog = builder.create();
            dialog.show();
            return true;
        });
    }

    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(getActivity());
        showQueryData();
    }

    private void showQueryData() {
        if (list != null) {
            list.clear();
        }
        list = mSQLiteHelper.query();
        adapter = new NotepadAdapter(getActivity(), list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            showQueryData();
        }
    }
}
