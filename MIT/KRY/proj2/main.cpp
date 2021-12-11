#include <iostream>
#include "rsa.h"
#include <cstdlib>
#include <ctime>
#include <cstring>


using namespace std;

int main(int argc, char *argv[]) {


    RsaArgs *rsaParams = new RsaArgs();
    parseRsaArguments(rsaParams, argc, argv);

    // generate RSA params
    if (rsaParams->generate) {

        // compute sizes for primes p and q
        int modulusLength = rsaParams->modulusBitSize;
        int pLength = modulusLength / 2;
        int qLength = modulusLength - pLength;
        int n_iters = 50;
        mpz_t publicModulus;
        mpz_t p;
        mpz_t q;
        mpz_t phi;
        mpz_t e;
        mpz_t d;
        mpz_init(p);
        mpz_init(q);
        mpz_init(publicModulus);
        mpz_init(phi);
        mpz_init(e);
        mpz_init(d);

        //initializeRandom(p, q, pLength, qLength);
        generatePrimes(p, q, modulusLength, n_iters);
        if(modulusLength <= 6){
            if(mpz_cmp(p,q) == 0){
                cerr << "P and Q are same." << endl;
                exit(1);
            }
        }
        getPublicModulus(p, q, publicModulus);
        computePhi(p, q, phi);
        char *phiStr;
        phiStr = mpz_get_str(nullptr, 2, phi);
        int phiLen = strlen(phiStr);
        computeKeys(phi, e, d, phiLen);
        if(mpz_cmp(e,d) == 0){
                cerr << "Error: E and D are same." << endl;
                exit(1);
            }
        gmp_printf("%#Zx %#Zx %#Zx %#Zx %#Zx\n", p, q, publicModulus, e, d);
    }
        // encrypt message
    else if (rsaParams->encrypt) {
        mpz_t publicExp;
        mpz_t publicModulus;
        mpz_t plainMessage;
        mpz_t encryptedMessage;
        mpz_init_set(publicExp, rsaParams->publicExponent);
        mpz_init_set(publicModulus, rsaParams->publicModulus);
        mpz_init_set(plainMessage, rsaParams->openText);
        mpz_init(encryptedMessage);
        encryptMessage(publicExp, publicModulus, plainMessage, encryptedMessage);
        gmp_printf("%#Zx \n", encryptedMessage);

        mpz_clear(publicExp);
        mpz_clear(publicModulus);
        mpz_clear(plainMessage);
        mpz_clear(encryptedMessage);
        mpz_clear(rsaParams->publicExponent);
        mpz_clear(rsaParams->publicModulus);
        mpz_clear(rsaParams->openText);

    }
        // decrypt message
    else if (rsaParams->decrypt) {
        mpz_t privateExp;
        mpz_t publicModulus;
        mpz_t decryptedMessage;
        mpz_t encryptedMessage;
        mpz_init_set(privateExp, rsaParams->privateExponent);
        mpz_init_set(publicModulus, rsaParams->publicModulus);
        mpz_init_set(encryptedMessage, rsaParams->cipherText);
        mpz_init(decryptedMessage);
        decryptMessage(privateExp, publicModulus, encryptedMessage, decryptedMessage);
        gmp_printf("%#Zx \n", decryptedMessage);

        mpz_clear(privateExp);
        mpz_clear(publicModulus);
        mpz_clear(decryptedMessage);
        mpz_clear(encryptedMessage);
        mpz_clear(rsaParams->privateExponent);
        mpz_clear(rsaParams->publicModulus);
        mpz_clear(rsaParams->cipherText);



    }
    // break RSA
    else if (rsaParams->breakRSA){


        mpz_t pubMod; mpz_init(pubMod);
        mpz_t pubExp; mpz_init(pubExp);
        mpz_t originalMessage; mpz_init(originalMessage);
        mpz_t encryptedMessage; mpz_init(encryptedMessage);
        mpz_t pPrime; mpz_init(pPrime);
        mpz_t qPrime; mpz_init(qPrime);
        mpz_t pTemp;
        mpz_t qTemp;
        mpz_t phi;
        mpz_t d;
        mpz_init(pTemp);
        mpz_init(qTemp);
        mpz_init(phi);
        mpz_init(d);
        mpz_init_set(pubMod, rsaParams->publicModulus);
        mpz_init_set(pubExp, rsaParams->publicExponent);
        mpz_init_set(encryptedMessage, rsaParams->cipherText);

        // get first factor using Pollard Rho method
        PollardRhoFactorization(pubMod, pPrime);
        // get second prime
        mpz_div(qPrime, pubMod, pPrime);
        mpz_sub_ui(pTemp, pPrime, 1);
        mpz_sub_ui(qTemp, qPrime, 1);
        mpz_mul(phi, pTemp, qTemp);

        //https://medium.com/asecuritysite-when-bob-met-alice/factorizing-integers-with-the-rho-method-40baae102dd9
        // e^{−1} (mod PHI) (and where (d×e) (mod PHI)=1),
        // we will obtain private key d
        getMultiplicativeInverse(pubExp, phi, d);
        mpz_powm(originalMessage, encryptedMessage, d, pubMod);
        gmp_printf("%#Zx %#Zx %#Zx\n", pPrime, qPrime, originalMessage);


        mpz_clear(pubMod);
        mpz_clear(pubExp);
        mpz_clear(originalMessage);
        mpz_clear(encryptedMessage);
        mpz_clear(pPrime);
        mpz_clear(qPrime);
        mpz_clear(pTemp);
        mpz_clear(qTemp);
        mpz_clear(phi);
        mpz_clear(d);
        mpz_clear(rsaParams->publicExponent);
        mpz_clear(rsaParams->publicModulus);
        mpz_clear(rsaParams->cipherText);

    }

    return 0;


}
