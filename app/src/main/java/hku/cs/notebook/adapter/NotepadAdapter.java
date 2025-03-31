package hku.cs.notebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import hku.cs.notebook.R;
import hku.cs.notebook.bean.NotepadBean;

public class NotepadAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<NotepadBean> list;

    public NotepadAdapter(Context context, List<NotepadBean> list) {
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    public void updateData(List<NotepadBean> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public NotepadBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        NotepadBean noteInfo = getItem(position);
        viewHolder.tvNotepadContent.setText(noteInfo.getNotepadContent());
        viewHolder.tvNotepadTime.setText(noteInfo.getNotepadTime());

        return convertView;
    }

    static class ViewHolder {
        TextView tvNotepadContent;
        TextView tvNotepadTime;

        public ViewHolder(View view) {
            tvNotepadContent = view.findViewById(R.id.item_content);
            tvNotepadTime = view.findViewById(R.id.item_time);
        }
    }
}
