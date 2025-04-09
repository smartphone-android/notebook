package hku.cs.notebook.bean;

public class NotepadBean {
    private String id;
    private String notepadName;
    private String notepadContent;
    private String notepadTime;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
