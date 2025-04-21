package hku.cs.notebook.ui.note_list;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hku.cs.notebook.R;
import hku.cs.notebook.adapter.NotepadAdapter;
import hku.cs.notebook.bean.NotepadBean;
import hku.cs.notebook.bean.UserNoteBean;
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
            bundle.putString("id", notepadBean.getNoteId());
            bundle.putString("time", notepadBean.getNotepadTime());
            bundle.putString("name", notepadBean.getNotepadName());
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
                        if (mSQLiteHelper.deleteData(notepadBean.getNoteId())) {
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

    private List<String> getNoteIdsByUserId(String userId, List<UserNoteBean> userNotes) {
        List<String> noteIds = new ArrayList<>();
        for (UserNoteBean userNote : userNotes) {
            if (userNote.getUserId().equals(userId)) {
                noteIds.add(userNote.getNoteId());
            }
        }
        return noteIds;
    }


    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(getActivity());
        showQueryData();
    }

    private void showQueryData() {
        if (list != null) {
            list.clear();
        }

        // Retrieve the current user ID from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("NotebookPrefs", MODE_PRIVATE);
        String currentUserId = prefs.getString("userId", "-1"); // Default to "-1" if userId is not found

        // Log the current user ID for debugging purposes
        Log.d("NoteListFragment", "Current User ID: " + currentUserId);

        if ("-1".equals(currentUserId)) {
            // Handle the case where the user ID is not found
            Toast.makeText(getActivity(), "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch user-specific notes using SQLiteHelper
        list = mSQLiteHelper.queryUserNotes(currentUserId);

        // Log the content of the list
        if (list != null && !list.isEmpty()) {
            for (NotepadBean note : list) {
                Log.d("SQL_Result", "Note ID: " + note.getNoteId() +
                        ", Name: " + note.getNotepadName() +
                        ", Content: " + note.getNotepadContent() +
                        ", Time: " + note.getNotepadTime());
            }
        } else {
            Log.d("SQL_Result", "The query returned an empty or null list.");
        }


        adapter = new NotepadAdapter(getActivity(), list);
        listView.setAdapter(adapter);
    }


}
