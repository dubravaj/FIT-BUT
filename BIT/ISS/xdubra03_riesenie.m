%1
[Y,Fs] = audioread('xdubra03.wav');
sec = length(Y) / Fs; %16000 vzorkov, 1 sec
%2. 
N = length(Y);
G = 10 * log10(1/N * abs(fft(Y)) .^2);
f = (0:N/2-1)/N*Fs;
G = G(1:N/2);
figure(1);
plot(f,G);
xlabel('f [Hz]');
ylabel('PSD [dB]');
%3
%max by malo byt na 252-1
[mv,pos] = max(G);
%4
b = [0.234 -0.4112 0.2324];
a = [1 0.2289 0.4662];
figure(2);
zplane(b,a);
%5 horna propust
H = freqz(b,a,N,Fs);
f = (0:N-1) / N * Fs/2;
figure(3);
plot(f,abs(H));
xlabel('f [Hz]');
ylabel('|H(f)|');

%6 max 4948 -1
h = filter(b,a,Y);
N = length(h);
G = 10 * log10(1/N * abs(fft(h)) .^2);
f = (0:N/2-1)/N*Fs;
G = G(1:N/2);
figure(4);
plot(f,G);
xlabel('f [Hz]');
ylabel('PSD [dB]');

%7 max je na frekvencii 4946
[maxval,mxpos] = max(G); 

%8 
a =[10,10,10,10,-10,-10,-10,-10];
a = repmat(a,1,40);
[p1,p2] = xcorr(Y,a);
[t1,t2] = max(p1);
r = p2(t2);
disp(Y(r));

%9
size = 50;
R = xcorr(Y,'biased');
k = -size:size;
N = length(Y);
figure(5);
plot(k,R(N-size:N+size));
xlabel('k');
ylabel('Rk');

%10 0.0228
res = R(N+10);
disp(res);

%11,12,13
xmin = min(Y);
xmax = max(Y);
N =30;
x = linspace(xmin,xmax,N);
L= length(x);
N = length(Y);
h = zeros(L,L);
xcol = x(:);
bigx = repmat(xcol,1,N);
yr = Y(:)';
bigy = repmat(Y,1,L)';
[d,ind] =min(abs(bigy - bigx));
for pos=1:N-10,
    ind2(pos) = ind(pos+10);
end
for ii=1:N-10,  
    d1 = ind(ii);
   d2 = ind2(ii);
    h(d1,d2) = h(d1,d2) + 1;
end
surf = ((x(2)-x(1))^2);
p = h / N / surf;
figure(6);
bar3(p);
x = x(:); X1 = repmat(x,1,L);
x = x'; X2 = repmat(x,L,1);
r = sum(sum (X1 .* X2 .* p)) * surf;
check = sum(sum (p)) * surf; 
disp(['hist2: check -- 2d integral should be 1 and is ' num2str(check)]); 