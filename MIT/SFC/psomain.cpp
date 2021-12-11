#include "psomain.h"
#include "ui_psomain.h"
#include <math.h>
#include <iostream>
#include "Particle.h"
#include "PSO.h"
#include <unistd.h>
#include <string>

/*
 * Ackley function
 * */
double ackley2(double x, double y){
    return -200 * exp(-0.2 * sqrt(pow(x,2) + pow(y,2)));
}

/*
 * Schaffer function
 * */
double schaffer2(double x, double y){
       double sinVal = pow(sin(pow(pow(x,2) + pow(y,2),2)), 2);

        return 0.5 + ((sinVal - 0.5) / pow(1 + 0.001 * (pow(x,2) + pow(y,2)),2));
}

/*
 * Schwefel function
 * */
double schweffel(double x, double x2){
    int d = 2;
    double sumX = 0.0;
    double sumX2 = 0.0;
    double sum = 0.0;
    sumX = x * sin(sqrt(abs(x)));
    sumX2 = x2 * sin(sqrt(abs(x2)));
    sum = sumX + sumX2;
    return 418.9829 * d - sum;
}




PsoMain::PsoMain(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::PsoMain)
{
    ui->setupUi(this);
    // connect UI buttons to handler functions
    QObject::connect(ui->initButton,SIGNAL(clicked()),this,SLOT(initClicked()));
    QObject::connect(ui->runButton,SIGNAL(clicked()),this,SLOT(runClicked()));
    QObject::connect(ui->stepButton,SIGNAL(clicked()),this,SLOT(stepClicked()));
    QObject::connect(ui->functionSelect,SIGNAL(currentTextChanged(QString)),this,SLOT(selectChanged()));
    QObject::connect(ui->stopButton,SIGNAL(clicked()),this,SLOT(stopClicked()));
    QObject::connect(ui->resetButton,SIGNAL(clicked()),this,SLOT(resetClicked()));
    QObject::connect(ui->customPlot, SIGNAL(mousePress(QMouseEvent*)), this, SLOT(setParticle(QMouseEvent*)));

    // setup plot for displaying 2D color map and particles graph
    ui->customPlot->axisRect()->setupFullAxesBox(true);
    ui->customPlot->xAxis->setLabel("x");
    ui->customPlot->yAxis->setLabel("y");
    this->colorMap = new QCPColorMap(ui->customPlot->xAxis, ui->customPlot->yAxis);
    this->colorMap->data()->clear();
    this->colorScale = new QCPColorScale(ui->customPlot);
    ui->customPlot->plotLayout()->addElement(0, 1, colorScale);
    this->colorScale->setType(QCPAxis::atRight);
    this->colorMap->setColorScale(colorScale);
    ui->customPlot->addGraph(ui->customPlot->xAxis, ui->customPlot->yAxis);

    this->particlesGraph = ui->customPlot->graph(0);
    this->particlesGraph->setAdaptiveSampling(false);
    this->particlesGraph->setLineStyle(QCPGraph::lsNone);
    this->particlesGraph->setScatterStyle(QCPScatterStyle(QCPScatterStyle::ssCircle, Qt::blue, Qt::white, 5));
    ui->currentStatus->setText("<b> Unitialized </b>");


    this->plotFunction("Ackley function",&ackley2);
    this->stop = false;
    this->initialized = false;


}


/*
 * Get Ackley function equation string
 * */
QString Ackley2Equation(){
    QString equation("");
    QString s = QString::fromUtf8("-200 * \u0065 \u207D \u221A");
    QString e = QString::fromUtf8("\u0065");
    QString s2 = QString("<b>Function: </b>: -200 * ");
    QString supLeft = QString("<sup>");
    QString supRight = QString("</sup>");
    QString sqrtStr = QString::fromUtf8("\u221A");
    QString x2 = QString::fromUtf8("x \u00B2");
    QString y2 = QString::fromUtf8("y \u00B2");
    equation = s2 + e + supLeft + "-0.2 *" + sqrtStr + "(" + x2 + " + " + y2 + ")" + supRight;
    return equation;
}

