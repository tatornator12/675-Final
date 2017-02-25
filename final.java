package edu.umbc.gis;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.esri.toolkit.OverviewMap;
import com.esri.toolkit.legend.JLegend;
import com.esri.toolkit.overlays.ScaleBarOverlay;
import com.esri.client.local.GPServiceType;
import com.esri.client.local.LocalGeoprocessingService;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.Geometry.Type;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Graphic;
import com.esri.core.portal.Portal;
import com.esri.core.portal.WebMap;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.Style;
import com.esri.core.tasks.ags.geoprocessing.GPFeatureRecordSetLayer;
import com.esri.core.tasks.ags.geoprocessing.GPLinearUnit;
import com.esri.core.tasks.ags.geoprocessing.GPParameter;
import com.esri.core.tasks.ags.geoprocessing.Geoprocessor;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.map.ArcGISDynamicMapServiceLayer;
import com.esri.map.FeatureLayer;
import com.esri.map.GraphicsLayer;
import com.esri.map.JMap;
import com.esri.map.MapEvent;
import com.esri.map.MapEventListener;

public class Tay_FP {

  private JFrame window;
  private JMap map;
  private Envelope fullE;
  private Envelope e1;
  private Locator geolocator;
  private Locator geolocator2;
  private GraphicsLayer geocodeLayer;
  private GraphicsLayer geocodeLayer2;
  private GraphicsLayer lineLayer;
  private WebMap myMap2;
  private int clickCount = 0;
  private ArcGISDynamicMapServiceLayer baseMap;
  private LocalGeoprocessingService clipFeaturesService;
  private JTextArea clippedResults;
  private JScrollPane scroll;
  private JTextArea placeInput1;
  private JTextArea placeInput2;
  private JTextArea bufferInput;
  private int count = 1;
  private OverviewMap overview;
  private JPanel leftPanel;
  private JPanel rightPanel;
  private JPanel bottomPanel;
  private JLabel panelLabel;
  private JLabel result;
  private JLabel instruct1;
  private JLabel instruct2;
  private JLabel buffer;
  private JLegend maplegend;
  private JLabel mapLegendLabel;
  private ScaleBarOverlay scaleBar;
  

  // Find the first geocode point
  
  public String findLoc(String place){
	  String coord="";
	  geocodeLayer.removeAll();
	  if(place !=null && place.trim().length()>0){
		  LocatorFindParameters findParams = new LocatorFindParameters(place);
		  findParams.setOutSR(map.getSpatialReference());
		  findParams.setMaxLocations(1);
		  try{
		    List<LocatorGeocodeResult> results=geolocator.find(findParams); 
		    if(results!=null && results.size()>0){
		            SimpleMarkerSymbol theMarker1 = new SimpleMarkerSymbol(Color.RED, 8, Style.CIRCLE);
		            Graphic thelocation1 = new Graphic(results.get(0).getLocation(), theMarker1);
		            geocodeLayer.addGraphic(thelocation1);
		            coord = results.get(0).getLocation().getY()+ "," + results.get(0).getLocation().getX();
		        }
		    
		  }catch(Exception ex){
		         coord= "Error in finding the place";
		  }
	  }else{
		  coord= "No valid input";
	  }
	  return coord;
  }
  
  
  // Find the second geocode point
  
  public String findLoc2(String place2){
	  String coord2 = "";
	  geocodeLayer2.removeAll();
	  if(place2 !=null && place2.trim().length()>0){
		  LocatorFindParameters findParams2 = new LocatorFindParameters(place2);
		  findParams2.setOutSR(map.getSpatialReference());
		  findParams2.setMaxLocations(1);
		  try{
		    List<LocatorGeocodeResult> results2 = geolocator2.find(findParams2); 
		    if(results2 !=null && results2.size()>0){
		            SimpleMarkerSymbol theMarker2 = new SimpleMarkerSymbol(Color.RED, 8, Style.CIRCLE);
		            Graphic thelocation2 = new Graphic(results2.get(0).getLocation(), theMarker2);
		            geocodeLayer2.addGraphic(thelocation2);
		            coord2 = results2.get(0).getLocation().getY() + "," + results2.get(0).getLocation().getX();
		        }  
		  }catch(Exception ex){
		         coord2 = "Error in finding the place";
		  }
	  }else{
		  coord2 = "No valid input";
	  }
	  return coord2;
	  
  }
  
