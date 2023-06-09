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

package gov.sandia.geotess.gui.extractdata.dialogs;

import gov.sandia.geotess.GeoTessException;
import gov.sandia.geotess.GeoTessModel;
import gov.sandia.geotess.gui.enums.FileOperation;
import gov.sandia.geotess.gui.enums.MethodHelp;
import gov.sandia.geotess.gui.enums.ParameterHelp;
import gov.sandia.geotess.gui.mainlayout.GeoTessPresenter;
import gov.sandia.geotess.gui.tools.*;
import gov.sandia.gmp.util.numerical.polygon.GreatCircle.GreatCircleException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SliceDistAzDialog extends AbstractModelNeededDialog {

	private LatLonComponents latlon1;
	private TitleFieldComponents distance;
	private TitleFieldComponents azimuth;
	private TitleFieldComponents nPoints;
	private TitleFieldComponents maxRadSpacing;
    private DeepestShallowestComponents layers;
	private HorizontalInterpolationComponents horizontal;
    private RadialInterpolationComponents radial;
	private String spatialCoords;
	private ReciprocalComponents reciprocal;
	private AttributeCheckboxPanel attributes;
	private FileIOComponents output;
	
	public SliceDistAzDialog(GeoTessPresenter presenter, JFrame parent) {
		super(presenter, parent, "SliceDistAz");
	}

	@Override
	public ActionListener getAcceptButtonListener() {
		return new SubmitButtonListener();
	}

	@Override
	public String methodHelp() {
		return MethodHelp.SLICE.getMethodTip();
	}

	@Override
	public JPanel makeNewDialog(GeoTessModel model) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(latlonPanel());
		p.add(distAzPanel());
		p.add(nPointsPanel());
		p.add(maximumRadialSpacingPanel());
        this.layers = new DeepestShallowestComponents(model);
        this.horizontal = new HorizontalInterpolationComponents();
        this.radial = new RadialInterpolationComponents();
        
        p.add(layers.getDeepLabel());
        p.add(layers.getDeepBox());

        p.add(layers.getShallowLabel());
        p.add(layers.getShallowBox());

        p.add(horizontal.getTitle());
        p.add(horizontal.getButtons());

        p.add(radial.getTitle());
        p.add(radial.getButtons());

        p.add(spatialCoordinates());

        this.reciprocal = new ReciprocalComponents();
        p.add(reciprocal.getButtons());

        p.add(attributes(model));

        this.output =  new FileIOComponents("Output File: ", FileOperation.SAVE,parent, ParameterHelp.OUTPUT);
        p.add(output.getTitle());
        p.add(output.getTextBox());
        p.add(output.getButton(), "wrap");
        return p;
	}

	private JPanel distAzPanel() {
		JPanel p = new JPanel();
		distance = new TitleFieldComponents("Distance to Last Point: ", 4," degrees", ParameterHelp.DIST_LAST_POINT);
		azimuth = new TitleFieldComponents("Azimuth to Last Point: ", 4, " degrees", ParameterHelp.AZIMUTH_LAST_POINT);
        p.add(distance.getTitle());
        p.add(distance.getTextBox());

        p.add(azimuth.getTitle());
        p.add(azimuth.getTextBox());

		return p;
	}

	private JPanel attributes(GeoTessModel model) {
		return attributes = new AttributeCheckboxPanel(model);
	}

	private JPanel spatialCoordinates() {
		final String[] coordinates = { "Distance", "Depth", "Radius", "X", "Y",
				"Z", "Lat", "Lon" };

		JPanel p = new JPanel();
		ButtonGroup bg = new ButtonGroup();
		ActionListener al = new RadioButtonListener();
		for (String s : coordinates) {
			JRadioButton button = new JRadioButton(s);
			button.addActionListener(al);
			if(s.equals("Distance"))
			{
				button.setSelected(true);
				spatialCoords = s;
			}
			bg.add(button);
			p.add(button);
		}
		return p;
	}

	private JPanel maximumRadialSpacingPanel(){
        JPanel p = new JPanel();
        this.maxRadSpacing = new TitleFieldComponents("N Points: ", 4, ParameterHelp.N_POINTS);
        p.add(maxRadSpacing.getTitle());
        p.add(maxRadSpacing.getTextBox());
        return p;
	}

	private JPanel nPointsPanel() {
        JPanel p = new JPanel();
        this.nPoints = new TitleFieldComponents("N Points: ", 4, ParameterHelp.N_POINTS);
        p.add(nPoints.getTitle());
        p.add(nPoints.getTextBox());
        return p;
	}

    private JPanel latlonPanel()
    {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4,3,5,5));
        this.latlon1 = new LatLonComponents("Latitude 1: ", "Longitude 1: ");

        p.add(latlon1.getLatTitle());
        p.add(latlon1.getLatTextBox());

        p.add(latlon1.getLonTitle());
        p.add(latlon1.getLonTextBox());

        return p;
    }

	private class RadioButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			spatialCoords = ((JRadioButton) e.getSource()).getText();
		}

	}

	private class SubmitButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				presenter.sliceDistAz(latlon1.getLat(), latlon1.getLon(),
						Double.parseDouble(distance.getFieldValue()), 
						Double.parseDouble(azimuth.getFieldValue()),
						Integer.parseInt(nPoints.getFieldValue()),
						Double.parseDouble(maxRadSpacing.getFieldValue()),
						layers.getDeepestIndex(), layers.getShallowestIndex(),
						horizontal.getInterpolation(),
						radial.getInterpolation(), spatialCoords,
						reciprocal.getSelected(), attributes.getCheckedAttributeIndexes(),
						output.getText());
				destroy();
				
			} catch (NumberFormatException | IOException | GreatCircleException
					| GeoTessException e) {
				setErrorVisible(true);
			}
		}

	}

}