/*
 * Get Schaffer function equation string
 * */
QString Schaffer2Equation(){
    QString equation("");
    QString supLeft = QString("<sup>");
    QString supRight = QString("</sup>");
    QString f = QString("<b>Function: </b>: 0.5 + ");
    QString sinX = QString::fromUtf8("(sin \u00B2 (x \u00B2 + y \u00B2) \u00B2 - 0.5");
    QString divisor = QString::fromUtf8(" / (1 + 0.001 * (x \u00B2 + y \u00B2)) \u00B2)");
    equation = f + sinX + divisor;
    return equation;
}

/*
 * Get Schwefel function equation
 * */
QString SchwefelEquation(){
    QString equation("");
    QString supLeft = QString("<sup>");
    QString supRight = QString("</sup>");
    QString f = QString("<b>Function: </b>: 418.9829 *d -  ");
    QString sum = QString::fromUtf8("\u2211 x<sub>i</sub> * sin(\u221A |x <sub>i</sub>|)");
    QString sinX = QString::fromUtf8("sin \u00B2 (x \u00B2 - y \u00B2) - 0.5");
    QString divisor = QString::fromUtf8(" / (1 + 0.001 * (x \u00B2 + y \u00B2)) \u00B2");
    equation = f + sum;
    return equation;
}

void PsoMain::setParticle(QMouseEvent *event){
    if(ui->particlesRandomInit->isChecked()){
        return;
    }
    if(this->initialized){
        return;
    }
    if(event->button() == Qt::LeftButton)
    {

        QCPAbstractPlottable *plottable =
        ui->customPlot->plottableAt(event->pos());

        if(plottable)
        {
            double x = ui->customPlot->xAxis->pixelToCoord(event->pos().x());
            double y = ui->customPlot->yAxis->pixelToCoord(event->pos().y());
            if(this->clickedParticles < ui->particlesInput->value()){
                this->particlesGraph->addData(x,y);
                this->clickedParticlesPos.push_back(std::make_tuple(x,y));
                this->clickedParticles++;
            }
            ui->customPlot->replot();
        }
    }
}


/*
 * Handler function for initButton click, initializes PSO instance parameters
 * */
void PsoMain::initClicked(){
       this->currentIteration = 0;

        if(ui->particlesClickInit->isChecked()){
            if(this->clickedParticlesPos.size() == 0){
                return;
            }
            if(this->clickedParticlesPos.size() < ui->particlesInput->value()){
                return;
            }
        }


       // if all required parameters are chosen, init PSO instance values
        if( ui->inertiaInput->value() > 0 && ui->c1Input->value() > 0
                && ui->c2Input->value() > 0 && ui->particlesInput->value() > 0 && ui->particlesInput->value() > 0
                && ui->iterationsInput->value() > 0  && ui->speedInput->value() > 0)
        {

            this->animationSpeed = ui->speedSlider->value();
            double inertia = ui->inertiaInput->value();
            double cp = ui->c1Input->value();
            double cg = ui->c2Input->value();
            int iterations = ui->iterationsInput->value();
            int particles = ui->particlesInput->value();
            double speed = ui->speedInput->value();
            double (*obj_fun)(double,double);
            std::string functionType = ui->functionSelect->currentText().toStdString();

            std::vector<std::tuple<int,int>> boundaries;
            std::tuple<int,int> bounds_x;
            std::tuple<int,int> bounds_y;

            // objective function in Ackley function
            if(functionType == "Ackley function"){
                obj_fun = &ackley2;
                ui->functionEquationLabel->setText(Ackley2Equation());

                // boundaries for Ackley function2, typical range for exploring, recommended
                bounds_x = {-32,32};
                bounds_y = {-32,32};

                boundaries.push_back(bounds_x);
                boundaries.push_back(bounds_y);

            }
            else if(functionType == "Schaffer function"){

                ui->functionEquationLabel->setText(Schaffer2Equation());

                obj_fun = &schaffer2;
                // required bounds for exploring function
                bounds_x = {-50,50};
                bounds_y = {-50,50};

                boundaries.push_back(bounds_x);
                boundaries.push_back(bounds_y);
            }
            else if(functionType == "Schwefel function"){
                ui->functionEquationLabel->setText(SchwefelEquation());

                obj_fun = &schweffel;
                bounds_x = {-500,500};
                bounds_y = {-500,500};
                boundaries.push_back(bounds_x);
                boundaries.push_back(bounds_y);
            }

            // initialize PSO parameters
            psoApp.init(inertia,cp,cg,speed,particles,iterations,obj_fun,boundaries);


           if(functionType == "Ackley function"){
                PsoMain::plotFunction(functionType,obj_fun);
            }
            //initialize positions of particles
            bool initRandom = ui->particlesRandomInit->isChecked() ? true : false;
            psoApp.initParticles(this->particlesGraph,this->clickedParticlesPos,initRandom);

            ui->fitnessStatus->clear();
            ui->customPlot->replot();
            this->initialized = true;
            ui->currentStatus->setText("<b>Initialized</b>");
            this->stop = false;
        }
}

