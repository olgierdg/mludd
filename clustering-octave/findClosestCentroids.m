function idx = findClosestCentroids(X, centroids)
%FINDCLOSESTCENTROIDS Returns indices of closest centroids.
%   idx = FINDCLOSESTCENTROIDS(X, centroids) Returns a vector of
%   indices: size(idx) = [m 1], where m is the number of samples (size(X, 1)).
%   + X - dataset: samples x features
%   + centroids - centroids in the given dataset

  K = size(centroids, 1);
  M = size(X, 1);
  idx = zeros(M, 1);

  for i = 1:M
    distance = zeros(K, 1);
    
    for j = 1:K
      temp = X(i, :) - centroids(j, :);
      distance(j) = temp * temp';
    endfor
    
    [x ix] = min(distance);
    idx(i) = ix;
  endfor
         
endfunction
