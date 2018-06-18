
# run as root
# [V3-771G turi-obj]# pwd
# /root/proj/turi-obj
# rm  turi-obj-detect.py ; cp /home/denny/proj/bee/recognize/src/turi-obj-detect.py turi-obj-detect.py ; python turi-obj-detect.py


import turicreate as tc
import os
#from pathlib import Path

annot_map={"1.jpg":
[
{'coordinates': {'height': 316, 'width': 168, 'x': 346, 'y': 2792},  'label': '7'},
{'coordinates': {'height': 276, 'width': 161, 'x': 2460, 'y': 2853},  'label': '7'},
{'coordinates': {'height': 289, 'width': 188, 'x': 1263, 'y': 1045},  'label': '7'},
{'coordinates': {'height': 276, 'width': 141, 'x': 2772, 'y': 2214},  'label': '7'},
{'coordinates': {'height': 302, 'width': 195, 'x': 1858, 'y': 4318},  'label': '7'},
{'coordinates': {'height': 316, 'width': 202, 'x': 665, 'y': 1892},  'label': '7'},
{'coordinates': {'height': 296, 'width': 195, 'x': 2100, 'y': 3501},  'label': '7'},
{'coordinates': {'height': 296, 'width': 181, 'x': 1603, 'y': 376},  'label': '7'},
]
,"2.jpg":
[
       {'coordinates': {'height': 249, 'width': 155, 'x': 1623, 'y': 3189},  'label': '7'},
       {'coordinates': {'height': 242, 'width': 175, 'x': 2177, 'y': 719},  'label': '7'},
       {'coordinates': {'height': 276, 'width': 202, 'x': 531, 'y': 2093},  'label': '7'},
       {'coordinates': {'height': 235, 'width': 175, 'x': 1734, 'y': 1099},  'label': '7'},
       {'coordinates': {'height': 255, 'width': 181, 'x': 2073, 'y': 2796},  'label': '7'},
       {'coordinates': {'height': 235, 'width': 188, 'x': 1169, 'y': 3740},  'label': '7'},
       {'coordinates': {'height': 242, 'width': 148, 'x': 2486, 'y': 2412},  'label': '7'},
       {'coordinates': {'height': 249, 'width': 202, 'x': 1048, 'y': 1515},  'label': '7'},
       ]
,"3.jpg":
[
{'coordinates': {'height': 188, 'width': 161, 'x': 2762, 'y': 2547},  'label': '7'},
{'coordinates': {'height': 222, 'width': 148, 'x': 1626, 'y': 1630},  'label': '7'},
{'coordinates': {'height': 222, 'width': 148, 'x': 242, 'y': 2617},  'label': '7'},
{'coordinates': {'height': 208, 'width': 134, 'x': 423, 'y': 1905},  'label': '7'},
{'coordinates': {'height': 202, 'width': 141, 'x': 2664, 'y': 3320},  'label': '7'},
{'coordinates': {'height': 255, 'width': 148, 'x': 1572, 'y': 2292},  'label': '7'},
{'coordinates': {'height': 249, 'width': 175, 'x': 1499, 'y': 2927},  'label': '7'},
{'coordinates': {'height': 269, 'width': 188, 'x': 1378, 'y': 3689},  'label': '7'},
],
"4.jpg":
[
{'coordinates': {'height': 114, 'width': 121, 'x': 961, 'y': 1421},  'label': '7'},
{'coordinates': {'height': 168, 'width': 128, 'x': 1166, 'y': 2759},  'label': '7'},
{'coordinates': {'height': 121, 'width': 121, 'x': 1304, 'y': 2083},  'label': '7'},
{'coordinates': {'height': 161, 'width': 148, 'x': 2930, 'y': 3011},  'label': '7'},
{'coordinates': {'height': 161, 'width': 148, 'x': 1888, 'y': 2594},  'label': '7'},
{'coordinates': {'height': 134, 'width': 134, 'x': 914, 'y': 2359},  'label': '7'},
],
"5.jpg":
[
{'coordinates': {'height': 188, 'width': 121, 'x': 1956, 'y': 2486},  'label': '7'},
{'coordinates': {'height': 148, 'width': 114, 'x': 2187, 'y': 2191},  'label': '7'},
{'coordinates': {'height': 141, 'width': 108, 'x': 1203, 'y': 3545},  'label': '7'},
{'coordinates': {'height': 121, 'width': 101, 'x': 1300, 'y': 2386},  'label': '7'},
{'coordinates': {'height': 134, 'width': 101, 'x': 837, 'y': 1633},  'label': '7'},
{'coordinates': {'height': 141, 'width': 121, 'x': 2816, 'y': 2873},  'label': '7'},
{'coordinates': {'height': 141, 'width': 101, 'x': 1623, 'y': 1650},  'label': '7'},
{'coordinates': {'height': 155, 'width': 101, 'x': 850, 'y': 2671},  'label': '7'},
],
"6.jpg":
[
{'coordinates': {'height': 168, 'width': 114, 'x': 366, 'y': 2631},  'label': '7'},
{'coordinates': {'height': 148, 'width': 121, 'x': 1599, 'y': 1572},  'label': '7'},
{'coordinates': {'height': 161, 'width': 101, 'x': 1898, 'y': 2755},  'label': '7'},
{'coordinates': {'height': 161, 'width': 108, 'x': 302, 'y': 1116},  'label': '7'},
{'coordinates': {'height': 161, 'width': 114, 'x': 904, 'y': 3696},  'label': '7'},
{'coordinates': {'height': 175, 'width': 101, 'x': 2785, 'y': 3609},  'label': '7'},
{'coordinates': {'height': 161, 'width': 141, 'x': 843, 'y': 2251},  'label': '7'},
{'coordinates': {'height': 168, 'width': 108, 'x': 2238, 'y': 2496},  'label': '7'},
],
"7.jpg":
[
{'coordinates': {'height': 101, 'width': 121, 'x': 1915, 'y': 783},  'label': '7'},
{'coordinates': {'height': 81, 'width': 141, 'x': 3303, 'y': 1989},  'label': '7'},
{'coordinates': {'height': 101, 'width': 134, 'x': 3058, 'y': 1374},  'label': '7'},
{'coordinates': {'height': 74, 'width': 128, 'x': 2994, 'y': 2779},  'label': '7'},
{'coordinates': {'height': 121, 'width': 128, 'x': 2792, 'y': 706},  'label': '7'},
{'coordinates': {'height': 101, 'width': 121, 'x': 2365, 'y': 1522},  'label': '7'},
{'coordinates': {'height': 108, 'width': 128, 'x': 2570, 'y': 141},  'label': '7'},
{'coordinates': {'height': 87, 'width': 128, 'x': 2671, 'y': 2160},  'label': '7'},
],
"8.jpg":
[
{'coordinates': {'height': 215, 'width': 121, 'x': 235, 'y': 1129},  'label': '4'},
{'coordinates': {'height': 134, 'width': 208, 'x': 2564, 'y': 2943},  'label': '7'},
{'coordinates': {'height': 128, 'width': 208, 'x': 1872, 'y': 4123},  'label': '7'},
{'coordinates': {'height': 134, 'width': 228, 'x': 1290, 'y': 1841},  'label': '7'},
{'coordinates': {'height': 108, 'width': 215, 'x': 2419, 'y': 2574},  'label': '7'},
{'coordinates': {'height': 121, 'width': 195, 'x': 2476, 'y': 1109},  'label': '7'},
{'coordinates': {'height': 235, 'width': 161, 'x': 961, 'y': 2335},  'label': '4'},
{'coordinates': {'height': 114, 'width': 195, 'x': 1683, 'y': 2376},  'label': '7'},
{'coordinates': {'height': 128, 'width': 228, 'x': 1270, 'y': 3330},  'label': '7'},
{'coordinates': {'height': 128, 'width': 208, 'x': 2859, 'y': 1643},  'label': '7'},
],
"9.jpg":
[
{'coordinates': {'height': 128, 'width': 108, 'x': 417, 'y': 2678},  'label': '4'},
{'coordinates': {'height': 155, 'width': 67, 'x': 1653, 'y': 2100},  'label': '7'},
{'coordinates': {'height': 108, 'width': 94, 'x': 1028, 'y': 2863},  'label': '4'},
{'coordinates': {'height': 134, 'width': 87, 'x': 2040, 'y': 3756},  'label': '4'},
{'coordinates': {'height': 134, 'width': 94, 'x': 2325, 'y': 2164},  'label': '7'},
{'coordinates': {'height': 128, 'width': 101, 'x': 2759, 'y': 2893},  'label': '7'},
{'coordinates': {'height': 134, 'width': 81, 'x': 2137, 'y': 2769},  'label': '7'},
{'coordinates': {'height': 134, 'width': 87, 'x': 1072, 'y': 1976},  'label': '7'},
{'coordinates': {'height': 128, 'width': 94, 'x': 1606, 'y': 3014},  'label': '4'},
{'coordinates': {'height': 114, 'width': 108, 'x': 1519, 'y': 2644},  'label': '7'},
{'coordinates': {'height': 121, 'width': 108, 'x': 470, 'y': 1855},  'label': '7'},
{'coordinates': {'height': 148, 'width': 108, 'x': 921, 'y': 2507},  'label': '7'},
{'coordinates': {'height': 134, 'width': 121, 'x': 1546, 'y': 3555},  'label': '4'},
{'coordinates': {'height': 121, 'width': 87, 'x': 2228, 'y': 3058},  'label': '4'},
{'coordinates': {'height': 134, 'width': 87, 'x': 2644, 'y': 3844},  'label': '4'},
],
"10.jpg":
[
{'coordinates': {'height': 108, 'width': 155, 'x': 2631, 'y': 2910},  'label': '7'},
{'coordinates': {'height': 108, 'width': 148, 'x': 1660, 'y': 1519},  'label': '4'},
{'coordinates': {'height': 94, 'width': 155, 'x': 696, 'y': 3414},  'label': '4'},
{'coordinates': {'height': 94, 'width': 101, 'x': 2651, 'y': 3468},  'label': '4'},
{'coordinates': {'height': 108, 'width': 148, 'x': 1808, 'y': 3837},  'label': '4'},
{'coordinates': {'height': 108, 'width': 141, 'x': 1287, 'y': 2587},  'label': '4'},
{'coordinates': {'height': 114, 'width': 168, 'x': 1361, 'y': 3316},  'label': '7'},
{'coordinates': {'height': 94, 'width': 141, 'x': 2470, 'y': 1310},  'label': '4'},
{'coordinates': {'height': 94, 'width': 128, 'x': 2597, 'y': 2103},  'label': '7'},
{'coordinates': {'height': 101, 'width': 141, 'x': 1394, 'y': 1824},  'label': '4'},
{'coordinates': {'height': 114, 'width': 168, 'x': 1280, 'y': 1119},  'label': '7'},
{'coordinates': {'height': 121, 'width': 168, 'x': 769, 'y': 4267},  'label': '7'},
{'coordinates': {'height': 94, 'width': 141, 'x': 1710, 'y': 2197},  'label': '7'},
{'coordinates': {'height': 94, 'width': 168, 'x': 608, 'y': 1835},  'label': '7'},
{'coordinates': {'height': 134, 'width': 161, 'x': 1714, 'y': 2964},  'label': '7'},
{'coordinates': {'height': 101, 'width': 128, 'x': 769, 'y': 2557},  'label': '4'},
],
"11.jpg":
[
{'coordinates': {'height': 94, 'width': 161, 'x': 3555, 'y': 2016},  'label': '7'},
{'coordinates': {'height': 74, 'width': 134, 'x': 2601, 'y': 360},  'label': '7'},
{'coordinates': {'height': 134, 'width': 114, 'x': 904, 'y': 1028},  'label': '4'},
{'coordinates': {'height': 134, 'width': 108, 'x': 3132, 'y': 786},  'label': '7'},
{'coordinates': {'height': 175, 'width': 108, 'x': 3246, 'y': 1801},  'label': '7'},
{'coordinates': {'height': 108, 'width': 134, 'x': 1828, 'y': 1438},  'label': '4'},
{'coordinates': {'height': 108, 'width': 134, 'x': 2890, 'y': 739},  'label': '7'},
{'coordinates': {'height': 121, 'width': 121, 'x': 1855, 'y': 934},  'label': '7'},
{'coordinates': {'height': 114, 'width': 134, 'x': 2903, 'y': 1583},  'label': '4'},
{'coordinates': {'height': 114, 'width': 148, 'x': 2083, 'y': 1945},  'label': '4'},
{'coordinates': {'height': 101, 'width': 148, 'x': 2628, 'y': 2664},  'label': '7'},
{'coordinates': {'height': 134, 'width': 161, 'x': 981, 'y': 1841},  'label': '4'},
{'coordinates': {'height': 101, 'width': 155, 'x': 1596, 'y': 575},  'label': '7'},
{'coordinates': {'height': 101, 'width': 161, 'x': 3219, 'y': 1300},  'label': '4'},
{'coordinates': {'height': 101, 'width': 148, 'x': 2164, 'y': 1415},  'label': '4'},
],
"12.jpg":
[
{'coordinates': {'height': 108, 'width': 121, 'x': 2076, 'y': 114},  'label': '7'},
{'coordinates': {'height': 155, 'width': 108, 'x': 3105, 'y': 561},  'label': '4'},
{'coordinates': {'height': 108, 'width': 87, 'x': 951, 'y': 1042},  'label': '4'},
{'coordinates': {'height': 161, 'width': 114, 'x': 3666, 'y': 2614},  'label': '7'},
{'coordinates': {'height': 134, 'width': 101, 'x': 1697, 'y': 2412},  'label': '4'},
{'coordinates': {'height': 101, 'width': 101, 'x': 2234, 'y': 837},  'label': '7'},
{'coordinates': {'height': 128, 'width': 81, 'x': 927, 'y': 2174},  'label': '4'},
{'coordinates': {'height': 128, 'width': 101, 'x': 1791, 'y': 2040},  'label': '7'},
{'coordinates': {'height': 101, 'width': 141, 'x': 2853, 'y': 2181},  'label': '4'},
{'coordinates': {'height': 114, 'width': 81, 'x': 1895, 'y': 1092},  'label': '7'},
{'coordinates': {'height': 121, 'width': 101, 'x': 3048, 'y': 1472},  'label': '7'},
{'coordinates': {'height': 101, 'width': 128, 'x': 1932, 'y': 2389},  'label': '4'},
{'coordinates': {'height': 121, 'width': 87, 'x': 1401, 'y': 1714},  'label': '4'},
{'coordinates': {'height': 141, 'width': 114, 'x': 3303, 'y': 1549},  'label': '7'},
{'coordinates': {'height': 128, 'width': 101, 'x': 3847, 'y': 1280},  'label': '4'},
{'coordinates': {'height': 81, 'width': 121, 'x': 1754, 'y': 1021},  'label': '7'},
],
"13.jpg":
[
{'coordinates': {'height': 81, 'width': 134, 'x': 3320, 'y': 168},  'label': '7'},
{'coordinates': {'height': 108, 'width': 87, 'x': 1515, 'y': 1982},  'label': '4'},
{'coordinates': {'height': 114, 'width': 114, 'x': 2026, 'y': 1421},  'label': '4'},
{'coordinates': {'height': 81, 'width': 148, 'x': 3441, 'y': 1976},  'label': '4'},
{'coordinates': {'height': 101, 'width': 114, 'x': 2295, 'y': 165},  'label': '4'},
{'coordinates': {'height': 108, 'width': 94, 'x': 1566, 'y': 948},  'label': '7'},
{'coordinates': {'height': 67, 'width': 148, 'x': 1068, 'y': 2003},  'label': '7'},
{'coordinates': {'height': 94, 'width': 108, 'x': 2950, 'y': 1028},  'label': '7'},
{'coordinates': {'height': 81, 'width': 121, 'x': 2124, 'y': 1035},  'label': '4'},
{'coordinates': {'height': 74, 'width': 128, 'x': 2927, 'y': 2765},  'label': '4'},
{'coordinates': {'height': 108, 'width': 108, 'x': 1277, 'y': 1068},  'label': '7'},
{'coordinates': {'height': 101, 'width': 121, 'x': 2856, 'y': 1798},  'label': '4'},
{'coordinates': {'height': 101, 'width': 81, 'x': 1042, 'y': 252},  'label': '7'},
{'coordinates': {'height': 94, 'width': 128, 'x': 3175, 'y': 1828},  'label': '7'},
{'coordinates': {'height': 94, 'width': 121, 'x': 2379, 'y': 2318},  'label': '4'},
{'coordinates': {'height': 81, 'width': 128, 'x': 3451, 'y': 927},  'label': '7'},
],
"14.jpg":
[
{'coordinates': {'height': 134, 'width': 54, 'x': 27, 'y': 1996},  'label': '7'},
{'coordinates': {'height': 141, 'width': 94, 'x': 524, 'y': 2617},  'label': '7'},
{'coordinates': {'height': 94, 'width': 74, 'x': 635, 'y': 2775},  'label': '7'},
{'coordinates': {'height': 148, 'width': 81, 'x': 1176, 'y': 3454},  'label': '7'},
{'coordinates': {'height': 108, 'width': 87, 'x': 1253, 'y': 1861},  'label': '7'},
{'coordinates': {'height': 108, 'width': 87, 'x': 1744, 'y': 2500},  'label': '7'},
{'coordinates': {'height': 108, 'width': 74, 'x': 1811, 'y': 1821},  'label': '7'},
{'coordinates': {'height': 114, 'width': 94, 'x': 2325, 'y': 2456},  'label': '7'},
{'coordinates': {'height': 108, 'width': 94, 'x': 685, 'y': 1808},  'label': '4'},
{'coordinates': {'height': 101, 'width': 81, 'x': 1149, 'y': 2436},  'label': '4'},
{'coordinates': {'height': 108, 'width': 81, 'x': 1257, 'y': 2748},  'label': '4'},
{'coordinates': {'height': 114, 'width': 87, 'x': 1804, 'y': 3437},  'label': '4'},
{'coordinates': {'height': 114, 'width': 81, 'x': 1861, 'y': 2725},  'label': '4'},
{'coordinates': {'height': 121, 'width': 108, 'x': 2412, 'y': 3420},  'label': '4'},
{'coordinates': {'height': 121, 'width': 81, 'x': 2379, 'y': 1808},  'label': '4'},
{'coordinates': {'height': 121, 'width': 108, 'x': 2883, 'y': 2426},  'label': '4'},
],
"15.jpg":
[
{'coordinates': {'height': 81, 'width': 114, 'x': 1132, 'y': 1055},  'label': '4'},
{'coordinates': {'height': 74, 'width': 121, 'x': 1882, 'y': 736},  'label': '4'},
{'coordinates': {'height': 121, 'width': 101, 'x': 1972, 'y': 531},  'label': '4'},
{'coordinates': {'height': 101, 'width': 108, 'x': 2789, 'y': 803},  'label': '4'},
{'coordinates': {'height': 108, 'width': 101, 'x': 2550, 'y': 1317},  'label': '4'},
{'coordinates': {'height': 108, 'width': 114, 'x': 3431, 'y': 1572},  'label': '4'},
{'coordinates': {'height': 94, 'width': 128, 'x': 2100, 'y': 2406},  'label': '4'},
{'coordinates': {'height': 94, 'width': 121, 'x': 2910, 'y': 2056},  'label': '4'},
{'coordinates': {'height': 108, 'width': 134, 'x': 2312, 'y': 1384},  'label': '7'},
{'coordinates': {'height': 101, 'width': 121, 'x': 1532, 'y': 1771},  'label': '7'},
{'coordinates': {'height': 94, 'width': 128, 'x': 1905, 'y': 2070},  'label': '7'},
{'coordinates': {'height': 94, 'width': 141, 'x': 2671, 'y': 1633},  'label': '7'},
{'coordinates': {'height': 121, 'width': 108, 'x': 3602, 'y': 1640},  'label': '7'},
{'coordinates': {'height': 141, 'width': 94, 'x': 3877, 'y': 2604},  'label': '7'},
{'coordinates': {'height': 108, 'width': 114, 'x': 2315, 'y': 2661},  'label': '7'},
{'coordinates': {'height': 87, 'width': 121, 'x': 3179, 'y': 2429},  'label': '7'},
],
"16.jpg":
[
{'coordinates': {'height': 81, 'width': 94, 'x': 1848, 'y': 349},  'label': '4'},
{'coordinates': {'height': 101, 'width': 67, 'x': 2224, 'y': 931},  'label': '4'},
{'coordinates': {'height': 87, 'width': 114, 'x': 2382, 'y': 1159},  'label': '4'},
{'coordinates': {'height': 101, 'width': 141, 'x': 3021, 'y': 702},  'label': '4'},
{'coordinates': {'height': 74, 'width': 87, 'x': 2369, 'y': 628},  'label': '4'},
{'coordinates': {'height': 87, 'width': 108, 'x': 3011, 'y': 158},  'label': '4'},
{'coordinates': {'height': 81, 'width': 121, 'x': 2399, 'y': 2796},  'label': '4'},
{'coordinates': {'height': 81, 'width': 108, 'x': 3071, 'y': 2399},  'label': '4'},
{'coordinates': {'height': 108, 'width': 74, 'x': 1845, 'y': 1109},  'label': '7'},
{'coordinates': {'height': 121, 'width': 87, 'x': 2241, 'y': 1740},  'label': '7'},
{'coordinates': {'height': 87, 'width': 114, 'x': 2436, 'y': 1704},  'label': '7'},
{'coordinates': {'height': 87, 'width': 114, 'x': 3108, 'y': 1267},  'label': '7'},
{'coordinates': {'height': 101, 'width': 74, 'x': 1845, 'y': 1932},  'label': '7'},
{'coordinates': {'height': 108, 'width': 67, 'x': 2238, 'y': 2607},  'label': '7'},
{'coordinates': {'height': 114, 'width': 121, 'x': 2399, 'y': 2248},  'label': '7'},
{'coordinates': {'height': 101, 'width': 134, 'x': 3058, 'y': 1831},  'label': '7'},
],
"17.jpg":
[
{'coordinates': {'height': 114, 'width': 94, 'x': 1304, 'y': 581},  'label': '4'},
{'coordinates': {'height': 108, 'width': 81, 'x': 1962, 'y': 1095},  'label': '4'},
{'coordinates': {'height': 148, 'width': 101, 'x': 1939, 'y': 457},  'label': '4'},
{'coordinates': {'height': 114, 'width': 81, 'x': 2628, 'y': 964},  'label': '4'},
{'coordinates': {'height': 128, 'width': 94, 'x': 2500, 'y': 205},  'label': '4'},
{'coordinates': {'height': 134, 'width': 81, 'x': 3232, 'y': 712},  'label': '4'},
{'coordinates': {'height': 81, 'width': 141, 'x': 2947, 'y': 2607},  'label': '4'},
{'coordinates': {'height': 101, 'width': 128, 'x': 3545, 'y': 1905},  'label': '4'},
{'coordinates': {'height': 114, 'width': 81, 'x': 1559, 'y': 1448},  'label': '7'},
{'coordinates': {'height': 134, 'width': 114, 'x': 2261, 'y': 1976},  'label': '7'},
{'coordinates': {'height': 128, 'width': 101, 'x': 2187, 'y': 1374},  'label': '7'},
{'coordinates': {'height': 114, 'width': 114, 'x': 2960, 'y': 1898},  'label': '7'},
{'coordinates': {'height': 121, 'width': 108, 'x': 2789, 'y': 1109},  'label': '7'},
{'coordinates': {'height': 141, 'width': 114, 'x': 3592, 'y': 1650},  'label': '7'},
{'coordinates': {'height': 114, 'width': 128, 'x': 1939, 'y': 2564},  'label': '7'},
{'coordinates': {'height': 128, 'width': 128, 'x': 2853, 'y': 2752},  'label': '7'},
]






}

def logannot(path):
  n = os.path.basename(path)
  a=annot(n)
  print(n, path, len(a))
  return a

def annot(n):
  return annot_map[n]
  raise Exception("unrecognized "+n)

#im_path = '/home/denny/Pictures/card-detect/train'
im_path = 'img'

#txt = Path(im_path+'/1.js').read_text()
#print(txt)

sframe = tc.image_analysis.load_images(im_path)

sframe['annotations'] = sframe['path'].apply(lambda path: logannot(path))
print(sframe)

model = tc.object_detector.create(sframe, max_iterations=1000)
model.save('mymodel-17-1000.model')


