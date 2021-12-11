#ifndef PARTICLE_H
#define PARTICLE_H
#include "eigen3/Eigen/Core"
#include <random>

using namespace Eigen;
using namespace std;
// define type for boundaries
typedef std::vector<std::tuple<int,int>> bounds_type;

class Particle{
        private:
            RowVector2d position; //2D vector representing particle position
            RowVector2d velocity; // 2D vector representing particle velocity
            RowVector2d pBestPosition; // 2D vector representing best position of particle
            double pBest; // personal best value of particle
            bounds_type boundaries; // boundaries for maximal/minimal allowed position

        public:
            Particle(){}
            void initPosition(mt19937 &generator, std::vector<std::tuple<int,int>> bounds, int dims);
            void initVelocity(mt19937 &generator, double vmax, int dims);
            void initBoundaries(bounds_type bounds);
            void computeFitness(double (*obj_fun)(double,double));
            void setPBest(double fitnessVal);
            double getPBest();
            void setBestPosition(RowVector2d &position);
            void setPosition(RowVector2d &position);
            RowVector2d & getBestPosition();
            RowVector2d & getPosition();
            RowVector2d& getVelocity();
            void updatePosition(RowVector2d &velocity);
            void updateVelocity(RowVector2d &newVelocity);

};

#endif // PARTICLE_H
