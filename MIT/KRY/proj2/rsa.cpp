//
// Created by juraj on 15. 4. 2020.
#include <getopt.h>
#include "rsa.h"
#include <iostream>
#include <gmp.h>

using namespace std;

/* parse arguments for RSA*/
void parseRsaArguments(RsaArgs *rsa_params, int argc, char *argv[]){
    int opt;
    int i;
    while((opt = getopt(argc,argv,":g:e:d:b:")) != -1){
        switch(opt){
            case 'g':
                rsa_params->generate = true;
                rsa_params->modulusBitSize = atoi(optarg);
                break;
            case 'e':
                i= optind - 1;
                rsa_params->encrypt = true;
                mpz_init(rsa_params->publicExponent);
                mpz_init(rsa_params->publicModulus);
                mpz_init(rsa_params->openText);
                mpz_set_str(rsa_params->publicExponent, argv[i], 0);
                mpz_set_str(rsa_params->publicModulus, argv[i+1], 0);
                mpz_set_str(rsa_params->openText, argv[i+2], 0);
                break;
            case 'd':
                i = optind - 1;
                rsa_params->decrypt = true;
                mpz_init(rsa_params->privateExponent);
                mpz_init(rsa_params->publicModulus);
                mpz_init(rsa_params->cipherText);
                mpz_set_str(rsa_params->privateExponent, argv[i], 0);
                mpz_set_str(rsa_params->publicModulus, argv[i+1], 0);
                mpz_set_str(rsa_params->cipherText, argv[i+2], 0);
                break;
            case 'b':
                i = optind - 1;
                rsa_params->breakRSA = true;
                mpz_init(rsa_params->publicExponent);
                mpz_init(rsa_params->publicModulus);
                mpz_init(rsa_params->cipherText);
                mpz_set_str(rsa_params->publicExponent, argv[i], 0);
                mpz_set_str(rsa_params->publicModulus, argv[i+1], 0);
                mpz_set_str(rsa_params->cipherText, argv[i+2], 0);
                break;
            default:
                break;
        }
    }
}

/* random initialization of primes p and q */
void initializeRandom(mpz_t p, mpz_t q, int pLength, int qLength){

    gmp_randstate_t randState;
    gmp_randinit_mt(randState);
    gmp_randseed_ui(randState, time(NULL));

    mpz_urandomb(p,randState,pLength);
    mpz_urandomb(q,randState,qLength);

    // set 0 bit, we want odd numbers
    mpz_setbit(p,0);
    mpz_setbit(q,0);

}


// Source: https://www.geeksforgeeks.org/primality-test-set-4-solovay-strassen/?ref=lbp
// compute value for Jacobian symbol
int computeJacobian(mpz_t a, mpz_t n){

    int jacobResult = 1;
    mpz_t temp;
    mpz_t temp2;
    mpz_init(temp);
    mpz_init(temp2);
    mpz_t a_tmp;
    mpz_t n_tmp;
    mpz_init_set(a_tmp,a);
    mpz_init_set(n_tmp,n);


    if(mpz_cmp_ui(a_tmp,0) == 0){
        return 0;
    }

    if(mpz_cmp_ui(a_tmp, 0) < 0){

        mpz_neg(a_tmp,a_tmp);
        mpz_mod_ui(temp,n_tmp,4);
        if(mpz_cmp_ui(temp,3) == 0){
            jacobResult = -jacobResult;
        }
    }

    if(mpz_cmp_ui(a_tmp, 1) == 0){
        return jacobResult;
    }

    while(mpz_cmp_ui(a_tmp,0) != 0){

        if(mpz_cmp_ui(a_tmp,0) < 0){
            mpz_neg(a_tmp,a_tmp);
            mpz_mod_ui(temp,n_tmp, 4);
            if(mpz_cmp_ui(temp, 3) == 0){
                jacobResult = -jacobResult;
            }
        }

        mpz_mod_ui(temp, a_tmp, 2);
        while(mpz_cmp_ui(temp,0) == 0){
            mpz_div_ui(a_tmp, a_tmp, 2);
            mpz_mod_ui(temp, n_tmp, 8);
            if(mpz_cmp_ui(temp,3) == 0 || mpz_cmp_ui(temp, 5) == 0){
                jacobResult = -jacobResult;
            }
            mpz_mod_ui(temp, a_tmp, 2);
        }

        mpz_swap(a_tmp,n_tmp);
        mpz_mod_ui(temp, a_tmp, 4);
        mpz_mod_ui(temp2,n_tmp, 4);
        if(mpz_cmp_ui(temp, 3) == 0 && mpz_cmp_ui(temp2, 3) == 0){
            jacobResult = -jacobResult;
        }
        mpz_mod(a_tmp, a_tmp, n_tmp);
        mpz_div_ui(temp, n_tmp, 2);
        if(mpz_cmp(a_tmp, temp) > 0){
            mpz_sub(a_tmp,a_tmp,n_tmp);
        }



    }

    if(mpz_cmp_ui(n_tmp, 1) == 0){
        return jacobResult;
    }

    mpz_clear(a_tmp);
    mpz_clear(n_tmp);
    mpz_clear(temp);
    mpz_clear(temp2);

    return 0;


}

