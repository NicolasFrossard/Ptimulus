GPS:
    grep -h "GPS" *.log | grep -v Started | awk 'BEGIN { FS = "[, ]"; } { print $6","$5","$7 }' > gps.csv
    grep -h "GPS" *.log | grep -v "Started" | awk -f GPSanalysis.awk > GPS.kml

ACCEL:
    grep -h "ACCEL" *.log | awk 'BEGIN { FS = "[, ]"; } { print $6","$7","$8 }' > accel.csv

MAGN:
    grep -h "MAGN" *.log | awk 'BEGIN { FS = "[, ]"; } { print $6","$7","$8 }' > magn.csv