import turicreate as turi
import os

def loglab(path):
  l=lab(path)
  print(path,l)
  return l

def lab(path):
  if 'diamonds' in path:
    return 'diamonds'
  if 'hearts' in path:
    return 'hearts'
  if 'spades' in path:
    return 'spades'
  if 'clubs' in path:
    return 'clubs'
  if 'four' in path:
    return 'four'
  if 'five' in path:
    return 'five'
  if 'eight' in path:
    return 'eight'
  if 'nine' in path:
    return 'nine'
  raise Exception("unrecognized path "+path)

# Load the training/test images from their respective directories
training_data = turi.image_analysis.load_images('/home/denny/Pictures/turicreate/training')

testpath = '/home/denny/Pictures/regionsOut'
#test_data = turi.image_analysis.load_images('/home/denny/Pictures/turicreate/test')
test_data = turi.image_analysis.load_images(testpath)

# Provide the labels for the training dataset
training_data['label'] = training_data['path'].apply(lambda path: loglab(path))
# Train the model for classification using training_data
model = turi.image_classifier.create(
                              training_data, target='label', verbose=True, max_iterations=40)
# Use the model to predict the class of the test_data
predictions = model.predict(test_data)
# Print the predictions
for image in zip(test_data,predictions):
    type = image[1];
    fpath = image[0]['path']
    print (fpath, type)
    if not os.path.isdir(testpath+"/"+type) :
      os.mkdir(testpath+"/"+type)
    os.rename(fpath, testpath+"/"+type+"/"+os.path.basename(fpath))
