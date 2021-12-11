//
// Created by juraj on 15. 4. 2020.
//

#ifndef RSA_RSA_H
#define RSA_RSA_H
#include <cstddef>
#include <gmp.h>


struct RsaArgs{
    bool generate = false;
    bool encrypt = false;
    bool decrypt = false;
    bool breakRSA = false;

    int modulusBitSize; // velkost verejneho modulu v bitoch
    mpz_t publicModulus; // verejny modulus
    mpz_t publicExponent; // verejny exponent, zvycajne = 3
    mpz_t privateExponent; // sukromy exponent
    mpz_t openText; // otvorena sprava
    mpz_t cipherText; // zasifrovana sprava

    RsaArgs(){
        /*mpz_init(this->publicModulus);
        mpz_init(this->publicModulus);
        mpz_init(this->privateExponent);
        mpz_init(this->openText);
        mpz_init(this->cipherText);*/

    }

};

int computeJacobian(mpz_t a, mpz_t n);
void computeBinaryExponentiation(mpz_t base, mpz_t exp, mpz_t mod, mpz_t result);
void parseRsaArguments(RsaArgs *rsa_params, int argc, char *argv[]);
void initializeRandom(mpz_t p, mpz_t q, int pLength, int qLength);
bool checkPrimalitySolovay(mpz_t n, int iters, int length);
void generatePrimes(mpz_t p, mpz_t q, int length,  int iters);
void getPublicModulus(mpz_t p, mpz_t q, mpz_t publicModulus);
void euclidGCD(mpz_t val1, mpz_t val2, mpz_t gcdResult);
void getMultiplicativeInverse(mpz_t val1, mpz_t val2, mpz_t extendedGcdRes);
void Update(mpz_t val1, mpz_t val2, mpz_t quotient);
void computePhi(mpz_t p, mpz_t q, mpz_t phiResult);
void computeKeys(mpz_t phiVal,  mpz_t publicKey, mpz_t privateKey, int length);
void encryptMessage(mpz_t publicExp, mpz_t publicModulus, mpz_t plainMessage, mpz_t encryptedMessage);
void decryptMessage(mpz_t privateExp, mpz_t publicModulus, mpz_t encryptedMessage, mpz_t decryptedMessage);
void PollardRhoFactorization(mpz_t publicModulus, mpz_t factorResult);




#endif //RSA_RSA_H
