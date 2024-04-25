package smilecounter.desktop.screens.swing.components;

import javax.swing.*;

public class ViewTitleComponent extends JPanel {
    private static final long serialVersionUID = 1L;

    private JLabel text;

    public ViewTitleComponent(){
        initGui();
        setVisible(true);
    }

    private void initGui(){
        text = new JLabel();
        text.setFont(text.getFont().deriveFont(3));
        add(text);

        initLayout();
    }

    private void initLayout(){
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(text)
        );

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(text)
        );
    }

    public void refreshText(String t){
        text.setText(t);
    }
}