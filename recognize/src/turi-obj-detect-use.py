
# run as root
# [V3-771G turi-obj]# pwd
# /root/proj/turi-obj
# rm  turi-obj-detect.py ; cp /home/denny/proj/bee/recognize/src/turi-obj-detect.py turi-obj-detect.py ; python turi-obj-detect.py


import turicreate as tc
import os
#from pathlib import Path

im_path = 'testimg'
loaded_model = tc.load_model('mymodel.model')

sframe = tc.image_analysis.load_images(im_path)
predictions = loaded_model.predict(sframe)

#sframe['annotations'] = sframe['path'].apply(lambda path: annot1)
print(predictions)
print(sframe)
#res = sframe.join(predictions)
#print(res)

#def getPred(path):


#sframe['predictions'] = sframe['path'].apply(lambda path: annot1)


#sframe['image_pred'] = tc.object_detector.util.draw_bounding_boxes(sframe['image'], sframe['predictions'])
#print(sframe)


