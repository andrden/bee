# run as root
# [V3-771G turi-obj]# pwd
# /root/proj/turi-obj
# rm  turi-obj-detect.py ; cp /home/denny/proj/bee/recognize/src/turi-obj-detect.py turi-obj-detect.py ; python turi-obj-detect.py

import turicreate as tc
import os
#from pathlib import Path

data =  tc.SFrame('/home/denny/Downloads/testing.sframe')
data.explore()
