#include "psomain.h"
#include <math.h>

#include <random>
#include <iostream>
#include "PSO.h"


#include <QApplication>


using namespace std;

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    PsoMain w;
    w.show();


    return a.exec();
}
