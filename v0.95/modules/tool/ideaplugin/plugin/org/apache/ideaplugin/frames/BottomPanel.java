package org.apache.ideaplugin.frames;

import org.apache.ideaplugin.bean.ArchiveBean;
import org.apache.ideaplugin.bean.ObjectKeeper;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
*/

/**
 * Author: Deepal Jayasinghe
 * Date: Sep 18, 2005
 * Time: 12:01:58 PM
 */
public class BottomPanel extends JPanel implements ActionListener {
    JButton butBack;
    JButton butNext;
    JButton btnFinsh;
    JButton btnCanclel;
    ServiceArciveFrame parent;
    private ArchiveBean bean;

    private JPanel currentPanel = null;

    public BottomPanel(JPanel currPanel, ServiceArciveFrame parent, ArchiveBean bean) {
        BottomPanelLayout customLayout = new BottomPanelLayout();

        setFont(new Font("Helvetica", Font.PLAIN, 12));
        setLayout(customLayout);
        this.parent = parent;
        this.bean = bean;

        butBack = new JButton("< Back");
        add(butBack);
        butBack.addActionListener(this);

        butNext = new JButton("Next >");
        butNext.setEnabled(false);
        add(butNext);
        butNext.addActionListener(this);

        btnFinsh = new JButton("Finish");
        btnFinsh.setEnabled(false);
        add(btnFinsh);
        btnFinsh.addActionListener(this);

        btnCanclel = new JButton("Cancel");
        add(btnCanclel);
        btnCanclel.addActionListener(this);

        this.currentPanel = currPanel;

        if (((ObjectKeeper) currentPanel).getPrivious() == null) {
            butBack.setEnabled(false);
        }
        setSize(getPreferredSize());
    }

    public void setEnable(boolean back, boolean next, boolean finish, boolean cancel) {
        butBack.setEnabled(back);
        butNext.setEnabled(next);
        btnFinsh.setEnabled(finish);
        btnCanclel.setEnabled(cancel);
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == btnCanclel) {
            parent.setVisible(false);
        }
        if (obj == butNext) {
            ((ObjectKeeper) currentPanel).fillBean(bean);
            currentPanel = ((ObjectKeeper) currentPanel).getNext();
            parent.Next(currentPanel);
            if (currentPanel instanceof DescriptorFile) {
                parent.setEnable(false, true, false, true);
            }
        } else if (obj == btnFinsh) {
            ((ObjectKeeper) currentPanel).fillBean(bean);
            bean.finsh();

            JOptionPane.showMessageDialog(parent, "Service creation successful!",
                    "Axis2 Service creation", JOptionPane.INFORMATION_MESSAGE);
            parent.setVisible(false);

        } else if (obj == butBack) {
            currentPanel = ((ObjectKeeper) currentPanel).getPrivious();
            parent.Back(currentPanel);
        }
    }
}

class BottomPanelLayout implements LayoutManager {

    public BottomPanelLayout() {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

        Insets insets = parent.getInsets();
        dim.width = 606 + insets.left + insets.right;
        dim.height = 64 + insets.top + insets.bottom;

        return dim;
    }

    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();

        Component c;
        c = parent.getComponent(0);
        if (c.isVisible()) {
            c.setBounds(insets.left + 176, insets.top + 24, 80, 24);
        }
        c = parent.getComponent(1);
        if (c.isVisible()) {
            c.setBounds(insets.left + 260, insets.top + 24, 80, 24);
        }
        c = parent.getComponent(2);
        if (c.isVisible()) {
            c.setBounds(insets.left + 344, insets.top + 24, 80, 24);
        }
        c = parent.getComponent(3);
        if (c.isVisible()) {
            c.setBounds(insets.left + 426, insets.top + 24, 80, 24);
        }
    }
}
