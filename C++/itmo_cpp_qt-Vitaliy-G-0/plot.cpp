#include "plot.h"
#include <QSettings>
#include <cmath>

using namespace QtDataVisualization;

Plot::Plot(QWidget *parent)
    : QWidget(parent)
{
    graph = new Q3DSurface();
    dataProxy = new QSurfaceDataProxy();
    series = new QSurface3DSeries(dataProxy);
    graph->addSeries(series);
    graph->activeTheme()->setType(Q3DTheme::ThemeStoneMoss);

    QHBoxLayout *buttonLayout = new QHBoxLayout;
    buttonLayout->addStretch();

    QWidget *container = QWidget::createWindowContainer(graph);
    container->setMinimumSize(800, 600);

    QVBoxLayout *layout = new QVBoxLayout(this);
    layout->addWidget(container);
    layout->addLayout(buttonLayout);
    setLayout(layout);

    QSurfaceFormat format;
    format.setDepthBufferSize(24);
    format.setStencilBufferSize(8);
    format.setVersion(3, 2);
    format.setProfile(QSurfaceFormat::CoreProfile);
    graph->setFormat(format);

    generateData();
}

void Plot::updateSurface(int function)
{
    if (currentFunction == 0)
        gradientFirstFunction = currentGradient;
    else if (currentFunction == 1)
        gradientSecondFunction = currentGradient;

    currentFunction = function;

    if (currentFunction == 0 && gradientFirstFunction != currentGradient)
        updateGradient(gradientFirstFunction);
    else if (currentFunction == 1 && gradientSecondFunction != currentGradient)
        updateGradient(gradientSecondFunction);

    generateData();
}

void Plot::updateGradient(int gradient)
{
    currentGradient = gradient;
    applyCustomGradient();
}

void Plot::updateStep(int step)
{
    sampleCount = step;
    generateData();
}

void Plot::setRangeMin(double min)
{
    xMin = min;
    generateData();
}

void Plot::setRangeMax(double max)
{
    xMax = max;
    generateData();
}

void Plot::generateData()
{
    QSurfaceDataArray *dataArray = new QSurfaceDataArray;

    double stepX = (xMax - xMin) / double(sampleCount - 1);
    double stepZ = (zMax - zMin) / double(sampleCount - 1);

    graph->axisX()->setRange(xMin, xMax);
    graph->axisZ()->setRange(zMin, zMax);

    for (int xIndex = 0; xIndex < sampleCount; ++xIndex)
    {
        double x = xMin + double(xIndex) * 2 * stepX;
        QSurfaceDataRow *newRow = new QSurfaceDataRow;
        for (int zIndex = 0; zIndex < sampleCount; ++zIndex)
        {
            double z = zMin + double(zIndex) * 2 * stepZ;

            QSurfaceDataItem item;
            double y;
            if (currentFunction == 0)
            {
                double distance = std::sqrt(x * x + z * z);
                distance == 0 ? y = 1 : y = std::sin(distance) / distance;
            }
            else if (currentFunction == 1)
            {
                if (x == 0 || z == 0)
                    y = 1;
                else
                    y = std::sin(x)/x * std::sin(z)/z;
            }
            else if (currentFunction == 2)
            {
                // Use your custom function here
                // double y = customFunction(x, z);
            }
            item.setPosition(QVector3D(x, y, z));
            newRow->append(item);
        }
        dataArray->append(newRow);
    }

    dataProxy->resetArray(dataArray);

    applyCustomGradient();
}

void Plot::applyCustomGradient()
{
    QLinearGradient gradient;
    if (currentGradient == 0)
    {
        gradient.setColorAt(0.0, QColor(240, 249, 33));
        gradient.setColorAt(0.3, QColor(248, 149, 64));
        gradient.setColorAt(0.6, QColor(204, 71, 120));
        gradient.setColorAt(0.8, QColor(126, 3, 168));
        gradient.setColorAt(1.0, QColor(13, 8, 135));
    }
    else if (currentGradient == 1)
    {
        gradient.setColorAt(0.0, QColor(253, 231, 37));
        gradient.setColorAt(0.3, QColor(94, 201, 98));
        gradient.setColorAt(0.6, QColor(33, 145, 140));
        gradient.setColorAt(0.8, QColor(59, 82, 139));
        gradient.setColorAt(1.0, QColor(68, 1, 84));
    }

    series->setBaseGradient(gradient);
    series->setColorStyle(Q3DTheme::ColorStyleRangeGradient);
}

int Plot::getCurrentGradient() const
{
    return currentGradient;
}

void Plot::setDisplayGrid(bool display)
{
    Q3DTheme *theme = graph->activeTheme();
    theme->setGridEnabled(display);
}

void Plot::setDisplayLabels(bool display)
{
    graph->activeTheme()->setLabelBackgroundEnabled(display);
    graph->activeTheme()->setLabelTextColor(display ? Qt::white : Qt::transparent);
}

void Plot::setDisplayLabelBorders(bool display)
{
    QPalette palette = graph->activeTheme()->labelBackgroundColor();
    QColor borderColor = display ? Qt::white : QColor(0, 0, 0, 0);
    palette.setColor(QPalette::Window, borderColor);
}

