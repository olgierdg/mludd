#!/usr/bin/python

import numpy as np
import pandas as pd
import scipy.cluster.vq as cluster
import matplotlib.pyplot as plt

from plot import *
from data import *
from kmeans import *
from interp import *

dt = load_data_from_db('../andlogger.db')
dataLat = np.array(dt[['timestamp', 'latitude']])
dataLon = np.array(dt[['timestamp', 'longitude']])

#data = np.array(dt[['latitude', 'longitude']])
#print data
#scatter(data['latitude'], data['longitude'])
#show()

interpDataLonLat = interpolation(dataLat, dataLon)
interpDataDF = interpData_to_dataframe(interpDataLonLat)
interpData = np.array(interpDataDF[['latitude', 'longitude']])
#print interpData

## k-means
k = jump_method(interpData, visualize = True)
centroids, idx = cluster.kmeans2(interpData, k, minit = 'points')

plt.figure();
plot_data(interpData, idx, centroids)
plt.xlabel("E")
plt.ylabel("N")
plt.show()