// Source: https://www.geeksforgeeks.org/primality-test-set-4-solovay-strassen/?ref=lbp
// Calculate binary exponentiation for given base, exponent and modul
void computeBinaryExponentiation(mpz_t base, mpz_t exp, mpz_t mod, mpz_t result){

    mpz_t x;
    mpz_init_set_ui(x, 1);
    mpz_t y;
    mpz_init_set(y, base);
    mpz_t temp;
    mpz_init(temp);
    mpz_t tempExponent;
    mpz_init_set(tempExponent,exp);

    while(mpz_cmp_ui(tempExponent, 0) > 0){
        mpz_mod_ui(temp, tempExponent, 2);
        if(mpz_cmp_ui(temp, 1) == 0){
            mpz_mul(temp,x,y);
            mpz_mod(x,temp,mod);
        }
        mpz_mul(temp,y,y);
        mpz_mod(y,temp,mod);
        mpz_div_ui(tempExponent,tempExponent, 2);
    }

    mpz_mod(result, x, mod);

    mpz_clear(x);
    mpz_clear(y);
    mpz_clear(temp);
    mpz_clear(tempExponent);

}

// Source: https://www.geeksforgeeks.org/primality-test-set-4-solovay-strassen/?ref=lbp
// Check if given number is prime using Solovay-Strassen algorithm
bool checkPrimalitySolovay(mpz_t n, int iters, int length){

    int valLength = length / 2;
    gmp_randstate_t randState;
    gmp_randinit_mt(randState);
    gmp_randseed_ui(randState, time(NULL));

    mpz_t randA;
    mpz_init(randA);
    mpz_t randTemp;
    mpz_init(randTemp);
    mpz_t jacobValue;
    mpz_init(jacobValue);
    mpz_t modValue;
    mpz_init(modValue);
    mpz_t result;
    mpz_init(result);
    mpz_t temp3;
    mpz_init(temp3);

    // n < 2
    if(mpz_cmp_ui(n,2) < 0){
        return false;
    }
    mpz_t temp;
    mpz_init(temp);
    mpz_mod_ui(temp,n, 2);

    if(mpz_cmp_ui(n,2) != 0 && mpz_cmp_ui(temp,0) == 0){
        return false;
    }


    for(int i = 0; i < iters; i++){

        mpz_urandomb(randTemp, randState, valLength);
        mpz_sub_ui(temp, n, 1);
        mpz_mod(randA, randTemp, temp);
        mpz_add_ui(randA, randA, 1);
        int jacobTemp = computeJacobian(randA, n);

        // make value positive because could be negative
        mpz_t jacobTemp2;
        mpz_init(jacobTemp2);
        if(jacobTemp < 0){
            mpz_set_ui(jacobTemp2, -jacobTemp);
            mpz_sub(jacobValue, n, jacobTemp2);
        }
        else{
            mpz_add_ui(jacobValue, n, jacobTemp);
        }
        mpz_mod(jacobValue, jacobValue, n);

        mpz_sub_ui(temp, n, 1);
        mpz_div_ui(temp, temp, 2);

        computeBinaryExponentiation(randA,temp,n, modValue);

        if(mpz_cmp_ui(jacobValue, 0) == 0 || mpz_cmp(modValue, jacobValue) != 0){
            return false;
        }
    }

    mpz_clear(randA);
    mpz_clear(jacobValue);
    mpz_clear(modValue);
    mpz_clear(temp);
    mpz_clear(temp3);
    mpz_clear(randTemp);

    return true;

}

