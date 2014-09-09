# usage: grep -h "GPS" *.log | grep -v "Started" | awk -f analysis.awk > GPS.kml

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
"        <altitudeMode>absolute</altitudeMode>\n"\
"        <coordinates>"; }
{ print $6","$5","round($7) }

END { print "</coordinates>\n"\
"      </LineString>\n"\
"    </Placemark>\n"\
"  </Document>\n"\
"</kml>\n"; }

function round(x, ival, aval, fraction)
{
   ival = int(x)    # integer part, int() truncates

   # see if fractional part
   if (ival == x)   # no fraction
      return ival   # ensure no decimals

   if (x < 0) {
      aval = -x     # absolute value
      ival = int(aval)
      fraction = aval - ival
      if (fraction >= .5)
         return int(x) - 1   # -2.5 --> -3
      else
         return int(x)       # -2.3 --> -2
   } else {
      fraction = x - ival
      if (fraction >= .5)
         return ival + 1
      else
         return ival
   }
}
