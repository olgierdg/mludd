function [centroids, idx] = runKMeans(X, K, maxIterations)
%RUNKMEANS Runs the K-means algorithm.
%   [centroids, idx] = RUNKMEANS(X, initialCentroids) Runs the K-means
%   algorithm on the dataset, where each row is a single sample.
%   Returns computed centroids and a vector containing centroid assignments.
%   + X - dataset: M x N where M is the number of samples and N is the
%     number of features
%   + K - number of centroids
%   + maxIterations - maximum number of iterations. If not specified,
%     algorithm runs until centroids don't change.

  [M N] = size(X);
  centroids = initializeCentroids(X, K);
  prevCentroids = centroids;
  
  if nargin < 3
     maxIterations = 0;
  endif
  i = 1;

  while true
    idx = findClosestCentroids(X, centroids);
    centroids = computeCentroids(X, idx, K);

    % NaNs break the comparison
    if all(prevCentroids == centroids) || i == maxIterations
      break;
    endif
    ++i;
    prevCentroids = centroids;
  endwhile
  
endfunction
