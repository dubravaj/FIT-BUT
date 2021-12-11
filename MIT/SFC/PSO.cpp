#include <iostream>
#include <eigen3/Eigen/Core>
#include <random>
#include "PSO.h"
#include "Particle.h"

using namespace Eigen;

/*
 * Get number of iterations
 * */
int PSO::getIterations(){
    return this->n_iter;
}

/*
 * Get population
 * */
std::vector<Particle> PSO::getPopulation(){
    return this->population;
}

/*
 * Get current global best position
 * */
RowVector2d PSO::getGlobalBestPosition(){
    return this->globalBestPos;
}

/*
 * Get current global best fitness
 * */
double PSO::getGlobalBestValue(){
    return this->globalBestVal;
}

/* Initialize PSO algorithm parameters
 * param inertia inertia value
 * param cp cognitive coefficient
 * param cg social coefficient
 * param n_particles number of particles in population
 * param n_iters number of iterations
 * param obj_fun pointer to objective function
 * boundaries objective function boundaries
*/
void PSO::init(double inertia, double cp, double cg, double speed, int n_particles, int n_iters, double (*obj_fun)(double, double), bounds_type boundaries){
    this->inertia = inertia;
    this->cp = cp;
    this->cg = cg;
    this->particleSpeed  = speed;
    this->n_particles = n_particles;
    this->n_iter = n_iters;
    this->obj_fun = obj_fun;
    std::random_device rd;
    std::mt19937 gen(rd());
    this->gen = gen;
    std::uniform_real_distribution<double> distribution(0,1);
    this->dist = distribution;
    this->population.clear();
    this->boundaries = boundaries;

}

/*
 * Initialize population of particles
 * param grpah graph for rendering particles
 * */
void PSO::initParticles(QCPGraph *graph, std::vector<std::tuple<double,double>> clickedParticles,bool initRandom){

    random_device rd;
    mt19937 gen(rd());

    // create vector of particles in population
    this->population.reserve(this->n_particles);
    // vectors used for storing particles position for rendering in graph
    QVector<double> xValues;
    QVector<double> yValues;

    // current best fitness values
    double prevBest = 1000000000;
    double currBest = 1000000000;

    //initialize population of particles
    for (int i = 0; i < this->n_particles; i++) {
        //create particle and initialize its position, velocity and fitness
        Particle p;
        if(!initRandom){
            RowVector2d pos;
            pos << std::get<0>(clickedParticles[i]), std::get<1>(clickedParticles[i]) ;
            p.setPosition(pos);
        }
        else{
            p.initPosition(gen, this->boundaries, this->n_dims);
        }
        p.initVelocity(gen, this->particleSpeed, this->n_dims);
        p.initBoundaries(this->boundaries);

        // compute initial fitness, set it ad current best value
        double f = this->obj_fun(p.getPosition()(0), p.getPosition()(1));
        p.setPBest(f);
        p.setBestPosition(p.getPosition());
        currBest = f;
        // update current global best position and fitness
        if (currBest <= prevBest) {
            prevBest = currBest;
            this->globalBestPos = p.getPosition();
        }
        this->population.push_back(p);
        xValues.push_back(p.getPosition()(0));
        yValues.push_back(p.getPosition()(1));
    }
    this->globalBestVal = prevBest;
    if(initRandom){
        graph->setData(xValues,yValues);
    }
}

/*
 * Make one step of algorithm, updates particles in population
 * */
void PSO::updatePopulation(){

    double fitness;
    double rp;
    double rg;

    for (auto &particle: this->population){

        rp = this->dist(this->gen);
        rg = this->dist(this->gen);
        RowVector2d newVelocity = this->inertia* particle.getVelocity() + this->cp*rp*(particle.getBestPosition() - particle.getPosition()) + this->cg*rg*(this->globalBestPos - particle.getPosition());

        particle.updatePosition(newVelocity);
        particle.updateVelocity(newVelocity);

        // compute current value of objective function
        fitness = this->obj_fun(particle.getPosition()(0),particle.getPosition()(1));

        // if particle's value is better than its best known value, update best known value and position
        if(fitness <= particle.getPBest()){
            particle.setPBest(fitness);
            particle.setBestPosition(particle.getPosition());
        }
        // if particle's value is better than global best, update global best known value and position
        if(fitness <= this->globalBestVal){
            this->globalBestVal = fitness;
            this->globalBestPos = particle.getPosition();
        }
    }
}





