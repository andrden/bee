
# run as root
# [V3-771G turi-obj]# pwd
# /root/proj/turi-obj
# rm  turi-obj-detect.py ; cp /home/denny/proj/bee/recognize/src/turi-obj-detect.py turi-obj-detect.py ; python turi-obj-detect.py


import turicreate as tc
import os
from pathlib import Path

annot1=[
       {'coordinates': {'height': 260, 'width': 180, 'x': 1600, 'y': 368},  'label': '7'},
       {'coordinates': {'height': 260, 'width': 180, 'x': 1256, 'y': 1032},  'label': '7'},
       {'coordinates': {'height': 260, 'width': 180, 'x': 664, 'y': 1888},  'label': '7'},
       {'coordinates': {'height': 260, 'width': 180, 'x': 344, 'y': 2816},  'label': '7'},

       {'coordinates': {'height': 260, 'width': 180, 'x': 2752, 'y': 2216},  'label': '7'},
       {'coordinates': {'height': 260, 'width': 180, 'x': 2456, 'y': 2856},  'label': '7'},
       {'coordinates': {'height': 260, 'width': 180, 'x': 2104, 'y': 3520},  'label': '7'},
       {'coordinates': {'height': 260, 'width': 180, 'x': 1840, 'y': 4344},  'label': '7'},
       ]
print(annot1)

im_path = '/home/denny/Pictures/card-detect/train'

#txt = Path(im_path+'/1.js').read_text()
#print(txt)

# Load all images in random order
sframe = tc.image_analysis.load_images(im_path)

sframe['annotations'] = sframe['path'].apply(lambda path: annot1)
print(sframe)

model = tc.object_detector.create(sframe, max_iterations=5)
model.save('mymodel.model')


