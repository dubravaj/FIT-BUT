#include <iostream>
#include <random>
#include <eigen3/Eigen/Core>
#include "Particle.h"

using namespace Eigen;
using namespace std;

/*
 * Initialize position of particle
 * param generator generator fo random numbers
 * param bounds range for setting minimal and maximal allowed position
 * param number of dimensions
 * */
void Particle::initPosition(mt19937 &generator, std::vector<std::tuple<int,int>> bounds, int dims) {
    for(auto dim=0; dim < dims; dim++){
        int low = std::get<0>(bounds[dim]);
        int high = std::get<1>(bounds[dim]);
        std::uniform_real_distribution<> dis(low, high);
        this->position(dim) = dis(generator);
    }
}

/*
 * Initialize particle velocity
 * param generator random number generator
 * param vmax maximal velocity, used for both low and high max value
 * param dims number of dimensions
 * */
void Particle::initVelocity(mt19937 &generator, double vmax, int dims) {
    std::uniform_real_distribution<> dis(-vmax,vmax);
    for(auto dim=0; dim < dims; dim++){
        this->velocity(dim) = dis(generator);
    }
}

/*
 *
 * */
void Particle::initBoundaries(bounds_type bounds){
        this->boundaries = bounds;
}


/*
 * Compute fitness of particle based on current position
 * param obj_fun fitness function pointer
 * */
void Particle::computeFitness(double (*obj_fun)(double, double)) {
    this->pBest = obj_fun(this->position(0),this->position(1));
}

/*
 * Set best position of particle
 * param position current best position
 * */
void Particle::setBestPosition(RowVector2d &position) {
    this->pBestPosition = position;
}

/*
 * Get position of particle
 * */
RowVector2d& Particle::getPosition() {
    return this->position;
}

/*
 * Get best position of particle
 * */
RowVector2d& Particle::getBestPosition() {
    return this->pBestPosition;
}
void Particle::setPosition(RowVector2d &position){
    this->position = position;
}

/*
 * Set best value for particle
 * param fitnessVal current best fitness
 * */
void Particle::setPBest(double fitnessVal) {
    this->pBest = fitnessVal;
}

/*
 * Get personal best fitness of particle
 * */
double Particle::getPBest() {
    return this->pBest;
}

/*
 * Get current velocity of particle
 * */
RowVector2d& Particle::getVelocity() {
    return this->velocity;
}

/*
 * Update velocity of particle
 * param newVelocity current velocity of particle
 * */
void Particle::updateVelocity(RowVector2d &newVelocity) {
    this->velocity = newVelocity;
}

/*
 * Update current position of particle
 * param velocity current velocity
 * */
void Particle::updatePosition(RowVector2d &velocity) {
    this->position = this->position + velocity;

    if(this->position(0) > std::get<1>(this->boundaries[0])){
        this->position(0) = std::get<1>(this->boundaries[0]);
    }
    if(this->position(0) < std::get<0>(this->boundaries[0])){
        this->position(0) = std::get<0>(this->boundaries[0]);
    }
    if(this->position(1) > std::get<1>(this->boundaries[0])){
        this->position(1) = std::get<1>(this->boundaries[0]);
    }
    if(this->position(1) < std::get<0>(this->boundaries[0])){
        this->position(1) = std::get<0>(this->boundaries[0]);
    }
}

