#ifndef PLOT_H
#define PLOT_H

#include <QWidget>
#include <QPushButton>
#include <QtDataVisualization>
#include <QVBoxLayout>

class Plot : public QWidget
{
    Q_OBJECT

public:
    explicit Plot(QWidget *parent = nullptr);

    int getCurrentGradient() const;
    void setRangeMin(double min);
    void setRangeMax(double max);

public slots:
    void updateSurface(int function);
    void updateGradient(int gradient);
    void updateStep(int step);
    void setDisplayGrid(bool display);
    void setDisplayLabels(bool display);
    void setDisplayLabelBorders(bool display);

private:
    QPushButton *sincButton;
    QPushButton *sincxsinczButton;
    QPushButton *customButton;
    QtDataVisualization::Q3DSurface *graph;
    QtDataVisualization::QSurfaceDataProxy *dataProxy;
    QtDataVisualization::QSurface3DSeries *series;
    int sampleCount = 50;
    double xMin = -10.0;
    double xMax = 10.0;
    double zMin = -10.0;
    double zMax = 10.0;
    int currentFunction = 0;
    int currentGradient = 0;
    int gradientFirstFunction = 0;
    int gradientSecondFunction = 0;

    void generateData();
    void applyCustomGradient();
};

#endif // PLOT_H