void PsoMain::stopClicked(){
       this->stop = true;
       ui->currentStatus->setText("<b>Stopped</b>");
}

void PsoMain::resetClicked(){
    this->initialized = false;
    this->clickedParticlesPos.clear();
    this->clickedParticles = 0;
    ui->customPlot->graph(0)->data()->clear();
    ui->fitnessStatus->clear();
     ui->currentStatus->setText("<b>Unitialized</b>");
    ui->customPlot->replot();
}

/*
 * Handler function for runButton click
 * Run algorithm until specified number of iterations is reached
 * Can be used after stepClick to finish remaining iterations
 * */
void PsoMain::runClicked(){
    if(!this->initialized){
        return;
    }
    ui->initButton->setEnabled(false);
    ui->runButton->setEnabled(false);
    ui->resetButton->setEnabled(false);
    ui->functionSelect->setEnabled(false);
    // positions of particles to be displayed in graph
    QVector<double> xValues;
    QVector<double> yValues;
    QString message;
    ui->currentStatus->setText("<b>Running...</b>");
    // run algorithm until entered number of iteration is reached
    for(int i=this->currentIteration; i< psoApp.getIterations(); i++){
       QCoreApplication::processEvents();
        if(this->stop){
            this->stop = false;
            break;
        }
        psoApp.updatePopulation();
        for(auto &particle: psoApp.getPopulation()){
             xValues.push_back(particle.getPosition()(0));
             yValues.push_back(particle.getPosition()(1));
        }
        particlesGraph->data()->clear();
        particlesGraph->setData(xValues,yValues);
        message = QStringLiteral("Iteration %1, best: X: %2 Y: %3, fitness: %4").arg(this->currentIteration).arg(psoApp.getGlobalBestPosition()(0)).arg(psoApp.getGlobalBestPosition()(1)).arg(psoApp.getGlobalBestValue());
        ui->fitnessStatus->appendPlainText(message);
        ui->customPlot->replot();
        usleep((1000*1000) / this->animationSpeed);
        xValues.clear();
        yValues.clear();
        this->currentIteration++;
    }
     ui->initButton->setEnabled(true);
     ui->runButton->setEnabled(true);
     ui->resetButton->setEnabled(true);
     ui->functionSelect->setEnabled(true);
     ui->currentStatus->setText("<b>Finished</b>");
}


/*
 * Handler for stepButton click
 * Does one iteration of PSO algorithm
 * */
void PsoMain::stepClicked(){
    if(!this->initialized){
        return;
    }
    QVector<double> xValues;
    QVector<double> yValues;
    QString message;
    if(this->currentIteration < psoApp.getIterations()){
            psoApp.updatePopulation();
            for(auto &particle: psoApp.getPopulation()){
                 xValues.push_back(particle.getPosition()(0));
                 yValues.push_back(particle.getPosition()(1));
            }
            particlesGraph->data()->clear();
            particlesGraph->setData(xValues,yValues);
            message = QStringLiteral("Iteration %1, best: X: %2 Y: %3, fitness: %4").arg(this->currentIteration).arg(psoApp.getGlobalBestPosition()(0)).arg(psoApp.getGlobalBestPosition()(1)).arg(psoApp.getGlobalBestValue());
            ui->fitnessStatus->appendPlainText(message);
            ui->customPlot->replot();
            this->currentIteration++;
            xValues.clear();
            yValues.clear();
        }
        ui->currentStatus->setText("<b>Step</b>");
}
/*
 * Handle change of selected function, plot selected function
 *
 * */
