// Second pass of analysis of the GPS to filter and interpolate
// Run with Scilab

gps1 = csvRead("gps1.csv");
accel1 = csvRead("accel1.csv");
magn1 = csvRead("magn1.csv");

t = 1:14514;
alt1 = gps1(:,3);

alt1_filt = alt1;
for i=1:14514,
    if alt1(i) == 0 & i ~= 1 then
        alt1_filt(i) = alt1_filt(i-1);
    end,
end

tmonte = 500:4200;
monte = alt1_filt(500:4200);

tdescente = 7250:9400;
descente = alt1_filt(7250:9400);

[pmon] = polyfit(tmonte,alt1_filt(tmonte),2)
[pdes] = polyfit(tdescente,alt1_filt(tdescente),3)


plot(t, alt1_filt, 'r')
plot(t(1:7000), pmon(1).*t(1:7000).*t(1:7000) + pmon(2) * t(1:7000) + pmon(3))
plot(t(6000:10000), pdes(1).*t(6000:10000).*t(6000:10000).*t(6000:10000) + pdes(2) * t(6000:10000).*t(6000:10000) + pdes(3).*t(6000:10000) + pdes(4))
