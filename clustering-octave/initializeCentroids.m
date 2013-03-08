function centroids = initializeCentroids(X, K)
%INITIALIZECENTROIDS Creates initial centroids.
%   centroids = INITIALIZECENTROIDS(X, K) Returns initial centroids
%   for:
%   + X - dataset: samples x features
%   + K - number of centroids
  centroids = zeros(K, size(X, 2));

  % Randomly reordered indices
  randidx = randperm(size(X, 1));
  % Centroids are random K samples in X
  centroids = X(randidx(1:K), :);
  
endfunction
