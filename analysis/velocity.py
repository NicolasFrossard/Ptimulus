#!/usr/bin/env python

import sys

if len(sys.argv) != 2:
	print "usage: %s <datfile>"
	sys.exit(2)

latlong = ""
fp = open(sys.argv[1],"r")
count = 0
for line in fp:
	cols = line.split(" ")
	if cols[1] != 'gps:':
		continue

	lat, long = cols[2].split(",")
	if not float(lat) and not float(long):
		count = 0
		continue
		
	alt = float(cols[3])
	ts = float(cols[4]) / 1000.0
	if not count:
		start_alt = alt
		start_ts = ts
	count+= 1

	if count == 60:
		vel = (alt - start_alt) / (ts - start_ts)
		print vel
		count = 0

