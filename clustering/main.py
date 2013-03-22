#!/usr/bin/python

import numpy as np
import scipy.cluster.vq as cluster
import matplotlib.pyplot as plt

from plot import *
from data import *
from kmeans import *
from interp import *

dt = load_data_from_db('../andlogger.db')
dataLa = np.array(dt[['timestamp', 'latitude']])
dataLo = np.array(dt[['timestamp', 'longitude']])

#data = np.array(dt[['latitude', 'longitude']])

#scatter(data['latitude'], data['longitude'])
#show()
interpolate_data(dataLa)
interpolate_data(dataLo)

## k-means
#k = jump_method(data, visualize = True)
#centroids, idx = cluster.kmeans2(data, k, minit = 'points')

#plt.figure();
##plot_data(data, idx, centroids)
#plt.xlabel("E")
#plt.ylabel("N")
#plt.show()
