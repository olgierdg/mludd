function d = distortion(X, idx, centroids, Gamma)
%DISTORTION Function calculates distortion.
%   d = DISTORTION(X, idx, centroids) Extended function description of
%   the dataset X with given K centroids.
%   + X - dataset: M x N where M is the number of samples and N is the
%     number of features
%   + idx - vector containing centroid assignments of size M x 1;
%     every value is in range 1:K
%   + centroids - K centroids
%   + Gamma - covariance matrix; default is eye(N)

  [M N] = size(X);
  K = size(centroids, 1);
  
  if nargin < 4
    Gamma = eye(N);
  endif
  cov = Gamma^(-1);

  d = 0;
  for i = 1:M
    temp = X(i, :) - centroids(idx(i), :);
    d = d + (temp * cov * temp');
  endfor
  d = d / (M * N);
         
endfunction
