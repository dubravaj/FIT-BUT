#ifndef PSOMAIN_H
#define PSOMAIN_H

#include <QMainWindow>
#include <QMouseEvent>
#include <QPainter>
#include <QPushButton>
#include <QDoubleSpinBox>
#include "PSO.h"
#include "ui_psomain.h"
namespace Ui { class PsoMain; }


class PsoMain : public QMainWindow
{
    Q_OBJECT
    Ui::PsoMain *ui;  // ui
    PSO psoApp; // PSO instance
    QCPGraph* particlesGraph; // graph for rendering particles
    QCPColorMap *colorMap = nullptr;  // color map to render color map of 2D function
    QCPColorScale *colorScale = nullptr; // color scale
    int currentIteration = 0;
    bool initialized = false;
    bool stop =false;
    int animationSpeed;
    int clickedParticles = 0;
    std::vector<tuple<double,double>> clickedParticlesPos;


public:
    PsoMain(QWidget *parent = nullptr);
    ~PsoMain();
    void plotFunction(std::string functionType, double (*fun)(double,double));

private slots:
    void initClicked();
    void runClicked();
    void stepClicked();
    void selectChanged();
    void stopClicked();
    void resetClicked();
    void setParticle(QMouseEvent *event);
};
#endif // PSOMAIN_H
