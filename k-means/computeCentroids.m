function centroids = computeCentroids(X, idx, K)
%COMPUTECENTROIDS Computes new centroids.
%   centroids = COMPUTECENTROIDS(X, idx, K) Returns new centroids by
%   computing the means of the samples assigned to each centroid.
%   + X - dataset: M x N where M is the number of samples and N is the
%     number of features
%   + idx - vector containing centroid assignments of size M x 1;
%     every value is in range 1:K
%   + K - number of centroids

  [M N] = size(X);
  centroids = zeros(K, N);

  for i = 1:K
    data = X(idx == i, :);
    centroids(i, :) = mean(data);
  endfor
         
endfunction
