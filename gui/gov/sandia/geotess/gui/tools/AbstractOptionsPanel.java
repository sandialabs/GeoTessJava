//- ****************************************************************************
//-
//- Copyright 2009 Sandia Corporation. Under the terms of Contract
//- DE-AC04-94AL85000 with Sandia Corporation, the U.S. Government
//- retains certain rights in this software.
//-
//- BSD Open Source License.
//- All rights reserved.
//-
//- Redistribution and use in source and binary forms, with or without
//- modification, are permitted provided that the following conditions are met:
//-
//-    * Redistributions of source code must retain the above copyright notice,
//-      this list of conditions and the following disclaimer.
//-    * Redistributions in binary form must reproduce the above copyright
//-      notice, this list of conditions and the following disclaimer in the
//-      documentation and/or other materials provided with the distribution.
//-    * Neither the name of Sandia National Laboratories nor the names of its
//-      contributors may be used to endorse or promote products derived from
//-      this software without specific prior written permission.
//-
//- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
//- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
//- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
//- ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
//- LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
//- CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
//- SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
//- INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
//- CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
//- ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
//- POSSIBILITY OF SUCH DAMAGE.
//-
//- ****************************************************************************

package gov.sandia.geotess.gui.tools;

import gov.sandia.geotess.GeoTessModel;
import gov.sandia.geotess.gui.mainlayout.GeoTessPresenter;
import gov.sandia.geotess.gui.interfaces.Function;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.SortedMap;

public abstract class AbstractOptionsPanel extends JPanel {

    protected GeoTessPresenter presenter;
    protected JFrame parent;

    private ModelGridDisplayPanel filePane;
    private SortedMap<String, Function> functionMap;
    private JList<String> list;
    private String selectedFunction;

    public AbstractOptionsPanel(GeoTessPresenter presenter, JFrame parent) {
        this.presenter = presenter;
        this.parent = parent;
        init();
    }

    private void init() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Used to space out all of the components from each other
        gbc.insets = new Insets(5, 5, 5, 5);

        this.filePane = new ModelGridDisplayPanel(presenter, parent);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(filePane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(makeFunctionPanel(), gbc);
    }

    public JPanel makeFunctionPanel() {
        JPanel p = new JPanel();
        p.setBorder(new TitledBorder("Function Calls"));
        p.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        p.add(functionsPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        p.add(buttonPanel(), gbc);
        return p;
    }

    private JPanel functionsPanel() {
        JPanel functions = new JPanel();
        functions.setPreferredSize(new Dimension(300, 150));
        functions.setLayout(new BoxLayout(functions, BoxLayout.Y_AXIS));

        this.functionMap = specificFunctionList();
        this.list = new JList<>(functionMap.keySet().toArray(new String[functionMap.keySet().size()]));
        this.list.addListSelectionListener(new ListListener());
        JScrollPane scroll = new JScrollPane(list);
        functions.add(scroll);

        return functions;
    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        JButton accept = new JButton("Run Function...");
        JButton help = new JButton("Help...");
        JButton clear = new JButton("Clear Display");

        accept.addActionListener(new AcceptListener());
        help.addActionListener(helpListener());
        clear.addActionListener(new ClearListener());

        panel.add(accept);
        panel.add(help);
        panel.add(clear);

        return panel;
    }

    public Function getSelectedFunction() {
        if(selectedFunction == null)
            return null;
        return functionMap.get(selectedFunction);
    }

    public String getSelected() {
        return selectedFunction;
    }

    public void updateDialogModel(String modelFile, String grid) throws IOException {
        if(modelGridErrorsExist(modelFile, grid))
            throw new IOException();
        this.filePane.updateModelValue(modelFile);
        this.filePane.updateGridValue(grid);
    }

    private boolean modelGridErrorsExist(String modelFile, String gridDir) {
        if(!GeoTessModel.isGeoTessModel(new File(modelFile)))
            return true;
        try {
            GeoTessModel model = new GeoTessModel(modelFile, gridDir);
        } catch(IOException e) {
            return true;
        }
        return false;
    }

    public abstract SortedMap<String, Function> specificFunctionList();

    public abstract ActionListener helpListener();

    private class ClearListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            presenter.clearUtilityText();
        }
    }

    private class ListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent arg0) {
            selectedFunction = list.getSelectedValue();
        }
    }

    private class AcceptListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(getSelectedFunction() == null)
                presenter.writeToUtilityPanel("Please Select A Function To Run From the List on the Left\n\n");
            else {
                GeoTessModel model = null;
                if(presenter.hasModel()) {
                    try {
                        model = new GeoTessModel(presenter.getModel(), presenter.getGrid());
                    } catch(IOException ee) {
                        ee.printStackTrace();
                    }
                }

                getSelectedFunction().updateModel(model);
                getSelectedFunction().execute();
            }
        }
    }
}