// generate prime numbers
void generatePrimes(mpz_t p, mpz_t q, int length, int iters){

    gmp_randstate_t randState;
    gmp_randinit_mt(randState);
    gmp_randseed_ui(randState, time(NULL));

    int pLength = length / 2;
    int qLength = length - pLength;
    initializeRandom(p,q,pLength,qLength);

    // length of primes and modul, we want it to be certain number of bits
    mpz_t checkBitLengthVal;
    mpz_init(checkBitLengthVal);
    mpz_t validBitLength;
    mpz_init(validBitLength);
    mpz_ui_pow_ui(validBitLength, 2, length - 1);

    bool isPrime = false;
    mpz_t pMaxVal;
    mpz_t qMaxVal;
    mpz_init(pMaxVal);
    mpz_init(qMaxVal);
    mpz_ui_pow_ui(pMaxVal, 2, pLength);
    if(pLength == qLength){
        mpz_ui_pow_ui(qMaxVal, 2, pLength);
    }
    else{
        mpz_ui_pow_ui(qMaxVal, 2 , qLength);
    }


    while(!isPrime){
        isPrime = checkPrimalitySolovay(p,iters, length);
        if(!isPrime){
            // my p is at first odd, we want next possible prime to be odd
            mpz_add_ui(p,p,2);
        }
    }

    isPrime = false;
    while(!isPrime){
        isPrime = checkPrimalitySolovay(q,iters, length);
        if(!isPrime){
            // my q is at first odd, we want next possible prime to be odd
            mpz_add_ui(q,q,2);
        }
    }

    isPrime = false;
    // we got 2 same numbers, but we want 2 different primes

    if(length <= 6){
        if(mpz_cmp(p,q) == 0){
            cerr << "P and Q are the same." << endl;
            exit(1);
        }
    }

    if(mpz_cmp(p,q) == 0){
        // go to next odd number
        mpz_add_ui(q,q,2);
        // get next prime bigger than previously computed
        while(!isPrime){
            isPrime = checkPrimalitySolovay(q,iters,qLength);
            if(!isPrime){
                mpz_add_ui(q,q,2);
            }
        }
    }

    // check if we satisfy bit length for value of public modulus
    mpz_mul(checkBitLengthVal, p, q);

    // if we dont satisfy specific bit length, compute new primes and check then until condition is satisfied
    while(mpz_cmp(checkBitLengthVal,validBitLength) < 0) {

        initializeRandom(p, q, pLength, qLength);

        while (!isPrime) {
            isPrime = checkPrimalitySolovay(p, iters, length);
            if (!isPrime) {
                // my p is at first odd, we want next possible prime to be odd
                mpz_add_ui(p, p, 2);
            }
        }


        isPrime = false;
        while (!isPrime) {
            isPrime = checkPrimalitySolovay(q, iters, length);
            if (!isPrime) {
                // my q is at first odd, we want next possible prime to be odd
                mpz_add_ui(q, q, 2);
            }
        }

        isPrime = false;
        // we got 2 same numbers, but we want 2 different primes
        while(mpz_cmp(p, q) == 0) {
            // go to next odd number
            mpz_add_ui(q, q, 2);
            // get next prime bigger than previously computed
            while (!isPrime) {
                isPrime = checkPrimalitySolovay(q, iters, qLength);
                if (!isPrime) {
                    mpz_add_ui(q, q, 2);
                }
            }
        }

        mpz_mul(checkBitLengthVal, p, q);

        if (mpz_cmp(p, pMaxVal) > 0) {
            mpz_set_ui(checkBitLengthVal, 0);
        }

        if (mpz_cmp(q, qMaxVal) > 0) {
            mpz_set_ui(checkBitLengthVal, 0);
        }


    }

    mpz_clear(checkBitLengthVal);
    mpz_clear(validBitLength);
    mpz_clear(pMaxVal);
    mpz_clear(qMaxVal);


}

/* Get value of public modulus N*/
void getPublicModulus(mpz_t p, mpz_t q, mpz_t publicModulus){
    mpz_mul(publicModulus, p, q);
}

