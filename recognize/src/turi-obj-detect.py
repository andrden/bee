
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
{'coordinates': {'height': 121, 'width': 188, 'x': 1680, 'y': 2379},  'label': '7'},
{'coordinates': {'height': 121, 'width': 181, 'x': 2416, 'y': 2574},  'label': '7'},
{'coordinates': {'height': 121, 'width': 181, 'x': 1287, 'y': 1841},  'label': '7'},
{'coordinates': {'height': 121, 'width': 202, 'x': 2493, 'y': 1109},  'label': '7'},
{'coordinates': {'height': 128, 'width': 181, 'x': 2866, 'y': 1643},  'label': '7'},
{'coordinates': {'height': 114, 'width': 188, 'x': 1882, 'y': 4129},  'label': '7'},
{'coordinates': {'height': 128, 'width': 202, 'x': 1284, 'y': 3330},  'label': '7'},
{'coordinates': {'height': 128, 'width': 188, 'x': 2567, 'y': 2953},  'label': '7'},
]


}

def logannot(path):
  n = os.path.basename(path)
  a=annot(n)
  print(n, path, a)
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
model.save('mymodel-8-1000.model')


