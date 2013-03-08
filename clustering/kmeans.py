#!/usr/bin/python

import numpy as np
import scipy.cluster.vq as cluster
import scipy.linalg as la
import matplotlib.pyplot as plt

def distortion(data, idx, centroids, gamma = None):
    """Function calculates distortion of the dataset with given centroids.
    
    Arguments:
    - `data`: dataset (numpy array) M x N where M is the number of samples
    and N is the number of features
    - `idx`: vector containing centroid assignments of size M x 1;
    every value is in range 1:K
    - `centroids`: K centroids
    - `gamma`: covariance matrix; default is eye(N)
    """
    M, N = data.shape
    K = len(centroids)

    if gamma is None:
        gamma = np.eye(N)
    cov = np.matrix(la.inv(gamma))

    distortion = 0
    for i in range(M):
        temp = np.matrix(data[i] - centroids[idx[i]])
        distortion += temp * cov * temp.T
    distortion = distortion / (M * N)

    return distortion

def jump_method(data, n = None, Y = None, max_iterations = 10, visualize = False):
    """Calculates optimal number of clusters
    for the given dataset using the jump method.
    Returns optimal K.
    
    Arguments:
    - `data`: dataset (numpy array): M x N where M is the number of samples
    and N is the number of features
    - `n`: range; defaults to sqrt(M)
    - `Y`: the transform power; optional - defaults to N/2
    - `max_iterations`: maximum number of iterations for k-means algorithm.
    - `visualize`: whether to show plots of raw distortion, transformed
    distortion and jump; defaults to false
    """
    M, N = data.shape

    if n is None:
        n = int(np.sqrt(M))
    if Y is None:
        Y = 0.5 * N

    dist = np.zeros(n)
    tf_dist = np.zeros(n + 1)
    jump = np.zeros(n)

    for k in range(1, n + 1):
        centroids, idx = cluster.kmeans2(data, k, minit = 'points', 
                                         iter = max_iterations)
        dist[k - 1] = distortion(data, idx, centroids)
        tf_dist[k] = dist[k - 1]**(-Y)
    
    for i in range(n):
        jump[i] = tf_dist[i + 1] - tf_dist[i]
    
    if visualize:
        plt.figure()
        
        plt.subplot(1, 3, 1)
        plt.plot(dist)
        plt.xlabel("K")
        plt.ylabel("Raw distortion")

        plt.subplot(1, 3, 2)
        plt.plot(tf_dist[1:])
        plt.xlabel("K")
        plt.ylabel("Transformed distortion")

        plt.subplot(1, 3, 3)
        plt.plot(jump)
        plt.xlabel("K")
        plt.ylabel("Jump")
    
        plt.show()

    return np.argmax(jump)