/* Get value of phi*/
void computePhi(mpz_t p, mpz_t q, mpz_t phiResult) {
    mpz_t pTemp;
    mpz_t qTemp;
    mpz_init_set(pTemp, p);
    mpz_init_set(qTemp, q);

    mpz_sub_ui(pTemp, p, 1);
    mpz_sub_ui(qTemp, q, 1);

    mpz_mul(phiResult, pTemp, qTemp);

    mpz_clear(pTemp);
    mpz_clear(qTemp);

}

/* Source: https://en.wikipedia.org/wiki/Euclidean_algorithm */
/* Euclid algortihm for GDC*/
void euclidGCD(mpz_t val1, mpz_t val2, mpz_t gcdResult){

    mpz_t temp;
    mpz_init(temp);
    mpz_t tempVal1;
    mpz_init_set(tempVal1, val1);
    mpz_t tempVal2;
    mpz_init_set(tempVal2, val2);

    while(mpz_cmp_ui(tempVal2, 0) != 0){
        mpz_set(temp,tempVal2);
        mpz_mod(tempVal2, tempVal1, tempVal2);
        mpz_set(tempVal1, temp);
    }
    mpz_set(gcdResult, tempVal1);

    mpz_clear(temp);
    mpz_clear(tempVal1);
    mpz_clear(tempVal2);
}


void Update(mpz_t val1, mpz_t val2, mpz_t quotient){
    mpz_t temp;
    mpz_init(temp);
    mpz_t tempQuotient;
    mpz_init_set(tempQuotient, quotient);

    mpz_set(temp, val1);
    mpz_mul(tempQuotient, quotient, temp);
    mpz_sub(val1, val2, tempQuotient);
    mpz_set(val2, temp);
    mpz_set(val1, val1);

    mpz_clear(temp);
    mpz_clear(tempQuotient);

}

/* Source: https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm */
/* Compute multiplicative inverse using extended Euclidean algorithm */
void getMultiplicativeInverse(mpz_t val1, mpz_t val2, mpz_t extendedGcdRes){
    mpz_t s;
    mpz_t t;
    mpz_t r;
    mpz_t oldS;
    mpz_t oldT;
    mpz_t oldR;
    mpz_t quotient;
    mpz_init_set_ui(s,0);
    mpz_init_set_ui(t,1);
    mpz_init_set(r, val2);
    mpz_init_set_ui(oldS, 1);
    mpz_init_set_ui(oldT, 0);
    mpz_init_set(oldR, val1);
    mpz_init(quotient);
    mpz_t temp;
    mpz_init(temp);
    mpz_t tempQuotient;
    mpz_init(tempQuotient);


    while(mpz_cmp_ui(r, 0) != 0){

        mpz_div(quotient, oldR, r);

        Update(r, oldR, quotient);
        Update(s, oldS, quotient);
        Update(t, oldT, quotient);
    }

    //result is in oldS
    mpz_set(extendedGcdRes, oldS);

    //result could be < 0, we need to convert it back to positive
    // equivalent to oldS mod val2
    // val2 - oldS
    if(mpz_cmp_ui(extendedGcdRes, 0) < 0){
        mpz_neg(extendedGcdRes,extendedGcdRes);
        mpz_sub(extendedGcdRes,val2,extendedGcdRes);
    }


    mpz_clear(s);
    mpz_clear(t);
    mpz_clear(r);
    mpz_clear(oldS);
    mpz_clear(oldT);
    mpz_clear(oldR);
    mpz_clear(quotient);
    mpz_clear(temp);
    mpz_clear(tempQuotient);
}


void computeKeys(mpz_t phiVal, mpz_t publicKey, mpz_t privateKey, int length){

    gmp_randstate_t randState;
    gmp_randinit_mt(randState);
    gmp_randseed_ui(randState, time(NULL));
    mpz_t temp;
    mpz_init(temp);
    mpz_t gcdResult;
    mpz_init(gcdResult);

    bool sameGCD = false;

    while(!sameGCD){

        mpz_urandomb(temp, randState, length);

        while(mpz_cmp_ui(temp, 1) < 0 || mpz_cmp(temp, phiVal) >= 0) {
            mpz_urandomb(temp, randState, length);
        }
        euclidGCD(temp, phiVal, gcdResult);
        // GCD is 1, we found result
        if(mpz_cmp_ui(gcdResult, 1) == 0){
            sameGCD = true;
        }

    }

    // temp value is value of public exponent
    mpz_set(publicKey, temp);
    getMultiplicativeInverse(temp, phiVal, privateKey);

    mpz_clear(temp);
    mpz_clear(gcdResult);
}


