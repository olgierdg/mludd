#!/usr/bin/python

import matplotlib.pyplot as plt
import matplotlib.colors as colors
import matplotlib.cm as cm

def plot_data(data, idx, centroids):
    """Makes a scatter plot, each cluster with different color.
    
    Arguments:
    - `data`: dataset (numpy array)
    - `idx`: a vector with indexes of centroids
    - `centroids`: centroids
    """
    K = len(centroids)
    norm = colors.Normalize(vmin = 0, vmax = K - 1)
    scalar_map = cm.ScalarMappable(norm = norm, cmap = cm.rainbow)

    plt.scatter(data[:, 0], data[:, 1],
                color = [scalar_map.to_rgba(x) for x in idx])
    plt.scatter(centroids[:, 0], centroids[:, 1], 
                color = [scalar_map.to_rgba(x) for x in range(K)], 
                marker = 'x')