  public Tay_FP() {
    window = new JFrame();
    window.setSize(1200, 1000);
    window.setLocationRelativeTo(null); // center on screen
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.getContentPane().setLayout(new BorderLayout(0, 0));
    window.setTitle("Final Project");

    // dispose map just before application window is closed.
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        super.windowClosing(windowEvent);
        if (map != null) {
			map.dispose();
		}
      }
    });

    // Initialize the map
    
    baseMap = new ArcGISDynamicMapServiceLayer("http://localhost:6080/arcgis/rest/services/USA_Topo/MapServer");
    map = new JMap();
    map.getLayers().add(baseMap);
    overview = null;

    try {       
        // Set the overview map size and border
    	
    	ArcGISDynamicMapServiceLayer overviewLayer = new ArcGISDynamicMapServiceLayer("http://localhost:6080/arcgis/rest/services/World_Topo/MapServer");
    	overview = new OverviewMap(map, overviewLayer);
    	overview.setSize(200, 200);
    	overview.setBorder(new LineBorder(Color.GRAY, 4));
    	
    	// Add the scalebar to the map as an overlay
        
        scaleBar = new ScaleBarOverlay();
        map.addMapOverlay(scaleBar);
       
   }catch(Exception e){
    }
    
    // Initialize geocode layers and locators
    
    geocodeLayer = new GraphicsLayer();
    geocodeLayer2 = new GraphicsLayer();
    map.getLayers().add(geocodeLayer);
    map.getLayers().add(geocodeLayer2);


    geolocator = Locator.createOnlineLocator("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
    geolocator2 = Locator.createOnlineLocator("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
    
    // Initialize the clip feature service
    
    clipFeaturesService = new LocalGeoprocessingService("C:\\Program Files (x86)\\ArcGIS SDKs\\java10.2.4\\sdk\\samples\\data\\gpks\\ClipFeatures\\ClipFeatures.gpk");
    clipFeaturesService.setServiceType(GPServiceType.EXECUTE);
    clipFeaturesService.startAsync();
    
    // Create a panel to the left of the map to display the Overview Map
    // and the Map Legend
    
    leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.setSize(200, 580);
    leftPanel.setBackground(Color.WHITE);
    leftPanel.setBorder(new LineBorder(Color.BLUE, 2));
    
    // Create a panel to the right of the map to display the clipped counties
    
    rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    rightPanel.setSize(200, 580);
    rightPanel.setBackground(Color.WHITE);
    rightPanel.setBorder(new LineBorder(Color.BLUE, 2));
    
    // Overview Map Panel Label
    
    panelLabel = new JLabel("Overview Map");
    panelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panelLabel.setFont(new Font("Dialog", Font.BOLD, 18));
    panelLabel.setBackground(Color.white);
    panelLabel.setForeground(Color.black);
    
    // Create a map legend and label
    
    maplegend = new JLegend(map);
    mapLegendLabel = new JLabel ("Map Legend");
    mapLegendLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mapLegendLabel.setFont(new Font("Dialog", Font.BOLD, 18));
    mapLegendLabel.setBackground(Color.white);
    mapLegendLabel.setForeground(Color.black);
    maplegend.setPreferredSize(new Dimension(200, 350));
    maplegend.setBorder(new LineBorder(new Color(100, 100, 255), 4));    
   
    // Create a bottom panel for inputs
    
    bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    
    // Create labels at the bottom to input addresses and buffer distance in miles
    
    instruct1 = new JLabel("Please input an address or a place name to locate it on map:");    
    placeInput1 = new JTextArea();
    placeInput1.setAlignmentX(Component.LEFT_ALIGNMENT);
    placeInput1.setEditable(true);
    
    instruct2 = new JLabel("Please input an address or a place name to locate it on map:");    
    placeInput2 = new JTextArea();
    placeInput2.setAlignmentX(Component.LEFT_ALIGNMENT);
    placeInput2.setEditable(true);
    
    buffer = new JLabel("Input buffer distance in miles:");    
    bufferInput = new JTextArea();
    bufferInput.setAlignmentX(Component.LEFT_ALIGNMENT);
    bufferInput.setEditable(true);
    
    // Create a label and text area to display the clipped counties
    
    result = new JLabel("Clipped Counties:");
    clippedResults = new JTextArea(20, 20);
    clippedResults.setAlignmentX(Component.LEFT_ALIGNMENT);
    clippedResults.setEditable(false);
    
    scroll = new JScrollPane(clippedResults);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
 
    // Add all of the elements to the various panels
    
    leftPanel.add(panelLabel);
    leftPanel.add(overview);
    leftPanel.add(mapLegendLabel);
    leftPanel.add(maplegend);
    bottomPanel.add(instruct1);
    bottomPanel.add(placeInput1);
    bottomPanel.add(instruct2);
    bottomPanel.add(placeInput2);
    bottomPanel.add(buffer);
    bottomPanel.add(bufferInput);
    rightPanel.add(result);
    rightPanel.add(scroll);

    // Create Tool Bar and add a file chooser button
    
    JToolBar toolBar = new JToolBar("Map Tool Bar");
    JButton openButton = new JButton(new ImageIcon("C:\\FinalProject\\src\\edu\\umbc\\gis\\open.jpg"));
    toolBar.add(openButton);
    toolBar.setPreferredSize(new Dimension(450, 50));
    
    // Create "Zoom In" Button
    
    JButton zoomInButton = new JButton(new ImageIcon("C:\\FinalProject\\src\\edu\\umbc\\gis\\Gnome-Zoom-In-32.png"));
    toolBar.add(zoomInButton);
    zoomInButton.addMouseListener(new MouseAdapter() {
    	public void mouseClicked(MouseEvent evt) {
    		map.zoom(.5);
    	}});
    
    // Create "Zoom Out" Button
    
    JButton zoomOutButton = new JButton(new ImageIcon("C:\\FinalProject\\src\\edu\\umbc\\gis\\magnifier_zoom_out.png"));
    toolBar.add(zoomOutButton);
    zoomOutButton.addMouseListener(new MouseAdapter() {
    	public void mouseClicked(MouseEvent evt) {
    		map.zoom(2);
    	}});
    
    // Create "Extent" Button
    
    JButton extentButton = new JButton(new ImageIcon("C:\\FinalProject\\src\\edu\\umbc\\gis\\Extent.png"));
    toolBar.add(extentButton);
    extentButton.addMouseListener(new MouseAdapter() {
    	public void mouseClicked(MouseEvent evt) {
    		map.zoomTo(fullE);
    	}});
    
    // Create "Toggle" Button
    
    JButton toggleButton = new JButton(new ImageIcon("C:\\FinalProject\\src\\edu\\umbc\\gis\\Icon-Globe30.png"));
    toolBar.add(toggleButton);
    toggleButton.addMouseListener(new MouseAdapter() {
    	public void mouseClicked(MouseEvent evt) {
    		
    		// Using an odd and even click count will toggle the basemaps and an error
    		// message will pop-up if the map is unable to load.
    		
    		try {
    			clickCount++;
    		    
    		if(clickCount % 2 == 1){
    			
    			// Reset the map
    			
    			resetMap();
    			
    			// Remove the previous map's left panel elements
    			
    			leftPanel.remove(overview);
    			leftPanel.remove(maplegend);
    		    leftPanel.remove(mapLegendLabel);
    			leftPanel.remove(panelLabel);
    			
    			// Replaces the previous map with the WebMap
    			
    			Portal mapPortal2 = new Portal("https://www.arcgis.com", null);
    		    myMap2 = WebMap.newInstance("d460088f1cd84063bbdf8970cf2fa0f1", mapPortal2);
    		    map.loadWebMap(myMap2);
    			maplegend = new JLegend(map);
    			
    			// Reset the overview map and left panel elements
    			
    			ArcGISDynamicMapServiceLayer overviewLayer2 = new ArcGISDynamicMapServiceLayer("https://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer");
    	    	overview = new OverviewMap(map, overviewLayer2);
    	    	overview.setSize(200, 200);
    	    	overview.setBorder(new LineBorder(Color.GRAY, 4));
    	    	leftPanel.setSize(200, 580);
    	    	leftPanel.add(panelLabel);
    	    	leftPanel.add(overview);
    	        leftPanel.add(mapLegendLabel);
    	    	leftPanel.add(maplegend);
    	    	
    			
    		}else{
    			
    			// Reset the map
    			
    			resetMap();
    			
    			// Remove the previous map's left panel elements
    			
    			leftPanel.remove(overview);
    			leftPanel.remove(maplegend);
    		    leftPanel.remove(mapLegendLabel);
    			leftPanel.remove(panelLabel);
    			
    			// Replaces the WebMap with the original map
    			
    			baseMap = new ArcGISDynamicMapServiceLayer("http://localhost:6080/arcgis/rest/services/USA_Topo/MapServer");
    			map.getLayers().add(baseMap);
    			maplegend = new JLegend(map);
    		
    			// Reset the overview map and left panel elements
    			
    			ArcGISDynamicMapServiceLayer overviewLayer3 = new ArcGISDynamicMapServiceLayer("http://localhost:6080/arcgis/rest/services/World_Topo/MapServer");
    			overview = new OverviewMap(map, overviewLayer3);
    	    	overview.setSize(200, 200);
    	    	overview.setBorder(new LineBorder(Color.GRAY, 4));
    	    	leftPanel.add(panelLabel);
    	    	leftPanel.add(overview);
    	        leftPanel.add(mapLegendLabel);
    	    	leftPanel.add(maplegend);
    			
    		}} catch (Exception e) { 
    			
    			// Creates a content pane to display an error message
    			
    			JLayeredPane contentPane = new JLayeredPane();
            	contentPane.setBounds(100, 100, 1000, 700);
            	contentPane.setLayout(new BorderLayout(0, 0));
            	contentPane.setVisible(true);
	          
            	JOptionPane.showMessageDialog(contentPane.getParent(), 
                        wrap("Unable to load the map. " + e.getLocalizedMessage()), 
                        "", 
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
    		}			
    		
    	}});
    		
    // Create "Buffer" Button
    
    JButton bufferButton = new JButton(new ImageIcon("C:\\FinalProject\\src\\edu\\umbc\\gis\\st_buffer01.png"));
    toolBar.add(bufferButton);
    bufferButton.addMouseListener(new MouseAdapter() {
    	public void mouseClicked(MouseEvent evt) {
    		    		
    		// Create strings to store the numbered coordinates produced by the geocoding
    		
    		String input1 = findLoc(placeInput1.getText());
    		String input2 = findLoc2(placeInput2.getText());
    		
    		// Create string array lists with the numbered coordinates and split the points
    		// with the delimiter ","
    		
    		String[] coords1 = input1.split(",", -1);
    		String[] coords2 = input2.split(",", -1);
    		
    		// Parse the coordinates into double integers and store them as geometry points
    	
    		Point mapPoint1 = new Point(Double.parseDouble(coords1[1]), Double.parseDouble(coords1[0]));
    		Point mapPoint2 = new Point(Double.parseDouble(coords2[1]), Double.parseDouble(coords2[0]));
    		
    		// Create a polyline with the coordinates and display the polyline as a graphic on the
    		// map
    		
    		Polyline line = new Polyline();
    	    line.startPath(mapPoint1);
    	    line.lineTo(mapPoint2);
    	    lineLayer = new GraphicsLayer();
    	    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(Color.BLUE, 3, SimpleLineSymbol.Style.SOLID);
    	    Graphic lineGraphic = new Graphic(line, lineSymbol);
    	    lineLayer.addGraphic(lineGraphic);
    	    map.getLayers().add(lineLayer);
    	    
    	    // Create a string to store the buffer input and parse the number into a double integer
    	    
    	    String input3 = bufferInput.getText();   
    	    Double bufferDouble = new Double(Double.parseDouble(input3));
        
    	    // Initialize the clipped service geoprocessor and set the spatial reference
    	    // for the geoprocessing
    	    
    	    Geoprocessor geoprocessor = new Geoprocessor(clipFeaturesService.getUrlGeoprocessingService() + "/ClipFeatures");
    		geoprocessor.setProcessSR(map.getSpatialReference()); 
    		geoprocessor.setOutSR(map.getSpatialReference());

    		// Set the geoprocessor parameters
    		
    		List<GPParameter> parameters = new ArrayList<>();
    		GPFeatureRecordSetLayer inputFeature = new GPFeatureRecordSetLayer("Input");
    		inputFeature.setGeometryType(Type.POLYLINE);
    		inputFeature.setSpatialReference(map.getSpatialReference());
    		inputFeature.addGraphic(new Graphic(line,new SimpleMarkerSymbol(Color.BLUE, 8, Style.CIRCLE)));
    		GPLinearUnit linearUnit = new GPLinearUnit("Linear_Unit");
    		linearUnit.setUnits("esriMiles");
    		linearUnit.setDistance(bufferDouble);
    		parameters.add(inputFeature);
    		parameters.add(linearUnit);
    		
    		// Sets the extent of the map to the polyline when the polyline is created
    		
    		map.setExtent(line);

    		// Creates a callback listener for the geoprocessor
    		
    		geoprocessor.executeAsync(parameters, new CallbackListener<GPParameter[]>() {
    				@Override
    				public void onError(Throwable ex) {
    					ex.printStackTrace();
    				}
    				@Override
    				public void onCallback(GPParameter[] result) {		            
    					     processResult(result);
    				}
    				}
    			);
    	    
    	}});
    
    // Add a listener to open up a file chooser dialog
    
    openButton.addMouseListener(new MouseAdapter() {    	
    	 public void mouseClicked(MouseEvent evt) { 
    		 
    		// Set the file filter to .shp (shape) files only
    		 
    			FileNameExtensionFilter filter = new FileNameExtensionFilter(null, "shp");
    			final JFileChooser fc = new JFileChooser();
    			fc.setFileFilter(filter);
    			
    		// Allow the user to open up the dialog box to choose a file
    			
    			int returnVal = fc.showOpenDialog(null);
    			if (returnVal == JFileChooser.APPROVE_OPTION) {
    			      try {
    			    	  
    			    	  // Selects the shapefile and adds it to the map.
    			    	  
    			    	  File file = fc.getSelectedFile();
  		              	  String filePath = file.getAbsolutePath();

  		              	  // If the map's spatial reference is equal to the shapefile's
  		              	  // spatial reference, the shapefile will be added to the map
  		              	  
  		              	  ShapefileFeatureTable myShape = new ShapefileFeatureTable(filePath);
  		              	  FeatureLayer myFeature1 = new FeatureLayer(myShape);
  		              	  SimpleMarkerSymbol mySym = new SimpleMarkerSymbol(Color.red, 10, SimpleMarkerSymbol.Style.CIRCLE);
  		              	  SimpleRenderer myRend = new SimpleRenderer(mySym);
  		              	  myFeature1.setRenderer(myRend);

  		             	  if(map.getSpatialReference().equals(myShape.getSpatialReference()) {
  		             	
    		              	map.getLayers().add(myFeature1);
    		              	
  		             	  }else{
  		             		  
  		             		// If the spatial reference does not match, an error message
  		             		// will pop-up
  		             		
  		              		JLayeredPane contentPane = new JLayeredPane();
  		              		contentPane.setBounds(100, 100, 1000, 700);
  		              		contentPane.setLayout(new BorderLayout(0, 0));
  		              		contentPane.setVisible(true);
    			          
  		              		JOptionPane.showMessageDialog(contentPane.getParent(), "The shapefile cannot be displayed. "
    			          		+ "It does not have the same Spatial Reference.",
    			        		  filePath, JOptionPane.ERROR_MESSAGE);
    			   
  		              	  } 
    			      } catch (Exception e) {
    }
    }
    			              	
    			              	
  	 }

   }); 
    
    	
    map.addMapEventListener(new MapEventListener() {
    	  @Override
    	  public void mapReady(MapEvent event) {
    	    // TODO Auto-generated method stub

    		  fullE = map.getFullExtent();
    	  }
    	  @Override
    	  public void mapExtentChanged(MapEvent event) {
    	    // TODO Auto-generated method stub
    	  }
    	  @Override
    	  public void mapDispose(MapEvent event) {
    	    // TODO Auto-generated method stub
    	  }
    	}); // semi-colon to add
    
    
    
    // Add the map, panels, and tool bar to the JFrame's content pane
    
    window.add(map, BorderLayout.CENTER);
    window.add(toolBar,BorderLayout.PAGE_START);
    window.add(leftPanel, BorderLayout.WEST);
    window.add(bottomPanel, BorderLayout.SOUTH);
    window.add(rightPanel, BorderLayout.EAST);
    
  }
  
  // A method to process the results from the geoprocessing process using the
  // geoprocessing parameters
  
  private void processResult(GPParameter[] result) {
		for (GPParameter outputParameter : result) {
		      if (outputParameter instanceof GPFeatureRecordSetLayer) {
		        GPFeatureRecordSetLayer gpFeature = (GPFeatureRecordSetLayer) outputParameter;
		        for (Graphic graphic : gpFeature.getGraphics()) {
		        			        	
		        	// Adds the buffered graphic to the line layer created earlier
		        	
		        	lineLayer.addGraphic(new Graphic(graphic.getGeometry(), new SimpleFillSymbol(new Color(0, 0, 200, 120), new SimpleLineSymbol(new Color(0, 0, 200), 2))));
		        	
		        	// Stores the results of the clipped counties from the buffer in a string
		        	// and adds them to the text area created earlier.
		        	
		        	String countiesString = graphic.getAttributeValue("NAME").toString();
		        	clippedResults.append(countiesString + System.lineSeparator());

		        }
		      }
		    }
  } 
  
  
  private String wrap(String str) {
	    // create a HTML string that wraps text when longer
	    return "<html><p style='width:200px;'>" + str + "</html>";
	  }

  // A method to reset the map
  
  private void resetMap() {
		System.out.println("MAP CHANGED!");
		if (map != null) {
			
			// get the extent of the map before disposing
			e1 = map.getExtent();

			// remove all layers
			for (int i = (this.map.getLayers().size() - 1) ; i == 0 ; i--) {
				this.map.getLayers().remove(i);					
			}
			
			// dispose data resources
			this.map.dispose(); 
			
			// remove map from content pane
			this.window.remove(map);

		}
		
		// re-create the map
		this.map = createMap();
		
	    this.map.addMapOverlay(scaleBar);
    
		// add new map to the content pane
		this.window.add(map, BorderLayout.CENTER);
		
		// Revalidate the map to perform a hierarchical refresh of all components
		this.map.revalidate();
	
		// Set the extent of the previous map
		this.map.setExtent(e1);
	}
	
// A method to create a new map
  
	private JMap createMap() {
		JMap jMap = new JMap();
		
		// give an incremental name
		jMap.setName("map " + count ++);
		
		map.setFullExtent(map.getExtent());
		
		return jMap;
	}

	
  /**
   * Starting point of this application.
   * @param args
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        try {
          Tay_FP application = new Tay_FP();
          application.window.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}
