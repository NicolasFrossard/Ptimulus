#!/usr/bin/env python

import sys

kml_template = """<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
  <Document>
    <name>Points</name>
    <description></description>
    <Style id="yellowLineGreenPoly">
      <LineStyle>
        <color>7f00ffff</color>
        <width>4</width>
      </LineStyle>
      <PolyStyle>
        <color>7f00ff00</color>
      </PolyStyle>
    </Style>
    <Placemark>
      <name>Absolute Extruded</name>
      <description>Transparent green wall with yellow outlines</description>
      <styleUrl>#yellowLineGreenPoly</styleUrl>
      <LineString>
        <extrude>1</extrude>
        <tessellate>1</tessellate>
        <altitudeMode>clampToGround</altitudeMode>
        <coordinates>%s</coordinates>
      </LineString>
    </Placemark>
  </Document>
</kml>
"""

if len(sys.argv) != 2:
	print "usage: %s <datfile>"
	sys.exit(2)

latlong = ""
fp = open(sys.argv[1],"r")
for line in fp:
	cols = line.split(" ")
	if cols[1] != 'gps:':
		continue
	alt = cols[3]
	lat, long = cols[2].split(",")
	if not float(lat) and not float(long):
		continue
	latlong+= "%s,%s,%s\n" % (long, lat, alt)

print kml_template % (latlong,)

