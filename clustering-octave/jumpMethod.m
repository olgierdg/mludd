function optK = jumpMethod(X, varargin)
%JUMPMETHOD Calculates optimal number of clusters for the given dataset.
%   optK = JUMPMETHOD(X, n, ...) Calculates optimal number of clusters
%   for the given dataset using the jump method.
%   + X - dataset: M x N where M is the number of samples and N is the
%     number of features
%   Additional parameters (passed as 'param', value pairs):
%   + n - range; defaults to sqrt(M)
%   + Y - the transform power; optional - defaults to N/2
%   + maxIterations - maximum number of iterations for k-means
%   algorithm.
%   + visualize - whether to show plots of raw distortion, transformed
%   distortion and jump; defaults to false

  % default values
  options = struct('n', round(sqrt(size(X, 1))) ,'Y', size(X, 2) / 2, ...
                   'maxIterations', 0, 'visualize', false);
  
  % option names {'Y', 'maxIterations'}
  optionNames = fieldnames(options);

  % count arguments
  nArgs = length(varargin);
  if round(nArgs/2) ~= nArgs/2
    error('Function needs propertyName/propertyValue pairs')
  end

  for pair = reshape(varargin, 2, []) % pair is {propName; propValue}
    inpName = pair{1};
    if any(strcmp(inpName,optionNames))
      % overwrite options
      options.(inpName) = pair{2};
    else
      error('%s is not a recognized parameter name',inpName)
    endif
  endfor

  % function body
  dist = zeros(options.n, 1);
  tfDist = zeros(options.n + 1, 1);
  jump = zeros(options.n, 1);

  for k = 1:options.n
    [centroids, idx] = runKMeans(X, k, options.maxIterations);
    dist(k) = distortion(X, idx, centroids);
    tfDist(k + 1) = dist(k)^(-options.Y);
  endfor
  
  for i = 1:options.n
    jump(i) = tfDist(i + 1) - tfDist(i);
  endfor

  if options.visualize
    figure;

    subplot(1, 3, 1);
    plot(dist);
    xlabel("K");
    ylabel("Raw distortion");

   py subplot(1, 3, 2);
    plot(tfDist(2:end));
    xlabel("K");
    ylabel("Transformed distortion");
    
    subplot(1, 3, 3);
    plot(jump);
    xlabel("K");
    ylabel("Jump");
  endif
  
  [d optK] = max(jump);
         
endfunction
