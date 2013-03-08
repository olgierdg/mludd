cluster1 = normrnd(10, 3, 20, 2);
cluster2 = normrnd(30, 5, 15, 2);

X = [cluster1' cluster2']';
K = 2;

figure;
plot(X(:, 1), X(:, 2), 'o');

[centroids, idx] = runKMeans(X, K, 20);

figure;
plotData(X, idx, centroids);

newK = jumpMethod(X, 'maxIterations', 20);
printf("K calculated by jump method: %d\n", newK);
