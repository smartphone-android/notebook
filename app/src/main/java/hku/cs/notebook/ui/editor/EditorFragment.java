package hku.cs.notebook.ui.editor;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.html.HtmlPlugin;


import hku.cs.notebook.R;
import hku.cs.notebook.database.SQLiteHelper;

public class EditorFragment extends Fragment implements View.OnClickListener {
    private TextView noteTime;
    private EditText content; // Markdown 编辑器

    private EditText notename;
    private TextView mdPreview; // Markdown 预览文本
    private ScrollView previewContainer; // 预览容器
    private Button btnCodeMode;
    private Button btnPreviewMode;
    private ImageView delete;
    private ImageView noteSave;

    private EditorViewModel viewModel;
    private SQLiteHelper mSQLiteHelper;
    private Markwon markwon; // Markdown 渲染器
    private MarkwonEditor editor; // Markdown 编辑器扩展
    private boolean isPreviewMode = false; // 当前模式标记

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化控件
        noteTime = view.findViewById(R.id.tv_time);
        content = view.findViewById(R.id.note_content);
        notename = view.findViewById(R.id.note_name);
        mdPreview = view.findViewById(R.id.md_preview);
        previewContainer = view.findViewById(R.id.md_preview_container);
        btnCodeMode = view.findViewById(R.id.btn_code_mode);
        btnPreviewMode = view.findViewById(R.id.btn_preview_mode);
        delete = view.findViewById(R.id.delete);
        noteSave = view.findViewById(R.id.note_save);

        // 初始化 Markwon
        markwon = Markwon.builder(requireContext())
                .usePlugin(ImagesPlugin.create())
                .usePlugin(HtmlPlugin.create())
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(content.getTextSize(), builder -> {
                    builder.inlinesEnabled(true);
                }))
                .build();

        editor = MarkwonEditor.create(markwon);

        content.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
        // 设置点击监听器
        delete.setOnClickListener(this);
        noteSave.setOnClickListener(this);
        btnCodeMode.setOnClickListener(this);
        btnPreviewMode.setOnClickListener(this);

        // 初始化 SQLiteHelper 和 ViewModel
        mSQLiteHelper = new SQLiteHelper(getContext());
        viewModel = new ViewModelProvider(this).get(EditorViewModel.class);

        // 获取传递的参数
        String id = getArguments() != null ? getArguments().getString("id") : null;
        viewModel.init(mSQLiteHelper, id);

        initData();
    }

    private void initData() {
        if (viewModel.hasId()) {
            content.setText(requireArguments().getString("content"));
            notename.setText(requireArguments().getString("name"));
            noteTime.setText(requireArguments().getString("time"));
            noteTime.setVisibility(View.VISIBLE);
        } else {
            noteTime.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.note_save) {
            saveContent();
        } else if (viewId == R.id.delete) {
            content.setText("");
        } else if (viewId == R.id.btn_code_mode) {
            toggleMode(false); // 切换到代码模式
        } else if (viewId == R.id.btn_preview_mode) {
            toggleMode(true); // 切换到预览模式
        }
    }

    private void saveContent() {
        String noteContent = content.getText().toString().trim();
        String noteName = notename.getText().toString().trim();
        SharedPreferences prefs = requireActivity().getSharedPreferences("NotebookPrefs", MODE_PRIVATE);
        String currentUserId = prefs.getString("userId", "-1"); // Default to "-1" if userId is not found


        if (noteContent.isEmpty()) {
            showToast(getString(R.string.empty_content_message)); // 使用资源中的消息
            return;
        }

        if (noteName.isEmpty()){
            noteName = noteContent.length() > 20 ? noteContent.substring(0, 20) : noteContent;
        }

        boolean success = viewModel.saveNote(currentUserId, noteContent, noteName);
        if (success) {
            showToast(viewModel.hasId() ? getString(R.string.note_updated_message) : getString(R.string.note_saved_message));
            Navigation.findNavController(requireView()).navigateUp(); // 返回到列表页面
        } else {
            showToast(viewModel.hasId() ? getString(R.string.update_failed_message) : getString(R.string.save_failed_message));
        }
    }

    private void toggleMode(boolean previewMode) {
        isPreviewMode = previewMode;

        if (isPreviewMode) {
            content.setVisibility(View.GONE);
            previewContainer.setVisibility(View.VISIBLE);

            // 渲染 Markdown 和 LaTeX
            String markdownText = content.getText().toString();
            markwon.setMarkdown(mdPreview, markdownText);
        } else {
            content.setVisibility(View.VISIBLE);
            previewContainer.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
