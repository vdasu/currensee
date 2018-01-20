import numpy as np
import cv2
import glob
import re
from matplotlib import pyplot as plt

MIN_MATCH_COUNT = 105
SUM = 0

# img1 = cv2.imread('200_query.jpg',0)          # queryImage
img2 = cv2.imread('200_500_2000_back.jpg',0) # trainImage

for image in glob.glob('./notes/*.jpg'):

	# image_str = re.findall('./notes/\[0-9][a-z]+.jpg', image)
	image_str = image.split('./notes/')[1]
	img1 = cv2.imread(image, 0)
	while True:	# Initiate SIFT detector
		sift = cv2.xfeatures2d.SIFT_create()

		# find the keypoints and descriptors with SIFT
		kp1, des1 = sift.detectAndCompute(img1,None)
		kp2, des2 = sift.detectAndCompute(img2,None)

		FLANN_INDEX_KDTREE = 0
		index_params = dict(algorithm = FLANN_INDEX_KDTREE, trees = 5)
		search_params = dict(checks = 50)

		flann = cv2.FlannBasedMatcher(index_params, search_params)

		matches = flann.knnMatch(des1,des2,k=2)

		# store all the good matches as per Lowe's ratio test.
		good = []
		for m,n in matches:
		    if m.distance < 0.7*n.distance:
		        good.append(m)

		if len(good)>=MIN_MATCH_COUNT:
		    src_pts = np.float32([ kp1[m.queryIdx].pt for m in good ]).reshape(-1,1,2)
		    dst_pts = np.float32([ kp2[m.trainIdx].pt for m in good ]).reshape(-1,1,2)

		    M, mask = cv2.findHomography(src_pts, dst_pts, cv2.RANSAC,5.0)
		    matchesMask = mask.ravel().tolist()

		    h,w = img1.shape
		    pts = np.float32([ [0,0],[0,h-1],[w-1,h-1],[w-1,0] ]).reshape(-1,1,2)
		    dst = cv2.perspectiveTransform(pts,M)

		    
		    img2 = cv2.fillConvexPoly(img2,np.int32(dst),255)

		    m = re.compile('.+?(?=_)')
		    SUM += int(re.findall('.+?(?=_)', image_str)[0])

		    print len(good)

		else:
		    print "Not enough matches are found - %d/%d" % (len(good),MIN_MATCH_COUNT)
		    print image_str`
		    FLAG = False
		    break
		    matchesMask = None

		draw_params = dict(matchColor = (0,255,0), # draw matches in green color
		                   singlePointColor = None,
		                   matchesMask = matchesMask, # draw only inliers
		                   flags = 2)

		img3 = cv2.drawMatches(img1,kp1,img2,kp2,good,None,**draw_params)

		plt.imshow(img3, 'gray'),plt.show()

print SUM