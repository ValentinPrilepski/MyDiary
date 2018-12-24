package com.mydiary.notelist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mydiary.R;
import com.mydiary.model.Note;
import com.mydiary.utils.FormatDate;
import com.mydiary.utils.SelectableModelWrapper;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;




public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private static final String TAG = NoteAdapter.class.getSimpleName();
    private CallbacksListNote mCallbacksListNote;
    private Context mContext;
    private List<SelectableModelWrapper<Note>> mNotes;


    NoteAdapter(List<SelectableModelWrapper<Note>> notes) {
        mNotes = notes;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        mCallbacksListNote = (CallbacksListNote) mContext;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        return new NoteHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        SelectableModelWrapper<Note> note = mNotes.get(position);
        holder.bind(note);

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void setNotes(List<SelectableModelWrapper<Note>> notes) {
        mNotes = notes;
    }

    public void updateItem(SelectableModelWrapper<Note> note) {
        int index = mNotes.indexOf(note);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    @Override
    public long getItemId(int position) {
        return mNotes.get(position).getModel().getId().hashCode();
    }

    public void cancelSelectionMode() {
        if (mNotes != null && !mNotes.isEmpty()) {
            mNotes.get(0).setSelectableMode(false, true);
        }
    }

    public class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.tv_note_title)
        TextView mTitleTextView;
        @BindView(R.id.tv_note_date)
        TextView mDateTextView;
        @BindView(R.id.checkbox_delete)
        CheckBox mDeleteCb;

        private SelectableModelWrapper<Note> mNote;

        NoteHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_note, parent, false));
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mNote.isSelectableMode()) {
                mNote.switchSelection();
                notifyItemChanged(getAdapterPosition());
                return;
            }
            mCallbacksListNote.onNoteSelected(mNote.getModel().getId());
        }

        public void bind(SelectableModelWrapper<Note> note) {
            mNote = note;
            Note model = mNote.getModel();
            Date date = new Date(model.getDate());
            mTitleTextView.setText(model.getTitle());
            mDateTextView.setText(FormatDate.updateFormatDate(date));
            mDeleteCb.setOnCheckedChangeListener(null);
            mDeleteCb.setVisibility(mNote.isSelectableMode() ? View.VISIBLE : View.GONE);
            mDeleteCb.setChecked(mNote.isSelected());
            mDeleteCb.setOnCheckedChangeListener((buttonView, isChecked) -> mNote.setSelected(isChecked));

        }

        @Override
        public boolean onLongClick(View v) {
            if (!mNote.isSelectableMode()) {
                mNote.setSelectableMode(true, true);
                mNote.setSelected(true);
            }
            return mNote.isSelectableMode();
        }
    }


}

