# run as root
# [V3-771G turi-obj]# pwd
# /root/proj/turi-obj
# rm  turi-obj-detect.py ; cp /home/denny/proj/bee/recognize/src/turi-obj-detect.py turi-obj-detect.py ; python turi-obj-detect.py

import turicreate as tc
import os
#from pathlib import Path

im_path = 'testimg'
loaded_model = tc.load_model('mymodel-6-1000.model')

sframe = tc.image_analysis.load_images(im_path)
predictions = loaded_model.predict(sframe)

sframe['n'] = sframe['path'].apply(lambda path: os.path.basename(path))
sframe = sframe.add_column(predictions, 'predictions')
for x in range(len(predictions)):
  print ("=========", sframe['path'][x], "predictions", predictions[x])
#print(predictions[0])
#print(predictions[1])
#print(sframe)

#sframe['image_pred'] = tc.object_detector.util.draw_bounding_boxes(sframe['image'], sframe['predictions'])
#print(sframe)

sframe.save('testing.sframe')
