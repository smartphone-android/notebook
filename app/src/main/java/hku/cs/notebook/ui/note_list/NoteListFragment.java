package hku.cs.notebook.ui.note_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.app.AlertDialog;
import android.widget.Toast;

import java.util.List;

import hku.cs.notebook.R;
import hku.cs.notebook.adapter.NotepadAdapter;
import hku.cs.notebook.bean.NotepadBean;
import hku.cs.notebook.database.SQLiteHelper;



public class NoteListFragment extends Fragment {
    ListView listView;
    List<NotepadBean> list;
    SQLiteHelper mSQLiteHelper;
    NotepadAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        showQueryData(); // 刷新数据
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化布局中的控件
        listView = view.findViewById(R.id.listview);
        ImageView add = view.findViewById(R.id.add);

        // 设置按钮点击事件
        add.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_noteListFragment_to_editorFragment);
        });

        // 初始化数据
        initData();

        // 设置列表点击事件
        listView.setOnItemClickListener((parent, v, position, id) -> {
            NotepadBean notepadBean = list.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", notepadBean.getId());
            bundle.putString("time", notepadBean.getNotepadTime());
            bundle.putString("content", notepadBean.getNotepadContent());
            Navigation.findNavController(v).navigate(R.id.action_noteListFragment_to_editorFragment, bundle);
        });

        // 设置列表长按事件
        listView.setOnItemLongClickListener((parent, v, position, id) -> {
            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.delete_confirmation))
                    .setPositiveButton(getString(R.string.confirm), (dialogInterface, which) -> {
                        NotepadBean notepadBean = list.get(position);
                        if (mSQLiteHelper.deleteData(notepadBean.getId())) {
                            list.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, which) -> dialogInterface.dismiss());
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
}
