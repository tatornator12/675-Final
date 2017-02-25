# 675-Final
Final Project using ArcGIS Runtime SDK for Java.

This map loads a layer from a map service created in GIS server. The toolbar has a file chooser button that, when clicked, pops up a file chooser dialog only displaying shapefiles. When you select the shapefile, it will be added to the map if it's in the same projection. If not, the application will display a message to notify the user that the shapefile cannot be displayed.

The toolbar also includes zoom in, zoom out, and full extent buttons. There is also a toggle button, when clicked, that will load a basemap created in ArcGIS Online. The two basemaps will show the same geographic region even though they are from different sources.

The application also has two input boxes to allow the user to type in two addresses as well as a third input box for buffer distance in miles. The toolbar includes a geoprocess button that will take the two addresses, find their locations on the map, draw a line between the two points, and buffer the line using the buffer distance. The buffered area will clip the counties, resulting in the county names being displayed on a text area on the application.
