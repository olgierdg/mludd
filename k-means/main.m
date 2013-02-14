format long;
X = csvread("logs.csv");
X = X(:, 1:2);

figure;
plot(X(:, 1), X(:, 2), 'o');

K = jumpMethod(X, 'maxIterations', 20, 'visualize', true);
[centroids, idx] = runKMeans(X, K, 20);

figure;
plotData(X, idx, centroids);
xlabel("E");
ylabel("N");
% save plot
print -dpng clusters.png
