# usage: grep "GPS" *.log | grep -v "Started" | awk -f analysis.awk > GPS.kml

BEGIN { FS = "[, ]"; print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"\
"<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"\
"  <Document>\n"\
"    <name>Points</name>\n"\
"    <description></description>\n"\
"    <Style id=\"yellowLineGreenPoly\">\n"\
"      <LineStyle>\n"\
"        <color>7f00ffff</color>\n"\
"        <width>4</width>\n"\
"      </LineStyle>\n"\
"      <PolyStyle>\n"\
"        <color>7f00ff00</color>\n"\
"      </PolyStyle>\n"\
"    </Style>\n"\
"    <Placemark>\n"\
"      <name>Absolute Extruded</name>\n"\
"      <description>Transparent green wall with yellow outlines</description>\n"\
"      <styleUrl>#yellowLineGreenPoly</styleUrl>\n"\
"      <LineString>\n"\
"        <extrude>1</extrude>\n"\
"        <tessellate>1</tessellate>\n"\
"        <altitudeMode>clampToGround</altitudeMode>\n"\
"        <coordinates>"; }
{ print $6","$5","$7 }

END { print "</coordinates>\n"\
"      </LineString>\n"\
"    </Placemark>\n"\
"  </Document>\n"\
"</kml>\n"; }
