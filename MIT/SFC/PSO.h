#ifndef PSO_H
#define PSO_H
#include <eigen3/Eigen/Core>
#include <QObject>
#include "Particle.h"
#include "ui_psomain.h"
#include <random>
#include <iostream>
using namespace Eigen;

// define type for pointer to objective function
typedef double (*fptr)(double,double);
// define type for boundaries
typedef std::vector<std::tuple<int,int>> bounds_type;

class PSO: public QObject{
    Q_OBJECT
    private:
        double inertia; // inertia
        double cp; // cognitive coef
        double cg; // social coef
        double rp; // random number rp
        double rg; // random number rg
        double bestVal;
        RowVector2d bestPos;
        double particleSpeed; // maximal speed of particle
        int n_particles; // number of particles
        int n_dims = 2; //number of dimesions of objective function, only 2D functions are used
        int n_iter; // number of iterations of algorithm
        fptr obj_fun; // objective function to be optimized
        bool initApp;
        std::vector<Particle> population; // population of particles
        std::mt19937 gen; // random number generator
        std::mt19937 normalDistGen; // generator for normal distribution
        std::uniform_real_distribution<double> dist; //normal distribution (0,1)
        bounds_type boundaries; // bondaries range for objective function
        RowVector2d globalBestPos; // global best position
        double globalBestVal; // global best fitness


    public:
        PSO(){};
        void init(double inertia, double cp, double cg,double speed, int n_particles, int n_iters, double (*obj_fun)(double, double), bounds_type boundaries);
        void initParticles(QCPGraph *graph,std::vector<std::tuple<double,double>> clickedParticles, bool initRandom);
        int getIterations();
        std::vector<Particle> getPopulation();
        std::mt19937 getGenerator();
        void updatePopulation();
        double getDistribution();
        RowVector2d getGlobalBestPosition();
        double getGlobalBestValue();

};
#endif // PSO_H
