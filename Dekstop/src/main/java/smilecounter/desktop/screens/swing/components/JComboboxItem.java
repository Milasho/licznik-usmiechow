package smilecounter.desktop.screens.swing.components;

public class JComboboxItem {
    private String text;
    private String value;
    public JComboboxItem(){}
    public JComboboxItem(String text, String value){
        this.text = text;
        this.value = value;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    @Override
    public String toString(){
        return this.text;
    }
}
