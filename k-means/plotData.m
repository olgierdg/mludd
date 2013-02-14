function plotData(X, idx, centroids)
%PLOTDATA Plots data points and centroids.
%   PLOTDATA(X, idx, centroids) Plots data points and centroids,
%   coloring them by centroids.

  K = size(centroids, 1);
         
  % Create palette
  palette = hsv(K + 1);
  colors = palette(idx, :);
  h = !ishold;

  % Plot the data
  if h
    hold on;
  endif
  
  scatter(X(: ,1), X(: ,2), 10, colors);
  scatter(centroids(:, 1), centroids(:, 2), 15, palette(1:(end-1), :), "x");
  
  if h
    hold off;
  endif
  
endfunction
