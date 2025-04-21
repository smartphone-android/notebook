package hku.cs.notebook.bean;

public class NotepadBean {
    private String noteId;
    private String notepadName;
    private String notepadContent;
    private String notepadTime;
    public String getNoteId() {
        return noteId;
    }
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    public String getNotepadName() {
        return notepadName;
    }
    public void setNotepadName(String notepadName) {
        this.notepadName = notepadName;
    }
    public String getNotepadContent() {
        return notepadContent;
    }
    public void setNotepadContent(String notepadContent) {
        this.notepadContent = notepadContent;
    }
    public String getNotepadTime() {
        return notepadTime;
    }
    public void setNotepadTime(String notepadTime) {
        this.notepadTime = notepadTime;
    }
}