// encrypt input message
void encryptMessage(mpz_t publicExp, mpz_t publicModulus, mpz_t plainMessage, mpz_t encryptedMessage){

    mpz_powm(encryptedMessage, plainMessage, publicExp, publicModulus);

}

// decrypt input message
void decryptMessage(mpz_t privateExp, mpz_t publicModulus, mpz_t encryptedMessage, mpz_t decryptedMessage){

    mpz_powm(decryptedMessage, encryptedMessage, privateExp, publicModulus);

}


// https://www.geeksforgeeks.org/pollards-rho-algorithm-prime-factorization/
// use Pollard Rho factorization to find factor of public modulus
void PollardRhoFactorization(mpz_t publicModulus, mpz_t factorResult){

    gmp_randstate_t randState;
    gmp_randinit_mt(randState);
    gmp_randseed_ui(randState, time(NULL));

    mpz_t x;
    mpz_t y;
    mpz_t c;
    mpz_t factor;
    mpz_init(x);
    mpz_init(y);
    mpz_init(c);
    mpz_init(factor);
    mpz_t temp;
    mpz_init(temp);
    mpz_t temp2;
    mpz_init(temp2);
    mpz_t tempAbs;
    mpz_init(tempAbs);
    bool factorFound = false;
    mpz_t exp;
    mpz_init_set_ui(exp, 2);


    mpz_mod_ui(temp, publicModulus, 2);
    // one of the the divisors is 2
    if(mpz_cmp_ui(temp, 0) == 0){
        mpz_set_ui(factorResult, 0);
        return;
    }

    while(!factorFound){

        // randomly create value x between [2, N)
        mpz_urandomm(temp2,randState, publicModulus);
        while(mpz_cmp_ui(temp2, 2) < 0){
            mpz_urandomm(temp2,randState, temp);
        }
        mpz_set(x,temp2);
        mpz_set(y, x);

        // constant c in g(x) = x^2  + c
        // when algorithm fails, g(x) will be used with different c
        // to be able to obtain result
        mpz_sub_ui(temp, publicModulus, 1);
        mpz_urandomm(temp2, randState, temp);
        while(mpz_cmp_ui(temp2,1) < 0){
            mpz_urandomm(temp2, randState, temp);
        }

        mpz_set(c,temp2);
        // set candidate divisor to 1
        mpz_set_ui(factor, 1);

        while(mpz_cmp_ui(factor, 1) == 0){

            /* Tortoise Move: x(i+1) = f(x(i)) */
            computeBinaryExponentiation(x,exp, publicModulus, x);
            mpz_add(x, x, c);
            mpz_add(x, x, publicModulus);
            mpz_mod(x, x, publicModulus);

            /* Hare Move: y(i+1) = f(f(y(i))) */
            computeBinaryExponentiation(y,exp,publicModulus,y);
            mpz_add(y, y, c);
            mpz_add(y,y,publicModulus);
            mpz_mod(y,y, publicModulus);
            computeBinaryExponentiation(y,exp,publicModulus,y);
            mpz_add(y, y, c);
            mpz_add(y,y,publicModulus);
            mpz_mod(y,y, publicModulus);

            /* check gcd of |x-y| and modulus */
            mpz_sub(tempAbs, x, y);
            mpz_abs(tempAbs, tempAbs);
            mpz_gcd(factor,tempAbs, publicModulus);

            // repeat calculation again because factor wasnt found
            if(mpz_cmp(factor, publicModulus) == 0){
                factorFound = false;
                break;

            }
            else{
                mpz_set(factorResult, factor);
                factorFound = true;

            }

        }

    }

    mpz_clear(x);
    mpz_clear(y);
    mpz_clear(c);
    mpz_clear(factor);
    mpz_clear(temp);
    mpz_clear(temp2);
    mpz_clear(tempAbs);

}



