function [J, grad] = costFunctionReg(theta, X, y, lambda)
%COSTFUNCTIONREG Compute cost and gradient for logistic regression with regularization
%   J = COSTFUNCTIONREG(theta, X, y, lambda) computes the cost of using
%   theta as the parameter for regularized logistic regression and the
%   gradient of the cost w.r.t. to the parameters. 

% Initialize some useful values
m = length(y); % number of training examples

% You need to return the following variables correctly 
J = 0;
grad = zeros(size(theta));

% ====================== YOUR CODE HERE ======================
% Instructions: Compute the cost of a particular choice of theta.
%               You should set J to the cost.
%               Compute the partial derivatives and set grad to the partial
%               derivatives of the cost w.r.t. each parameter in theta

h = sigmoid(X*theta);

%sum = 0;
%for i=2:size(theta);
%	sum += theta(i)*theta(i);
%end;

J = (1/m)*((log(h))'*-y - (log(1-h))'*(1-y))  + (lambda/(2*m)) * (theta'(2:size(theta))*theta(2:size(theta)));

%J = (1/m)*((log(h))'*-y - (log(1-h))'*(1-y))  + (lambda/(2*m)) * sum;


grad = (1/m) * (X'*(h-y) + lambda*theta);
%grad(1) = grad(1) - (lambda/m)*theta(1);
grad(1) = (1/m) * (X'(1,:)*(h-y));


% =============================================================

end
