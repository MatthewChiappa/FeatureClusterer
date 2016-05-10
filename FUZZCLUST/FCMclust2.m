function result = FCMclust(data,param)


%data normalization
X=data.X;

f0=param.c;
%checking the parameters given
%default parameters
if exist('param.m')==1, m = param.m;else m = 2;end;
if exist('param.e')==1, e = param.m;else e = 1e-4;end;

[N,n] = size(X);
[Nf0,nf0] = size(f0); 
X1 = ones(N,1);

% Initialize fuzzy partition matrix
rand('state',0)
if max(Nf0,nf0) == 1, 		% only number of cluster given
  c = f0;
  mm = mean(X);             %mean of the data (1,n)
  aa = max(abs(X - ones(N,1)*mm)); %
  v = 2*(ones(c,1)*aa).*(rand(c,n)-0.5) + ones(c,1)*mm;

  tmpX = transpose(X);
  tmpV = transpose(v);
  d = MahalanobisDistance(tmpX,tmpV);
  
  d = (d+1e-10).^(-1/(m-1));
  f0 = (d ./ (sum(d,2)*ones(1,c)));

else
  c = size(f0,2);
  fm = f0.^m; sumf = sum(fm);
  v = (fm'*X)./(sumf'*ones(1,n)); %
end;

f = zeros(N,c);                % partition matrix
iter = 0;                       % iteration counter

% Iterate
while  max(max(f0-f)) > e
  iter = iter + 1;
  f = f0;
  
  % Calculate centers
  fm = f.^m;
  sumf = sum(fm);
  v = (fm'*X)./(sumf'*ones(1,n));

  % distance formula
  % use with less than 4 dimensions
 if N <= 4,
      tmpX = transpose(X);
      tmpV = transpose(v*eye(n));
      d = MahalanobisDistance(tmpX,tmpV);
 else
      for j = 1 : c,
        xv = X - X1*v(j,:);
        d(:,j) = sum((xv*eye(n).*xv),2);
      end;
 end
  distout = sqrt(d);

  J(iter) = sum(sum(f0.*d));
  % Update f0
  d = (d+1e-10).^(-1/(m-1));
  f0 = (d ./ (sum(d,2)*ones(1,c)));
end

fm = f.^m; 
sumf = sum(fm);

% calculate eigenvectors and eigenvalues
V = zeros(c,n);                % eigenvectors
D = V;                          % eigenvalues

% calculate P,V,D,M
for j = 1 : c,                        
    xv = X - ones(N,1)*v(j,:);
    % Calculate covariance matrix
    A = ones(n,1)*fm(:,j)'.*xv'*xv/sumf(j);
    % Calculate eigen values and eigen vectors
    [ev,ed] = eig(A); ed = diag(ed)';
    ev = ev(:,ed == min(ed));
    % Put cluster info in one matrix
    V(j,:) = ev';
    D(j,:) = ed;
end

%results
result.cluster.V = V;
result.cluster.D = D;
result.data.f=f0;
result.data.d=distout;
result.cluster.v=v;
result.iter = iter;
result.cost = J;