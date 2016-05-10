function C = MinkowskiDistance(A,B,P)    
    C = zeros(size(A,2),size(B,2));
    for i = 1:size(A,2)
        C(i,:) = sum(abs(bsxfun(@minus,B,A(:,i))).^P,1).^(1/P);
    end
end