void PsoMain::selectChanged(){

    ui->customPlot->graph(0)->data()->clear();
    this->clickedParticlesPos.clear();
    this->clickedParticles = 0;
    /*particlesGraph = new QCPGraph(ui->customPlot->xAxis, ui->customPlot->yAxis);
    particlesGraph->setAdaptiveSampling(false);
    particlesGraph->setLineStyle(QCPGraph::lsNone);
    particlesGraph->setScatterStyle(QCPScatterStyle(QCPScatterStyle::ssCircle, Qt::blue, Qt::white, 5));
*/
    std::string currentFun = ui->functionSelect->currentText().toStdString();
     double (*obj_fun)(double,double);
     if(currentFun == "Ackley function"){
         ui->functionEquationLabel->setText(Ackley2Equation());
         obj_fun = &ackley2;
     }
     else if(currentFun == "Schwefel function"){
         ui->functionEquationLabel->setText(SchwefelEquation());
         obj_fun = &schweffel;
     }
     else if(currentFun == "Schaffer function"){
         ui->functionEquationLabel->setText(Schaffer2Equation());
         obj_fun = &schaffer2;
     }
    PsoMain::plotFunction(currentFun,obj_fun);
    this->initialized =false;
}

/*
 * Plot objective function
 * */
void PsoMain::plotFunction(std::string functionType, double (*fun)(double,double)){

    ui->customPlot->axisRect()->setupFullAxesBox(true);
    ui->customPlot->xAxis->setLabel("x");
    ui->customPlot->yAxis->setLabel("y");
    int nx = 0;
    int ny = 0;
    if(functionType == "Ackley function"){
        nx = 65;
        ny = 65;
        // show for range -32,32 in each dimension
        this->colorMap->data()->setSize(nx, ny);
        this->colorMap->data()->setRange(QCPRange(-32, 32), QCPRange(-32,32));

    }
    else if(functionType == "Schaffer function"){
        nx = 102;
        ny = 102;
        // show for range -50,50 in each dimension
        this->colorMap->data()->setSize(nx, ny);
        this->colorMap->data()->setRange(QCPRange(-50, 50), QCPRange(-50,50));
    }
    else if(functionType == "Schwefel function"){
        nx = 1000;
        ny = 1000;
        // show for range -500,500 in each dimensions
        this->colorMap->data()->setSize(nx, ny);
        this->colorMap->data()->setRange(QCPRange(-500, 500),QCPRange(-500,500));

    }


    // set data points to color map
    double x, y, z;

    for (int xIndex=0; xIndex<nx;  xIndex++)
    {
      for (int yIndex=0; yIndex<ny; yIndex++)
      {

        this->colorMap->data()->cellToCoord(xIndex, yIndex, &x, &y);
         z = fun((int)x,(int)y);
        this->colorMap->data()->setCell(xIndex, yIndex, z);
      }
    }





    // set the color gradient
    this->colorMap->setGradient(QCPColorGradient::gpPolar);
    // rescale the data dimension (color) such that all data points lie in the span visualized by the color gradient
    this->colorMap->rescaleDataRange();

    QCPMarginGroup *marginGroup = new QCPMarginGroup(ui->customPlot);
    ui->customPlot->axisRect()->setMarginGroup(QCP::msBottom|QCP::msTop, marginGroup);
    this->colorScale->setMarginGroup(QCP::msBottom|QCP::msTop, marginGroup);
    // rescale the key (x) and value (y) axes so the whole color map is visible
    ui->customPlot->rescaleAxes();
    ui->customPlot->replot();


}


PsoMain::~PsoMain()
{
    delete ui;
}



